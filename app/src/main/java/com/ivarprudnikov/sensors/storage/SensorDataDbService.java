package com.ivarprudnikov.sensors.storage;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.ivarprudnikov.sensors.App;
import com.ivarprudnikov.sensors.config.Constants;
import com.ivarprudnikov.sensors.config.Preferences;
import com.ivarprudnikov.sensors.storage.SensorDataContract.DataEntry;

public class SensorDataDbService extends ContextWrapper {

    private SensorDataDbHelper mDbHelper;

    public SensorDataDbService (Context ctx){
        super(ctx);
        mDbHelper = new SensorDataDbHelper(ctx);
    }

    public int countSensorEvents(){

        SQLiteDatabase db = null;
        int count = -1;

        try {
            db = mDbHelper.getReadableDatabase();
        } catch(SQLiteException e){
            Log.e("SensorDataDbService","mDbHelper.getReadableDatabase() exception", e);
        }

        if(db != null){
            Cursor c = db.query(
                    DataEntry.TABLE_NAME,
                    new String[]{DataEntry._ID},
                    DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX + "=?",
                    new String[]{"0"},
                    null, null, null, null);
            count = c.getCount();
            db.close();
        }

        return count;
    }

    public void save(String sensorName, float[] sensorValues, long timestamp){

        boolean sensorLogEnabled = App.getPrefs().getBoolean(Constants.PREFS_IS_SENSOR_LOG_ENABLED, false);
        if(sensorLogEnabled == false){
            return;
        }

        for(int i = 0; i < sensorValues.length; i++){
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            float val = sensorValues[i];

            values.put(DataEntry.COLUMN_NAME_TIMESTAMP, timestamp);
            values.put(DataEntry.COLUMN_NAME_SENSOR_NAME, sensorName);
            values.put(DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE, val);
            values.put(DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX, i);

            try {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                if(db != null){
                    db.insert(DataEntry.TABLE_NAME, null, values);
                }
            } catch(SQLiteException e){
                Log.e("SensorDataDbService", "mDbHelper.getWritableDatabase() exception", e);
            }

        }

    }

    public int deleteAllRows(){

        SQLiteDatabase db = null;
        String whereClause = null;
        String[] whereArgs = null;

        try {
            db = mDbHelper.getWritableDatabase();
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getWritableDatabase() exception", e);
        }

        int deleted = 0;

        if(db != null){
            deleted = db.delete(DataEntry.TABLE_NAME, whereClause, whereArgs);
        }

        return deleted;
    }

    public int trimData(){

        long time = System.currentTimeMillis();
        String duration = Preferences.getStorageDuration();
        String[] parts = duration.split(" ");
        int num = Integer.valueOf(parts[0]);
        long deleteOffset;
        if(parts[0].equals("min")){
            deleteOffset = (long)(time - (60 * 1000 * num));
        } else {
            deleteOffset = (long)(time - (3600 * 1000 * num));
        }

        SQLiteDatabase db = null;
        String whereClause = DataEntry.COLUMN_NAME_TIMESTAMP + " < ?";
        String[] whereArgs = new String[]{ String.valueOf(deleteOffset) };

        try {
            db = mDbHelper.getWritableDatabase();
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getWritableDatabase() exception", e);
        }

        int deleted = 0;

        if(db != null){
            deleted = db.delete(DataEntry.TABLE_NAME, whereClause, whereArgs);
        }

        return deleted;
    }

}
