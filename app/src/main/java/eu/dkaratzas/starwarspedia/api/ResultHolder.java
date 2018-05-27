/*
 * Copyright 2018 Dionysios Karatzas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
