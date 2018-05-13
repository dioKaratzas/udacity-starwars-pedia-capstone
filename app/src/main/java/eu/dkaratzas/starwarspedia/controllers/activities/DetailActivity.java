package eu.dkaratzas.starwarspedia.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

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
import eu.dkaratzas.starwarspedia.models.QueryData;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity {

    private AllQueryData mData;
    public static final String EXTRA_DATA_TO_DISPLAY = "extra_data";
    public static final int LOADER_ID = 91;

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
        if (bundle != null && bundle.containsKey(EXTRA_DATA_TO_DISPLAY)) {
            mData = bundle.getParcelable(EXTRA_DATA_TO_DISPLAY);

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            publishUI();

        } else {
            throw new IllegalArgumentException("Must provide a SwapiModel as intent extra to display it's data!");
        }
    }

    private void publishUI() {
        getSupportActionBar().setTitle(mData.getCategory().getString(getApplicationContext()));
        mTvTitle.setText(mData.getTitle());


        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(6));
        GlideApp.with(this)
                .load(mData.getImageStorageReference())
                .placeholder(R.drawable.ic_image_placeholder)
                .apply(requestOptions)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mIvThumb);

        LinkedHashMap<String, String> detailsMap = mData.getDetailsMap();
        StringBuilder details = new StringBuilder();
        for (Map.Entry<String, String> entry : detailsMap.entrySet()) {
            if (entry.getKey().equals(getString(R.string.opening_crawl))) {
                TextView textView = new TextView(this);
                textView.setText(Html.fromHtml(entry.getValue()));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getApplicationContext().getResources().getDimension(R.dimen.text_large));
                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                addCategoryToLinearContainer(entry.getKey(), textView);
            } else
                details.append(String.format("<b>%s:</b> %s<br>", entry.getKey(), entry.getValue()));
        }
        mTvDetails.setText(Html.fromHtml(details.toString()));

        loadAndPublishRelatedToRecyclers();

    }

    private void loadAndPublishRelatedToRecyclers() {
        for (Map.Entry<String, List<QueryData>> entry : mData.getRelatedItems().entrySet()) {
            Timber.d("Publishing recycler for %s entry", entry.getKey());

            RecyclerView recyclerView = new RecyclerView(DetailActivity.this);
            RelatedToAdapter relatedToAdapter = new RelatedToAdapter(getApplicationContext(), entry.getValue(), new RelatedToAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(QueryData queryData) {
                    ApolloManager.instance().fetchSwapiItem(getApplicationContext(), queryData.getId(), queryData.getCategory(), getSupportLoaderManager(), LOADER_ID, new StarWarsApiCallback<AllQueryData>() {
                        @Override
                        public void onResponse(AllQueryData result) {
                            getSupportLoaderManager().destroyLoader(LOADER_ID);
                            if (result == null) {
                                StatusMessage.show(DetailActivity.this, getString(R.string.error_getting_data));
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(DetailActivity.EXTRA_DATA_TO_DISPLAY, result);
                                Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            });
            int viewsMargin = getApplicationContext().getResources().getDimensionPixelSize(R.dimen.margin_large);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            SpacingItemDecoration itemDecoration = new SpacingItemDecoration(viewsMargin);
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
        tvTitle.setTextSize(22);
        tvTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, Misc.dpToPx(24), 0, 0); // first item need extra top margin

        tvTitle.setLayoutParams(lp);

        mLinearContainer.addView(tvTitle);
        mLinearContainer.addView(view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
//            getSupportLoaderManager().destroyLoader(LOADER_ID);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        getSupportLoaderManager().destroyLoader(LOADER_ID);
        super.onBackPressed();
    }
}
