package eu.dkaratzas.starwarspedia.controllers.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import eu.dkaratzas.starwarspedia.libs.Animations;
import eu.dkaratzas.starwarspedia.libs.GridAutofitLayoutManager;
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
    @BindView(R.id.recyclerContainer)
    SwipeRefreshLayout mRecyclerContainer;
    @BindView(R.id.avi)
    AVLoadingIndicatorView mAvi;

    public static final int LOADER_ID = 89;
    private static final String ARG_CATEGORY = "param_category";
    private SwapiCategory mCategory;
    private CategoryFragmentCallbacks mListener;
    private Unbinder unbinder;

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
        unbinder = ButterKnife.bind(this, view);

        loadData();
        return view;
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
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = GlobalApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    private void loadData() {
        mListener.onCategoryDataLoading(true);
        mAvi.show();

        StarWarsApi.getApi().getAllCategoryItems(mCategory)
                .loaderLoad(getContext(), getActivity().getSupportLoaderManager(), LOADER_ID, new StarWarsApiCallback<SwapiModelList<SwapiModel>>() {
                    @Override
                    public void onResponse(SwapiModelList<SwapiModel> result) {
                        getActivity().getSupportLoaderManager().destroyLoader(LOADER_ID);
                        setUpRecycler(result);
                        mListener.onCategoryDataLoading(false);
                        mAvi.smoothToHide();
                    }

                    @Override
                    public void onCancel() {
                        mListener.onCategoryDataLoading(false);
                        mAvi.smoothToHide();
                    }
                });
    }

    private void setUpRecycler(SwapiModelList<SwapiModel> data) {
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), data, new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SwapiModel swapiModel) {
                mListener.onCategoryItemClicked(swapiModel);
            }
        });
        GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(getContext(), (int) getContext().getResources().getDimension(R.dimen.poster_image_width));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.item_offset);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(categoryAdapter);
        Animations.SlideInUpAnimation(mRecyclerView);
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

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }

}
