package eu.dkaratzas.starwarspedia.loaders;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.orhanobut.logger.Logger;

import eu.dkaratzas.starwarspedia.api.StarWarsApi;
import eu.dkaratzas.starwarspedia.api.StarWarsApiCallback;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import eu.dkaratzas.starwarspedia.models.SwapiModelList;

public class CategoryLoader<T> extends AsyncTaskLoader<T> {
    private SwapiCategory mSwapiCategory;
    private T mResult;

    public static <T> Loader<T> load(Context context, LoaderManager manager,
                                     int id, SwapiCategory swapiCategory, StarWarsApiCallback<T> apiCallback) {
        return manager.initLoader(id, null, new LoaderCallbacksDelegator<>(context, swapiCategory, apiCallback));
    }


    public static <T> Loader<T> reload(Context context, LoaderManager manager,
                                       int id, SwapiCategory swapiCategory, StarWarsApiCallback<T> apiCallback) {
        return manager.restartLoader(id, null, new LoaderCallbacksDelegator<>(context, swapiCategory, apiCallback));
    }

    static class LoaderCallbacksDelegator<T> implements LoaderManager.LoaderCallbacks<T> {
        private final Context mContext;
        private final SwapiCategory mSwapiCategory;
        private final StarWarsApiCallback<T> mCallback;

        LoaderCallbacksDelegator(Context context, SwapiCategory swapiCategory, StarWarsApiCallback<T> apiCallback) {
            this.mContext = context;
            this.mSwapiCategory = swapiCategory;
            this.mCallback = apiCallback;
        }


        @NonNull
        @Override
        public Loader<T> onCreateLoader(int id, @Nullable Bundle args) {
            return new CategoryLoader<>(mContext, mSwapiCategory);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<T> loader, T data) {
            mCallback.onResponse(data);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<T> loader) {
            mCallback.onCancel();
        }
    }

    public CategoryLoader(Context context, SwapiCategory swapiCategory) {
        super(context);
        mSwapiCategory = swapiCategory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T loadInBackground() {
        int currentPage = 1;
        boolean gotAnotherPage;
        SwapiModelList<T> result = null;
        do try {
            result = mapResult(result, (SwapiModelList<T>) StarWarsApi.getApi().getItemsRequestOnCategoryById(currentPage, mSwapiCategory).sync());
            currentPage++;
            gotAnotherPage = result.gotAnotherPage();
        } catch (ClassCastException ex) {
            Logger.e(Log.getStackTraceString(ex));
            return null;
        } while (gotAnotherPage);

        return (T) result;
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

    private SwapiModelList<T> mapResult(SwapiModelList<T> result, SwapiModelList<T> newResult) {
        if (result == null) {
            result = newResult;
        } else {
            result.results.addAll(newResult.results);

            result = new SwapiModelList<>(newResult.getNext(), newResult.getPrevious(), newResult.getCount(), result.results);

        }
        return result;
    }
}
