package eu.dkaratzas.starwarspedia.loaders;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import eu.dkaratzas.starwarspedia.api.StarWarsApi;
import eu.dkaratzas.starwarspedia.api.StarWarsApiCallback;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import eu.dkaratzas.starwarspedia.models.SwapiModel;
import eu.dkaratzas.starwarspedia.models.SwapiModelList;
import timber.log.Timber;

public class CategoryDataLoader extends AsyncTaskLoader<SwapiModelList<SwapiModel>> {
    private SwapiCategory mSwapiCategory;
    private SwapiModelList<SwapiModel> mResult;

    public CategoryDataLoader(Context context, SwapiCategory swapiCategory) {
        super(context);
        mSwapiCategory = swapiCategory;
    }

    public static <T> Loader load(Context context, LoaderManager manager,
                                  int id, SwapiCategory swapiCategory, StarWarsApiCallback<SwapiModelList<SwapiModel>> apiCallback) {
        return manager.initLoader(id, null, new CategoryDataLoader.LoaderCallbacksDelegator(context, swapiCategory, apiCallback));
    }


    public static <T> Loader reload(Context context, LoaderManager manager,
                                    int id, SwapiCategory swapiCategory, StarWarsApiCallback<SwapiModelList<SwapiModel>> apiCallback) {
        return manager.restartLoader(id, null, new CategoryDataLoader.LoaderCallbacksDelegator(context, swapiCategory, apiCallback));
    }

    @Nullable
    @Override
    public SwapiModelList<SwapiModel> loadInBackground() {
        int currentPage = 1;
        boolean gotAnotherPage;
        SwapiModelList<SwapiModel> result = null;
        do {
            try {
                result = mapResult(result, StarWarsApi.getApi().getItemsRequestOnCategoryById(currentPage, mSwapiCategory).sync());
                currentPage++;
                gotAnotherPage = result.gotAnotherPage();
            } catch (Exception ex) {
                Timber.e(Log.getStackTraceString(ex));
                return null;
            }


        } while (gotAnotherPage);

        return result;
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


    private SwapiModelList<SwapiModel> mapResult(SwapiModelList<SwapiModel> result, SwapiModelList<SwapiModel> newResult) {
        if (result == null) {
            result = newResult;
        } else {
            result.results.addAll(newResult.results);

            result = new SwapiModelList<>(newResult.getNext(), newResult.getPrevious(), newResult.getCount(), result.results);

        }
        return result;
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
        public Loader onCreateLoader(int id, @Nullable Bundle args) {
            return new CategoryDataLoader(mContext, mSwapiCategory);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<SwapiModelList<SwapiModel>> loader, SwapiModelList<SwapiModel> data) {
            mCallback.onResponse(data);
        }

        @Override
        public void onLoaderReset(@NonNull Loader loader) {

        }
    }
}