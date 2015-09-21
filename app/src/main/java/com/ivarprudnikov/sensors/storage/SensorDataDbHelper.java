package com.ivarprudnikov.sensors.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.ivarprudnikov.sensors.storage.SensorDataContract.DataEntry;

/**
 * http://developer.android.com/training/basics/data-storage/databases.html
 */
public class SensorDataDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SensorData.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DataEntry.TABLE_NAME + " (" +
                    DataEntry._ID + " INTEGER PRIMARY KEY," +
                    DataEntry.COLUMN_NAME_TIMESTAMP + INTEGER_TYPE + COMMA_SEP +
                    DataEntry.COLUMN_NAME_SENSOR_NAME + TEXT_TYPE + COMMA_SEP +
                    DataEntry.COLUMN_NAME_SENSOR_DATA + BLOB_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME;

    public SensorDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
