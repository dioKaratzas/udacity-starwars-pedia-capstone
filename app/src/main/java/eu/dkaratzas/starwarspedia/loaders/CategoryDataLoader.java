package eu.dkaratzas.starwarspedia.loaders;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import eu.dkaratzas.starwarspedia.api.StarWarsApi;
import eu.dkaratzas.starwarspedia.api.StarWarsApiCallback;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import eu.dkaratzas.starwarspedia.models.SwapiModel;
import eu.dkaratzas.starwarspedia.models.SwapiModelList;

public class CategoryDataLoader extends AsyncTaskLoader<SwapiModelList<SwapiModel>> {
    private SwapiCategory mSwapiCategory;
    private SwapiModelList<SwapiModel> mResult;

    public CategoryDataLoader(Context context, SwapiCategory swapiCategory) {
        super(context);
        mSwapiCategory = swapiCategory;
    }

    public static Loader load(Context context, LoaderManager manager,
                              int id, SwapiCategory swapiCategory, StarWarsApiCallback<SwapiModelList<SwapiModel>> apiCallback) {
        return manager.initLoader(id, null, new CategoryDataLoader.LoaderCallbacksDelegator(context, swapiCategory, apiCallback));
    }


    public static Loader reload(Context context, LoaderManager manager,
                                int id, SwapiCategory swapiCategory, StarWarsApiCallback<SwapiModelList<SwapiModel>> apiCallback) {
        return manager.restartLoader(id, null, new CategoryDataLoader.LoaderCallbacksDelegator(context, swapiCategory, apiCallback));
    }

    @Nullable
    @Override
    public SwapiModelList<SwapiModel> loadInBackground() {
        return StarWarsApi.getApi().getAllItemsOnCategory(mSwapiCategory);
    }

    @Override
    protected void onStartLoading() {
        if (mResult != null) {
            deliverResult(mResult);
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Cancel the current call.
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
        mResult = null;
    }

    @Override
    public void deliverResult(SwapiModelList<SwapiModel> result) {
        if (isReset()) {
            // The loader was reset while stopped
            return;
        }
        this.mResult = result;
        if (isStarted()) {
            super.deliverResult(result);
        }
    }


    static class LoaderCallbacksDelegator implements LoaderManager.LoaderCallbacks<SwapiModelList<SwapiModel>> {

        private final Context mContext;
        private final SwapiCategory mSwapiCategory;
        private final StarWarsApiCallback<SwapiModelList<SwapiModel>> mCallback;

        public LoaderCallbacksDelegator(Context context, SwapiCategory swapiCategory, StarWarsApiCallback<SwapiModelList<SwapiModel>> callback) {
            this.mContext = context;
            this.mSwapiCategory = swapiCategory;
            this.mCallback = callback;
        }


        @NonNull
        @Override
        public Loader<SwapiModelList<SwapiModel>> onCreateLoader(int id, @Nullable Bundle args) {
            return new CategoryDataLoader(mContext, mSwapiCategory);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<SwapiModelList<SwapiModel>> loader, SwapiModelList<SwapiModel> data) {
            mCallback.onResponse(data);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<SwapiModelList<SwapiModel>> loader) {

        }

    }
}