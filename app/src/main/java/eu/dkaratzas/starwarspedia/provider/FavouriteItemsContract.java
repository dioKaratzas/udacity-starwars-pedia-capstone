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
