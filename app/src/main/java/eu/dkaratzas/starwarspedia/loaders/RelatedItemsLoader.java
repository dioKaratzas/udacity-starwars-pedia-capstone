package eu.dkaratzas.starwarspedia.loaders;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.dkaratzas.starwarspedia.api.StarWarsApi;
import eu.dkaratzas.starwarspedia.api.StarWarsApiCallback;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import eu.dkaratzas.starwarspedia.models.SwapiModel;

public class RelatedItemsLoader extends AsyncTaskLoader<Map<String, List<SwapiModel>>> {
    // The list is the url's of the item to fetch from the internet
    private Map<String, Map<SwapiCategory, List<String>>> mItemsToLoad;
    private Map<String, List<SwapiModel>> mResult;

    public RelatedItemsLoader(Context context, Map<String, Map<SwapiCategory, List<String>>> itemsToLoad) {
        super(context);
        mItemsToLoad = itemsToLoad;
    }

    public static Loader load(Context context, LoaderManager manager,
                              int id, Map<String, Map<SwapiCategory, List<String>>> itemsToLoad, StarWarsApiCallback<Map<String, List<SwapiModel>>> apiCallback) {
        return manager.initLoader(id, null, new RelatedItemsLoader.LoaderCallbacksDelegator(context, itemsToLoad, apiCallback));
    }

    @Nullable
    @Override
    public Map<String, List<SwapiModel>> loadInBackground() {
        if (mItemsToLoad != null && mItemsToLoad.size() > 0) {
            Map<String, List<SwapiModel>> result = new LinkedHashMap<>();

            for (Map.Entry<String, Map<SwapiCategory, List<String>>> relatedEntry : mItemsToLoad.entrySet()) {

                for (Map.Entry<SwapiCategory, List<String>> entry : mItemsToLoad.get(relatedEntry.getKey()).entrySet()) {

                    List<SwapiModel> itemsResult = StarWarsApi.getApi().getItemsOnCategoryByUrls(entry.getValue(), entry.getKey());
                    if (itemsResult != null)
                        result.put(relatedEntry.getKey(), itemsResult);
                }
            }

            return result;
        }
        return null;
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
    public void deliverResult(Map<String, List<SwapiModel>> result) {
        if (isReset()) {
            // The loader was reset while stopped
            return;
        }
        this.mResult = result;
        if (isStarted()) {
            super.deliverResult(result);
        }
    }


    static class LoaderCallbacksDelegator implements LoaderManager.LoaderCallbacks<Map<String, List<SwapiModel>>> {

        private final Context mContext;
        private final Map<String, Map<SwapiCategory, List<String>>> mItemsToLoad;
        private final StarWarsApiCallback<Map<String, List<SwapiModel>>> mCallback;

        public LoaderCallbacksDelegator(Context context, Map<String, Map<SwapiCategory, List<String>>> itemsToLoad, StarWarsApiCallback<Map<String, List<SwapiModel>>> callback) {
            this.mContext = context;
            this.mItemsToLoad = itemsToLoad;
            this.mCallback = callback;
        }


        @NonNull
        @Override
        public Loader<Map<String, List<SwapiModel>>> onCreateLoader(int id, @Nullable Bundle args) {
            return new RelatedItemsLoader(mContext, mItemsToLoad);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Map<String, List<SwapiModel>>> loader, Map<String, List<SwapiModel>> data) {
            mCallback.onResponse(data);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Map<String, List<SwapiModel>>> loader) {

        }

    }
}