package eu.dkaratzas.starwarspedia.controllers.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.RefWatcher;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.dkaratzas.starwarspedia.GlobalApplication;
import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.adapters.CategoryAdapter;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import eu.dkaratzas.starwarspedia.libs.Misc;
import eu.dkaratzas.starwarspedia.libs.SpacingItemDecoration;
import eu.dkaratzas.starwarspedia.libs.StatusMessage;
import eu.dkaratzas.starwarspedia.libs.animations.YoYo;
import eu.dkaratzas.starwarspedia.libs.animations.techniques.SlideInUpAnimator;
import eu.dkaratzas.starwarspedia.models.CategoryItems;
import eu.dkaratzas.starwarspedia.models.SimpleQueryData;
import eu.dkaratzas.starwarspedia.provider.FavouriteItemsContract;
import timber.log.Timber;

/**
 * Displays the selected category content of the SwapiModel API.
 * Activities that contain this fragment must implement the
 * {@link FavouritesFragmentCallbacks} interface
 * to handle interaction events.
 * Use the {@link FavouritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavouritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.rvCategory)
    RecyclerView mRecyclerView;
    @BindView(R.id.avi)
    AVLoadingIndicatorView mAvi;
    @BindView(R.id.ivRefresh)
    ImageView mIvRefresh;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;

    public static final int LOADER_ID = 89;
    public static final String BUNDLE_DATA_KEY = "favourites_data";
    public static final String BUNDLE_RECYCLER_POSITION = "recycler_position";

    private FavouritesFragmentCallbacks mListener;
    private Unbinder mUnbinder;
    private CategoryItems mFavouriteItems;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CategoryFragment.
     */
    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
    }

    // region Fragment Lifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        mUnbinder = ButterKnife.bind(this, view);


        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_DATA_KEY)) {
            mFavouriteItems = savedInstanceState.getParcelable(BUNDLE_DATA_KEY);

            int position = 0;
            if (savedInstanceState.containsKey(BUNDLE_RECYCLER_POSITION)) {
                position = savedInstanceState.getInt(BUNDLE_RECYCLER_POSITION);
            }

            mTvTitle.setText(getString(R.string.favourites));
            setUpRecycler(position);
        } else {
            setLoadingStatus(false);

            getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }

        mIvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadingStatus(false);
                getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, FavouritesFragment.this);
            }
        });


        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mFavouriteItems != null) {
            outState.putParcelable(BUNDLE_DATA_KEY, mFavouriteItems);

            if (mRecyclerView.getLayoutManager() != null && mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                int[] positions = ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPositions(null);
                outState.putInt(BUNDLE_RECYCLER_POSITION, (positions[0] > 0) ? positions[0] : 0);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FavouritesFragment.FavouritesFragmentCallbacks) {
            mListener = (FavouritesFragment.FavouritesFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnCategoryClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Timber.d("onDestroy");
        RefWatcher refWatcher = GlobalApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    // endregion

    private void setLoadingStatus(boolean loadingStatus) {
        // notify activity about the loadingStatus
        mListener.onFavouriteDataLoading(loadingStatus);

        if (loadingStatus) {
            // show loading indicator
            mAvi.smoothToShow();
        } else {
            // hide loading indicator
            mAvi.hide();
        }
    }

    private void setUpRecycler(int scrollToPosition) {

        if (mFavouriteItems != null) {

            CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), mFavouriteItems, new CategoryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(SimpleQueryData queryData) {
                    mListener.onFavouriteItemClicked(queryData);
                }
            });

            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                    Misc.getHandySpanCount(getContext(), getContext().getResources().getDimensionPixelSize(R.dimen.category_item_preferred_width), getContext().getResources().getDimensionPixelSize(R.dimen.category_recycler_item_offset)),
                    LinearLayoutManager.VERTICAL);

            SpacingItemDecoration itemDecoration = new SpacingItemDecoration(getContext().getResources().getDimensionPixelSize(R.dimen.category_recycler_item_offset));

            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(itemDecoration);
            mRecyclerView.setAdapter(categoryAdapter);

            if (scrollToPosition != 0)
                layoutManager.scrollToPosition(scrollToPosition);

            YoYo.with(new SlideInUpAnimator())
                    .duration(400)
                    .playOn(mRecyclerView);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(getActivity()) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    return getActivity().getContentResolver().query(FavouriteItemsContract.FavouriteItemEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            FavouriteItemsContract.FavouriteItemEntry._ID);

                } catch (Exception e) {
                    Timber.e("Failed to asynchronously load data.");
                    Timber.e(e);
                    Crashlytics.logException(e);
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            mTvTitle.setText(getString(R.string.favourites));
            mapDataAndSetRecycler(data);
        } else {
            StatusMessage.show(getActivity(), getString(R.string.no_favourites_message), false);
        }
        getActivity().getSupportLoaderManager().destroyLoader(LOADER_ID);
        setLoadingStatus(false);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private void mapDataAndSetRecycler(Cursor cursor) {
        List<SimpleQueryData> queryDataList = new ArrayList<>();

        for (int i = 0; i < cursor.getCount(); i++) {
            int swapiIdIndex = cursor.getColumnIndex(FavouriteItemsContract.FavouriteItemEntry.COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(FavouriteItemsContract.FavouriteItemEntry.COLUMN_TITLE);
            int swapiCategoryIndex = cursor.getColumnIndex(FavouriteItemsContract.FavouriteItemEntry.COLUMN_CATEGORY);
            int base64ImageIndex = cursor.getColumnIndex(FavouriteItemsContract.FavouriteItemEntry.COLUMN_IMAGE);

            cursor.moveToPosition(i);
            queryDataList.add(
                    new SimpleQueryData(
                            cursor.getString(swapiIdIndex),
                            cursor.getString(titleIndex),
                            SwapiCategory.values()[cursor.getInt(swapiCategoryIndex)],
                            Misc.base64ToBitmap(cursor.getString(base64ImageIndex))
                    ));
        }
        mFavouriteItems = new CategoryItems(queryDataList);

        setUpRecycler(0);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface FavouritesFragmentCallbacks {
        void onFavouriteItemClicked(SimpleQueryData queryData);

        void onFavouriteDataLoading(boolean loading);
    }

}
