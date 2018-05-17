package eu.dkaratzas.starwarspedia.api;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

/**
 * Loader implementation for Apollo Call API.
 *
 * @param <T> The data type to be loaded.
 */

class ApolloLoader<T> extends Loader<ResultHolder<T>> {

    /**
     * Load the provided {@link ApolloCall} using the {@link LoaderManager},
     * or deliver the result if it has already been loaded at the
     * provided ID.
     *
     * @param <T>      The data type to be loaded.
     * @param context  The Context to provide to the ApolloLoader.
     * @param manager  The LoaderManager instance.
     * @param id       The unique identifier to be used for the ApolloLoader.
     * @param call     The call to be executed.
     * @param callback The Apollo callback.
     */
    public static <T> void load(Context context, LoaderManager manager, int id, ApolloCall<T> call, ApolloCall.Callback<T> callback) {
        manager.initLoader(id, null, new LoaderCallbacksDelegator<>(context, call, callback));
    }

    /**
     * Reload the provided {@link ApolloCall} using the {@link LoaderManager},
     * regardless of whether a result has already been loaded or is
     * currently loading.
     *
     * @param <T>      The data type to be loaded.
     * @param context  The Context to provide to the ApolloLoader.
     * @param manager  The LoaderManager instance.
     * @param id       The unique identifier to be used for the ApolloLoader.
     * @param call     The call to be executed.
     * @param callback The Apollo callback.
     */
    public static <T> void reload(Context context, LoaderManager manager, int id, ApolloCall<T> call, ApolloCall.Callback<T> callback) {
        manager.restartLoader(id, null, new LoaderCallbacksDelegator<>(context, call, callback));
    }

    static class LoaderCallbacksDelegator<T> implements LoaderManager.LoaderCallbacks<ResultHolder<T>> {
        private final Context context;
        private final ApolloCall<T> call;
        private final ApolloCall.Callback<T> callback;

        LoaderCallbacksDelegator(Context context, ApolloCall<T> call, ApolloCall.Callback<T> callback) {
            this.context = context;
            this.call = call;
            this.callback = callback;
        }

        @NonNull
        @Override
        public Loader<ResultHolder<T>> onCreateLoader(int id, Bundle args) {
            return new ApolloLoader<>(context, call);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ResultHolder<T>> loader, ResultHolder<T> resultHolder) {
            Response<T> response;
            try {
                response = resultHolder.get();
            } catch (ApolloException t) {
                callback.onFailure(t);
                return;
            }
            callback.onResponse(response);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ResultHolder<T>> loader) {
        }
    }

    private final ApolloCall<T> call;
    private ApolloCall<T> currentCall, cancellingCall;
    private ResultHolder<T> result;

    public ApolloLoader(Context context, ApolloCall<T> call) {
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

    private class ResultHandler extends ApolloCall.Callback<T> {
        private final ApolloCall<T> call;

        ResultHandler() {
            call = currentCall;
        }

        @Override
        public void onResponse(@Nonnull Response<T> response) {
            onResult(new ResultHolder.ResponseHolder<>(response));
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            onResult(new ResultHolder.ErrorHolder<T>(e));
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
    }
}

