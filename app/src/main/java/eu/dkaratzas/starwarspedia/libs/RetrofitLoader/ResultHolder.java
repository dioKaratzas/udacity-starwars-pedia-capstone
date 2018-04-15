package eu.dkaratzas.starwarspedia.libs.RetrofitLoader;

import retrofit2.Response;

/**
 * A wrapper around the Retrofit {@link Response} that will
 * throw any loading errors upon retrieval.
 *
 * @param <T> The data type that was loaded.
 */
public interface ResultHolder<T> {
    /**
     * Get the wrapped Response.
     *
     * @return The wrapped Response.
     * @throws Throwable if Retrofit encountered an error
     *                   while performing the HTTP request.
     */
    Response<T> get() throws Throwable;

    class ResponseHolder<T> implements ResultHolder<T> {
        private final Response<T> response;

        ResponseHolder(Response<T> response) {
            this.response = response;
        }

        @Override
        public Response<T> get() {
            return response;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ResponseHolder)) return false;
            Object otherResponse = ((ResponseHolder) obj).response;
            return response == otherResponse ||
                    (response != null && response.equals(otherResponse));
        }

        @Override
        public int hashCode() {
            return response == null ? 0 : (response.hashCode() + 1);
        }

        @Override
        public String toString() {
            return "ResponseHolder{response=" + response + '}';
        }
    }

    class ErrorHolder<T> implements ResultHolder<T> {
        private final Throwable error;

        ErrorHolder(Throwable throwable) {
            this.error = throwable;
        }

        @Override
        public Response<T> get() throws Throwable {
            throw error;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ErrorHolder &&
                    error.equals(((ErrorHolder) obj).error);
        }

        @Override
        public int hashCode() {
            return error.hashCode() + 1;
        }

        @Override
        public String toString() {
            return "ErrorHolder{error=" + error + '}';
        }
    }
}
