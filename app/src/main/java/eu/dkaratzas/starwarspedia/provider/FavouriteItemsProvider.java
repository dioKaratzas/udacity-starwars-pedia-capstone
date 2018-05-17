package eu.dkaratzas.starwarspedia.provider;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

import static eu.dkaratzas.starwarspedia.provider.FavouriteItemsContract.FavouriteItemEntry.TABLE_NAME;

public class FavouriteItemsProvider extends ContentProvider {

    public static final int FAVOURITE_ITEMS = 100;
    public static final int FAVOURITE_ITEM_WITH_ID = 101;

    private FavouriteItemsDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavouriteItemsContract.CONTENT_AUTHORITY, FavouriteItemsContract.PATH_FAVOURITES, FAVOURITE_ITEMS);
        uriMatcher.addURI(FavouriteItemsContract.CONTENT_AUTHORITY, FavouriteItemsContract.PATH_FAVOURITES + "/#", FAVOURITE_ITEM_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mOpenHelper = new FavouriteItemsDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case FAVOURITE_ITEMS:
                cursor = db.query(TABLE_NAME,
                        projection,
                        selection, selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri resultUri;

        switch (sUriMatcher.match(uri)) {
            case FAVOURITE_ITEMS:
                long id = mOpenHelper.getWritableDatabase().insert(TABLE_NAME, null, values);
                if (id > 0) {
                    resultUri = ContentUris.withAppendedId(FavouriteItemsContract.FavouriteItemEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(resultUri, null);
        Timber.d("Uri is %s", resultUri);
        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numRowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case FAVOURITE_ITEM_WITH_ID:
                String id = uri.getPathSegments().get(1);
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(TABLE_NAME, "movie_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("We are not implementing update in StarWars Pedia.");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in StarWars Pedia.");
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}