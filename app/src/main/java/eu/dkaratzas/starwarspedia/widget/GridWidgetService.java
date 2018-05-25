package eu.dkaratzas.starwarspedia.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import eu.dkaratzas.starwarspedia.controllers.activities.MainActivity;
import eu.dkaratzas.starwarspedia.libs.Misc;
import eu.dkaratzas.starwarspedia.models.SimpleQueryData;
import eu.dkaratzas.starwarspedia.provider.FavouriteItemsContract;
import timber.log.Timber;


public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Timber.d("onGetViewFactory");
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        Timber.d("onDataSetChanged");
        // get all favourite items
        if (mCursor != null) {
            mCursor.close();
        }

        final long identityToken = Binder.clearCallingIdentity();

        mCursor = mContext.getContentResolver().query(FavouriteItemsContract.FavouriteItemEntry.CONTENT_URI,
                null,
                null,
                null,
                FavouriteItemsContract.FavouriteItemEntry._ID);

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {
        Timber.d("getViewAt");
        if (mCursor == null || mCursor.getCount() == 0) return null;
        mCursor.moveToPosition(position);
        int swapiIdIndex = mCursor.getColumnIndex(FavouriteItemsContract.FavouriteItemEntry.COLUMN_ID);
        int titleIndex = mCursor.getColumnIndex(FavouriteItemsContract.FavouriteItemEntry.COLUMN_TITLE);
        int swapiCategoryIndex = mCursor.getColumnIndex(FavouriteItemsContract.FavouriteItemEntry.COLUMN_CATEGORY);
        int base64ImageIndex = mCursor.getColumnIndex(FavouriteItemsContract.FavouriteItemEntry.COLUMN_IMAGE);

        SimpleQueryData favouriteItemData = new SimpleQueryData(
                mCursor.getString(swapiIdIndex),
                mCursor.getString(titleIndex),
                SwapiCategory.values()[mCursor.getInt(swapiCategoryIndex)],
                Misc.base64ToBitmap(mCursor.getString(base64ImageIndex))
        );

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.favourite_item_widget);

        views.setImageViewBitmap(R.id.widget_favourite_image, favouriteItemData.getImage());
        views.setTextViewText(R.id.widget_favourite_title, favouriteItemData.getTitle());

        // Fill in the onClick PendingIntent Template using the specific item Id for each item individually
        Bundle extras = new Bundle();
        extras.putParcelable(MainActivity.EXTRA_FAVOURITE_DATA, favouriteItemData);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        fillInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        views.setOnClickFillInIntent(R.id.widget_favourite_image, fillInIntent);

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

