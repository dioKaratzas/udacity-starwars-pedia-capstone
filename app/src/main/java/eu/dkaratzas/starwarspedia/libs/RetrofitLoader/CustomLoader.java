package eu.dkaratzas.starwarspedia.libs.RetrofitLoader;


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
import eu.dkaratzas.starwarspedia.models.SwapiModelList;

public class CustomLoader<T> extends AsyncTaskLoader<T> {
    private boolean mLoadAllPages = true;
    private SwapiCategory mSwapiCategory;
    private T mResult;

    public static <T> void load(Context context, LoaderManager manager,
                                int id, SwapiCategory swapiCategory, StarWarsApiCallback<T> apiCallback) {
        manager.initLoader(id, null, new LoaderCallbacksDelegator<T>(
                context, swapiCategory, apiCallback));
    }


    public static <T> void reload(Context context, LoaderManager manager,
                                  int id, SwapiCategory swapiCategory, StarWarsApiCallback<T> apiCallback) {
        manager.restartLoader(id, null, new LoaderCallbacksDelegator<T>(
                context, swapiCategory, apiCallback));
    }

    static class LoaderCallbacksDelegator<T>
            implements LoaderManager.LoaderCallbacks<T> {
        private final Context context;
        private final SwapiCategory swapiCategory;
        private final StarWarsApiCallback<T> callback;

        LoaderCallbacksDelegator(Context context,
                                 SwapiCategory swapiCategory, StarWarsApiCallback<T> apiCallback) {
            this.context = context;
            this.swapiCategory = swapiCategory;
            this.callback = apiCallback;
        }


        @NonNull
        @Override
        public Loader<T> onCreateLoader(int id, @Nullable Bundle args) {
            return new CustomLoader<T>(context, swapiCategory);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<T> loader, T data) {
            callback.onResponse(data);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<T> loader) {

        }
    }

    public CustomLoader(Context context, SwapiCategory swapiCategory) {
        super(context);
        mSwapiCategory = swapiCategory;
    }

    public CustomLoader(Context context, SwapiCategory swapiCategory, boolean loadAllPages) {
        super(context);
        mSwapiCategory = swapiCategory;
        mLoadAllPages = loadAllPages;
    }

    @Override
    public T loadInBackground() {
        if (mLoadAllPages) {
            int currentPage = 1;
            boolean gotAnotherPage;

            do {
                try {
                    SwapiModelList<T> result = (SwapiModelList<T>) StarWarsApi.getApi().getResourcesOfCategoryOnPage(currentPage, mSwapiCategory).sync();
                    mapResult(result);
                    currentPage++;
                    gotAnotherPage = result.gotAnotherPage();
                } catch (Exception ex) {
                    return null;
                }


            } while (gotAnotherPage);
        }
        return mResult;
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
    public void deliverResult(T result) {
        if (isReset()) {
            // The loader was reset while stopped
            return;
        }
        this.mResult = result;
        if (isStarted()) {
            super.deliverResult(result);
        }
    }

    private void mapResult(SwapiModelList<T> result) {
        if (mResult == null) {
            mResult = (T) result;
        } else {
            SwapiModelList<T> tempResult = (SwapiModelList<T>) mResult;
            tempResult.results.addAll(result.results);

            mResult = (T) new SwapiModelList<T>(result.getNext(), result.getPrevious(), result.getCount(), tempResult.results);

        }
    }
}
