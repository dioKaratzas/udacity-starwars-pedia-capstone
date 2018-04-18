package eu.dkaratzas.starwarspedia.loaders.RetrofitLoader;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Loader implementation for Retrofit 2.0 Call API.
 *
 * @param <T> The data type to be loaded.
 */
public class RetrofitLoader<T> extends Loader<ResultHolder<T>> {
    /**
     * Load the provided {@link Call} using the {@link LoaderManager},
     * or deliver the result if it has already been loaded at the
     * provided ID.
     *
     * @param <T>      The data type to be loaded.
     * @param context  The Context to provide to the RetrofitLoader.
     * @param manager  The LoaderManager instance.
     * @param id       The unique identifier to be used for the RetrofitLoader.
     * @param call     The call to be executed.
     * @param callback The Retrofit callback.
     */
    public static <T> void load(Context context, LoaderManager manager,
                                int id, Call<T> call, Callback<T> callback) {
        manager.initLoader(id, null, new LoaderCallbacksDelegator<>(
                context, call, callback));
    }

    /**
     * Reload the provided {@link Call} using the {@link LoaderManager},
     * regardless of whether a result has already been loaded or is
     * currently loading.
     *
     * @param <T>      The data type to be loaded.
     * @param context  The Context to provide to the RetrofitLoader.
     * @param manager  The LoaderManager instance.
     * @param id       The unique identifier to be used for the RetrofitLoader.
     * @param call     The call to be executed.
     * @param callback The Retrofit callback.
     */
    public static <T> void reload(Context context, LoaderManager manager,
                                  int id, Call<T> call, Callback<T> callback) {
        manager.restartLoader(id, null, new LoaderCallbacksDelegator<>(
                context, call, callback));
    }

    static class LoaderCallbacksDelegator<T>
            implements LoaderManager.LoaderCallbacks<ResultHolder<T>> {
        private final Context context;
        private final Call<T> call;
        private final Callback<T> callback;

        LoaderCallbacksDelegator(Context context,
                                 Call<T> call, Callback<T> callback) {
            this.context = context;
            this.call = call;
            this.callback = callback;
        }

        @Override
        public Loader<ResultHolder<T>> onCreateLoader(
                int id, Bundle args) {
            return new RetrofitLoader<>(context, call);
        }

        @Override
        public void onLoadFinished(Loader<ResultHolder<T>> loader,
                                   ResultHolder<T> resultHolder) {
            Response<T> response;
            try {
                response = resultHolder.get();
            } catch (Throwable t) {
                callback.onFailure(call, t);
                return;
            }
            callback.onResponse(call, response);
        }

        @Override
        public void onLoaderReset(Loader<ResultHolder<T>> loader) {
        }
    }

    private final Call<T> call;
    private Call<T> currentCall, cancellingCall;
    private ResultHolder<T> result;

    public RetrofitLoader(Context context, Call<T> call) {
        super(context);
        this.call = call;
    }

    @Override
    protected void onStartLoading() {
        if (result != null) {
            deliverResult(result);
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
        result = null;
    }

    @Override
    public void deliverResult(ResultHolder<T> result) {
        if (isReset()) {
            // The loader was reset while stopped
            return;
        }
        this.result = result;
        if (isStarted()) {
            super.deliverResult(result);
        }
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        cancelLoad();
        currentCall = call.clone();
        if (cancellingCall == null) {
            currentCall.enqueue(new ResultHandler());
        }
    }

    @Override
    protected boolean onCancelLoad() {
        if (currentCall == null) {
            return false;
        }
        if (cancellingCall != null) {
            // There was a pending call already waiting for a previous
            // one being canceled; just drop it.
            currentCall = null;
            return false;
        }
        currentCall.cancel();
        cancellingCall = currentCall;
        currentCall = null;
        return true;
    }

    private class ResultHandler implements Callback<T> {
        private final Call<T> call;

        ResultHandler() {
            call = currentCall;
        }

        private void onResult(ResultHolder<T> result) {
            if (currentCall != call) {
                if (cancellingCall == call) {
                    cancellingCall = null;
                    deliverCancellation();
                    if (currentCall != null) {
                        currentCall.enqueue(new ResultHandler());
                    }
                }
            } else if (!isAbandoned()) {
                currentCall = null;
                deliverResult(result);
            }
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            onResult(new ResultHolder.ResponseHolder<>(response));
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            onResult(new ResultHolder.ErrorHolder<T>(t));
        }
    }
}
