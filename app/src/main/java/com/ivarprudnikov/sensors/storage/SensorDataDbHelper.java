/**
 * This file is part of com.ivarprudnikov.sensors package.
 *
 * com.ivarprudnikov.sensors is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * com.ivarprudnikov.sensors is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with com.ivarprudnikov.sensors.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ivarprudnikov.sensors.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ivarprudnikov.sensors.storage.SensorDataContract.DataEntry;
import com.ivarprudnikov.sensors.storage.SensorDataContract.ActionUrl;
import com.ivarprudnikov.sensors.storage.SensorDataContract.ActionResult;

/**
 * http://developer.android.com/training/basics/data-storage/databases.html
 */
public class SensorDataDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 15;
    public static final String DATABASE_NAME = "SensorData.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DataEntry.TABLE_NAME + " (" +
                    DataEntry._ID + INTEGER_TYPE + " PRIMARY KEY," +
                    DataEntry.COLUMN_NAME_TIMESTAMP + INTEGER_TYPE + COMMA_SEP +
                    DataEntry.COLUMN_NAME_SENSOR_NAME + TEXT_TYPE + COMMA_SEP +
                    DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE + REAL_TYPE + COMMA_SEP +
                    DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX + INTEGER_TYPE +
                    " )";
    private static final String SQL_CREATE_ACTIONS =
            "CREATE TABLE " + ActionUrl.TABLE_NAME + " (" +
                    ActionUrl._ID + INTEGER_TYPE + " PRIMARY KEY," +
                    ActionUrl.COLUMN_NAME_TIMESTAMP + INTEGER_TYPE + COMMA_SEP +
                    ActionUrl.COLUMN_NAME_LAST_UPDATED + INTEGER_TYPE + COMMA_SEP +
                    ActionUrl.COLUMN_NAME_URL + TEXT_TYPE + COMMA_SEP +
                    ActionUrl.COLUMN_NAME_FREQUENCY + INTEGER_TYPE + COMMA_SEP +
                    ActionUrl.COLUMN_NAME_CLIENT_CERTIFICATE + BLOB_TYPE +
                    " )";
    private static final String SQL_CREATE_ACTION_RESULTS =
            "CREATE TABLE " + ActionResult.TABLE_NAME + " (" +
                    ActionResult._ID + INTEGER_TYPE + " PRIMARY KEY," +
                    ActionResult.COLUMN_NAME_TIMESTAMP + INTEGER_TYPE + COMMA_SEP +
                    ActionResult.COLUMN_NAME_ACTION_ID + INTEGER_TYPE + COMMA_SEP +
                    ActionResult.COLUMN_NAME_IS_SUCCESS + INTEGER_TYPE + COMMA_SEP +
                    ActionResult.COLUMN_NAME_DATA_FROM_TIME + INTEGER_TYPE + COMMA_SEP +
                    ActionResult.COLUMN_NAME_DATA_TO_TIME + INTEGER_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME;
    private static final String SQL_DELETE_ACTIONS = "DROP TABLE IF EXISTS " + ActionUrl.TABLE_NAME;
    private static final String SQL_DELETE_ACTION_RESULTS = "DROP TABLE IF EXISTS " + ActionResult.TABLE_NAME;

    public SensorDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ACTIONS);
        db.execSQL(SQL_CREATE_ACTION_RESULTS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_ACTIONS);
        db.execSQL(SQL_DELETE_ACTION_RESULTS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
