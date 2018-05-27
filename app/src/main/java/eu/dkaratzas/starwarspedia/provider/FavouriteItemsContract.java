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

package eu.dkaratzas.starwarspedia.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavouriteItemsContract {
    public static final String CONTENT_AUTHORITY = "eu.dkaratzas.starwarspedia";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVOURITES = "favourites";

    public static final class FavouriteItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon()
                        .appendPath(PATH_FAVOURITES)
                        .build();

        public static final String TABLE_NAME = "favourites";
        public static final String COLUMN_ID = "swapi_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CATEGORY = "swapi_category";
        public static final String COLUMN_IMAGE = "image_base64";
    }
}
