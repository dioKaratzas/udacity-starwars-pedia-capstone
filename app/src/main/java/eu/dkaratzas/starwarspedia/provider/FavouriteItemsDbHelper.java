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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavouriteItemsDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "swp.db";
    private static final int DATABASE_VERSION = 1;

    public FavouriteItemsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + FavouriteItemsContract.FavouriteItemEntry.TABLE_NAME + " (" +
                FavouriteItemsContract.FavouriteItemEntry._ID + " INTEGER PRIMARY KEY, " +
                FavouriteItemsContract.FavouriteItemEntry.COLUMN_ID + " TEXT NOT NULL, " +
                FavouriteItemsContract.FavouriteItemEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                FavouriteItemsContract.FavouriteItemEntry.COLUMN_CATEGORY + " TEXT NOT NULL," +
                FavouriteItemsContract.FavouriteItemEntry.COLUMN_IMAGE + " TEXT NOT NULL)" +
                "; ";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteItemsContract.FavouriteItemEntry.TABLE_NAME);
        onCreate(db);
    }
}
