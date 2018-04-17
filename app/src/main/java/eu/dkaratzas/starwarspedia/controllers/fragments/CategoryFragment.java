package eu.dkaratzas.starwarspedia.controllers.fragments;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.leakcanary.RefWatcher;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.dkaratzas.starwarspedia.GlobalApplication;
import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.adapters.CategoryAdapter;
import eu.dkaratzas.starwarspedia.api.StarWarsApi;
import eu.dkaratzas.starwarspedia.api.StarWarsApiCallback;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import eu.dkaratzas.starwarspedia.libs.GridAutofitLayoutManager;
import eu.dkaratzas.starwarspedia.libs.Misc;
import eu.dkaratzas.starwarspedia.libs.animations.YoYo;
import eu.dkaratzas.starwarspedia.libs.animations.techniques.FadeInAnimator;
import eu.dkaratzas.starwarspedia.libs.animations.techniques.PulseAnimator;
import eu.dkaratzas.starwarspedia.libs.animations.techniques.SlideInUpAnimator;
import eu.dkaratzas.starwarspedia.libs.animations.techniques.SlideOutDownAnimator;
import eu.dkaratzas.starwarspedia.models.SwapiModel;
import eu.dkaratzas.starwarspedia.models.SwapiModelList;

/**
 * Displays the selected category content of the SwapiModel API.
 * Activities that contain this fragment must implement the
 * {@link CategoryFragmentCallbacks} interface
 * to handle interaction events.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {
    @BindView(R.id.rvCategory)
    RecyclerView mRecyclerView;
    @BindView(R.id.avi)
    AVLoadingIndicatorView mAvi;
    @BindView(R.id.statusMessageContainer)
    CardView mStatusMessageContainer;
    @BindView(R.id.tvStatusMessage)
    TextView mTvStatusMessage;
    @BindView(R.id.ivRefresh)
    ImageView mIvRefresh;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;

    public static final int LOADER_ID = 89;
    public static final String BUNDLE_DATA_KEY = "categories_data";
    public static final String BUNDLE_RECYCLER_POSITION = "recycler_position";
    private static final String ARG_CATEGORY = "param_category";

    private SwapiCategory mCategory;
    private CategoryFragmentCallbacks mListener;
    private Unbinder mUnbinder;
    private SwapiModelList<SwapiModel> mData;

    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param category Parameter 1.
     * @return A new instance of fragment CategoryFragment.
     */
    public static CategoryFragment newInstance(SwapiCategory category) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY, category.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    // region Fragment Lifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_CATEGORY)) {
            mCategory = SwapiCategory.values()[getArguments().getInt(ARG_CATEGORY)];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        setLoadingStatus(false);

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_DATA_KEY)) {
            mData = savedInstanceState.getParcelable(BUNDLE_DATA_KEY);

            int position = 0;
            if (savedInstanceState.containsKey(BUNDLE_RECYCLER_POSITION)) {
                position = savedInstanceState.getInt(BUNDLE_RECYCLER_POSITION);
            }
            setUpRecycler(position);
        } else {
            loadData();
        }

        mIvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mData != null) {
            outState.putParcelable(BUNDLE_DATA_KEY, mData);

            if (mRecyclerView.getLayoutManager() != null && mRecyclerView.getLayoutManager() instanceof GridAutofitLayoutManager)
                outState.putInt(BUNDLE_RECYCLER_POSITION, ((GridAutofitLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CategoryFragmentCallbacks) {
            mListener = (CategoryFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCategoryClickedListener");
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
        RefWatcher refWatcher = GlobalApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    // endregion

    private void loadData() {
        if (Misc.isNetworkAvailable(getActivity().getApplicationContext())) {
            setLoadingStatus(true);

            StarWarsApi.getApi().getAllCategoryItems(mCategory)
                    .loaderLoad(getContext(), getActivity().getSupportLoaderManager(), LOADER_ID, new StarWarsApiCallback<SwapiModelList<SwapiModel>>() {
                        @Override
                        public void onResponse(SwapiModelList<SwapiModel> result) {
                            if (result == null) {
                                showStatus(getString(R.string.error_getting_data));
                            } else {
                                mTvTitle.setText(mCategory.toString(getContext()));
                            }

                            mData = result;
                            getActivity().getSupportLoaderManager().destroyLoader(LOADER_ID);
                            setUpRecycler(0);
                            setLoadingStatus(false);
                        }

                        @Override
                        public void onCancel() {
                            mListener.onCategoryDataLoading(false);
                            mAvi.smoothToHide();
                        }
                    });
        } else {
            showStatus(getResources().getString(R.string.no_internet));
        }

    }

    private void setLoadingStatus(boolean loadingStatus) {
        // notify activity about the loadingStatus
        mListener.onCategoryDataLoading(loadingStatus);

        if (loadingStatus) {
            // hide status message if is visible
            hideStatus(0);

            // show loading indicator
            mAvi.smoothToShow();
        } else {
            // hide loading indicator
            mAvi.hide();
        }

        if (mData == null && !loadingStatus)
            // if data failed to load show the refresh button
            YoYo.with(new FadeInAnimator())
                    .duration(300)
                    .onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            YoYo.with(new PulseAnimator())
                                    .repeat(3)
                                    .playOn(mIvRefresh);
                        }
                    })
                    .playOn(mIvRefresh);
        else if (loadingStatus)
            // else hide it
            mIvRefresh.setVisibility(View.GONE);
    }

    private void setUpRecycler(int position) {
        if (mData != null) {
            CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), mData, new CategoryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(SwapiModel swapiModel) {
                    mListener.onCategoryItemClicked(swapiModel);
                }
            });
            GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(getContext(), getContext().getResources().getDimensionPixelSize(R.dimen.thumb_image_height));
            ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext().getResources().getDimensionPixelSize(R.dimen.category_recycler_item_offset));
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(itemDecoration);
            mRecyclerView.setAdapter(categoryAdapter);

            if (position != 0)
                layoutManager.scrollToPosition(position);

            YoYo.with(new SlideInUpAnimator())
                    .duration(400)
                    .playOn(mRecyclerView);
        }
    }

    private void showStatus(String message) {
        mTvStatusMessage.setText(message);

        YoYo.with(new SlideInUpAnimator())
                .duration(200)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        hideStatus(4000);
                    }
                })
                .playOn(mStatusMessageContainer);
    }

    private void hideStatus(int delay) {
        YoYo.with(new SlideOutDownAnimator())
                .duration(200)
                .delay(delay)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        mStatusMessageContainer.setVisibility(View.GONE);
                    }
                })
                .playOn(mStatusMessageContainer);
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
    public interface CategoryFragmentCallbacks {
        void onCategoryItemClicked(SwapiModel swapiModel);

        void onCategoryDataLoading(boolean loading);
    }

    private class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }

}
