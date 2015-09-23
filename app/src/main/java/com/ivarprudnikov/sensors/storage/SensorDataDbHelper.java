package com.ivarprudnikov.sensors.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ivarprudnikov.sensors.storage.SensorDataContract.DataEntry;

import java.util.ArrayList;

/**
 * http://developer.android.com/training/basics/data-storage/databases.html
 */
public class SensorDataDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "SensorData.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DataEntry.TABLE_NAME + " (" +
                    DataEntry._ID + " INTEGER PRIMARY KEY," +
                    DataEntry.COLUMN_NAME_TIMESTAMP + INTEGER_TYPE + COMMA_SEP +
                    DataEntry.COLUMN_NAME_SENSOR_NAME + TEXT_TYPE + COMMA_SEP +
                    DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE + TEXT_TYPE + COMMA_SEP +
                    DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX + INTEGER_TYPE +
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

    /**
     * First cursor contains query results
     * Second contains error if any
     * @param query<String>
     * @return
     */
    public ArrayList<Cursor> getData(String query) {

        SQLiteDatabase sqlDB = this.getWritableDatabase();
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        String[] columns = new String[]{"mesage"};
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            Cursor c = sqlDB.rawQuery(query, null);
            Cursor2.addRow(new Object[]{"Success"});
            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {
                c.moveToFirst();
                alc.set(0, c);
            }
        } catch (Exception ex) {
            Log.d("SensorDataDbHelper", ex.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
        }

        return alc;
    }

}
