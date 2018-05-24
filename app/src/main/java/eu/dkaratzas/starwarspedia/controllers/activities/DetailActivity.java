package eu.dkaratzas.starwarspedia.controllers.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.adapters.RelatedToAdapter;
import eu.dkaratzas.starwarspedia.api.ApolloManager;
import eu.dkaratzas.starwarspedia.api.StarWarsApiCallback;
import eu.dkaratzas.starwarspedia.libs.GlideApp;
import eu.dkaratzas.starwarspedia.libs.Misc;
import eu.dkaratzas.starwarspedia.libs.SpacingItemDecoration;
import eu.dkaratzas.starwarspedia.libs.StatusMessage;
import eu.dkaratzas.starwarspedia.models.AllQueryData;
import eu.dkaratzas.starwarspedia.models.SimpleQueryData;
import eu.dkaratzas.starwarspedia.provider.FavouriteItemsContract;
import eu.dkaratzas.starwarspedia.widget.FavouritesWidgetProvider;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_DATA_TO_DISPLAY = "extra_data";
    public static final String EXTRA_CURRENT_CATEGORY_TITLE = "extra_title";
    public static final int LOADER_ID = 91;

    private AllQueryData mData;
    private String mCurrentCategoryTitle;
    private boolean mIsFavourite = false;
    private Menu mMenu;
    private Thread mThread;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.ivThumb)
    ImageView mIvThumb;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tvDetails)
    TextView mTvDetails;
    @BindView(R.id.linearContainer)
    LinearLayout mLinearContainer;
    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(EXTRA_DATA_TO_DISPLAY) && bundle.containsKey(EXTRA_CURRENT_CATEGORY_TITLE)) {
            mData = bundle.getParcelable(EXTRA_DATA_TO_DISPLAY);
            mCurrentCategoryTitle = bundle.getString(EXTRA_CURRENT_CATEGORY_TITLE);

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            mIsFavourite = isFavouriteItem();

            // Wait till image get load
            supportPostponeEnterTransition();

            publishUI();
        } else {
            throw new IllegalArgumentException("Must provide an AllQueryData and Category's Fragment current category as intent extras to display it's data and set the toolbars title.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        getMenuInflater().inflate(R.menu.activity_details_menu, menu);
        switchFavouriteDrawable();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }

    private void publishUI() {
        getSupportActionBar().setTitle(mCurrentCategoryTitle);
        mTvTitle.setText(mData.getTitle());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(6));
        GlideApp.with(this)
                .load(mData.getImageStorageReference())
                .error(R.drawable.ic_image_placeholder)
                .apply(requestOptions)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(mIvThumb);

        // Get item details to display
        LinkedHashMap<String, String> detailsMap = mData.getDetailsMap();
        StringBuilder details = new StringBuilder();

        for (Map.Entry<String, String> entry : detailsMap.entrySet()) {
            // if we display a film display the Opening Crawl in a separate view
            if (entry.getKey().equals(getString(R.string.opening_crawl))) {
                TextView textView = new TextView(this);
                textView.setText(Html.fromHtml(entry.getValue()));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getApplicationContext().getResources().getDimension(R.dimen.text_x_large));
                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));

                addCategoryToLinearContainer(entry.getKey(), textView);
            } else
                details.append(String.format("<b>%s:</b> %s<br>", entry.getKey(), entry.getValue()));
        }
        mTvDetails.setText(Html.fromHtml(details.toString()));

        loadAndPublishRelatedToRecyclers();

    }

    private void loadAndPublishRelatedToRecyclers() {

        for (Map.Entry<String, List<SimpleQueryData>> entry : mData.getRelatedItems().entrySet()) {
            Timber.d("Publishing recycler for %s entry", entry.getKey());

            RecyclerView recyclerView = new RecyclerView(DetailActivity.this);

            RelatedToAdapter relatedToAdapter = new RelatedToAdapter(getApplicationContext(), entry.getValue(), new RelatedToAdapter.OnItemClickListener() {
                // Fetch the item by ID and start DetailsActivity to show its details
                @Override
                public void onItemClick(SimpleQueryData queryData) {

                    ApolloManager.instance().fetchSwapiItem(getApplicationContext(), queryData.getId(), queryData.getCategory(), getSupportLoaderManager(), LOADER_ID, new StarWarsApiCallback<AllQueryData>() {
                        @Override
                        public void onResponse(AllQueryData result) {
                            getSupportLoaderManager().destroyLoader(LOADER_ID);

                            if (result == null) {
                                StatusMessage.show(DetailActivity.this, getString(R.string.error_getting_data));
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(DetailActivity.EXTRA_DATA_TO_DISPLAY, result);
                                bundle.putString(DetailActivity.EXTRA_CURRENT_CATEGORY_TITLE, mCurrentCategoryTitle);
                                Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });

                }
            });

            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            SpacingItemDecoration itemDecoration = new SpacingItemDecoration(getApplicationContext().getResources().getDimensionPixelSize(R.dimen.margin_large));
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(itemDecoration);
            recyclerView.setAdapter(relatedToAdapter);

            addCategoryToLinearContainer(entry.getKey(), recyclerView);

        }

        mScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, 0);
            }
        }, 500);

    }

    private void addCategoryToLinearContainer(String title, View view) {
        TextView tvTitle = new TextView(DetailActivity.this);
        tvTitle.setText(title);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_xx_large));
        tvTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, getResources().getDimensionPixelSize(R.dimen.margin_extra_large), 0, 0);

        tvTitle.setLayoutParams(lp);

        mLinearContainer.addView(tvTitle);
        mLinearContainer.addView(view);
    }

    private boolean isFavouriteItem() {

        final Cursor cursor;
        cursor = getApplicationContext().getContentResolver().query(FavouriteItemsContract.FavouriteItemEntry.CONTENT_URI, null, "swapi_id=?", new String[]{String.valueOf(mData.getId())}, null);

        boolean result = cursor.getCount() > 0;
        cursor.close();

        return result;
    }

    private Bitmap getBitmapFromImageView(ImageView imageView) {
        try {
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
            Bitmap bitmap;
            if (bitmapDrawable == null) {
                imageView.buildDrawingCache();
                bitmap = imageView.getDrawingCache();
                imageView.buildDrawingCache(false);
            } else {
                bitmap = bitmapDrawable.getBitmap();
            }

            return bitmap;
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }

    /**
     * Add or delete the item from favourites
     */
    private void switchFavouriteStatus() {

        if (mThread != null && mThread.isAlive()) {
            return;
        }
        mMenu.getItem(0).setEnabled(false);

        if (mIsFavourite) {

            mThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    Uri uri = FavouriteItemsContract.FavouriteItemEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(String.valueOf(mData.getId())).build();
                    int returnUri = getApplicationContext().getContentResolver().delete(uri, null, null);
                    Timber.d("ReturnUri: %s", returnUri);

                    getApplicationContext().getContentResolver().notifyChange(uri, null);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mIsFavourite = !mIsFavourite;

                            switchFavouriteDrawable();
                            StatusMessage.show(DetailActivity.this, mData.getTitle() + " " + getString(R.string.removed_from_favourite));
                            FavouritesWidgetProvider.notifyWidgetDataChanged(getApplicationContext());
                        }
                    });

                }
            });

            mThread.start();

        } else {

            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final ContentValues contentValues = new ContentValues();
                    contentValues.put(FavouriteItemsContract.FavouriteItemEntry.COLUMN_ID, mData.getId());
                    contentValues.put(FavouriteItemsContract.FavouriteItemEntry.COLUMN_TITLE, mData.getTitle());
                    contentValues.put(FavouriteItemsContract.FavouriteItemEntry.COLUMN_CATEGORY, mData.getCategory().ordinal());

                    String imageBase64 = Misc.bitmapToBase64(getBitmapFromImageView(mIvThumb));
                    contentValues.put(FavouriteItemsContract.FavouriteItemEntry.COLUMN_IMAGE, imageBase64);

                    final Uri uri = getApplicationContext().getContentResolver().insert(FavouriteItemsContract.FavouriteItemEntry.CONTENT_URI, contentValues);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (uri != null) {
                                mIsFavourite = !mIsFavourite;

                                switchFavouriteDrawable();
                                StatusMessage.show(DetailActivity.this, mData.getTitle() + " " + getString(R.string.added_to_favourite));
                                FavouritesWidgetProvider.notifyWidgetDataChanged(getApplicationContext());

                            } else {
                                Timber.d("Uri null");
                            }
                        }
                    });

                }
            });

            mThread.start();

        }
    }

    private void switchFavouriteDrawable() {
        mMenu.getItem(0).setEnabled(true);

        if (mIsFavourite) {
            mMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unfavourite));
        } else {
            mMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favourite));
        }

    }

    private void share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " - " + mData.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mTvDetails.getText());
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportLoaderManager().destroyLoader(LOADER_ID);
                finish();
                return true;
            case R.id.share_action:
                share();
                break;
            case R.id.favourite_action:
                switchFavouriteStatus();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        getSupportLoaderManager().destroyLoader(LOADER_ID);
        super.onBackPressed();
    }
}
