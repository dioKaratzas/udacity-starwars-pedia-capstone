package eu.dkaratzas.starwarspedia.api;


import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

/**
 * A wrapper around the Apollo {@link Response} that will
 * throw any loading errors upon retrieval.
 *
 * @param <T> The data type that was loaded.
 */
interface ResultHolder<T> {
    /**
     * Get the wrapped Response.
     *
     * @return The wrapped Response.
     * @throws ApolloException if Apollo encountered an error
     *                         while performing the HTTP request.
     */
    Response<T> get() throws ApolloException;

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
        private final ApolloException error;

        ErrorHolder(ApolloException throwable) {
            this.error = throwable;
        }

        @Override
        public Response<T> get() throws ApolloException {
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
