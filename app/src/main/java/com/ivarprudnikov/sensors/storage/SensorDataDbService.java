package com.ivarprudnikov.sensors.storage;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.util.Log;

import com.ivarprudnikov.sensors.App;
import com.ivarprudnikov.sensors.config.Constants;
import com.ivarprudnikov.sensors.config.Preferences;
import com.ivarprudnikov.sensors.storage.SensorDataContract.DataEntry;

import java.util.HashMap;
import java.util.Map;

public class SensorDataDbService extends ContextWrapper {

    private SensorDataDbHelper mDbHelper;

    public SensorDataDbService (Context ctx){
        super(ctx);
        mDbHelper = new SensorDataDbHelper(ctx);
    }

    public int countSensorEvents(Sensor sensor){

        int count = -1;

        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String whereQuery = DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX + "=?";
            String[] args = new String[]{"0"};

            if(sensor != null){
                whereQuery = DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX + "=? AND " + DataEntry.COLUMN_NAME_SENSOR_NAME + "=?";
                args = new String[]{"0", sensor.getName()};
            }

            if(db != null){
                Cursor c = db.query(
                        DataEntry.TABLE_NAME,
                        new String[]{DataEntry._ID},
                        whereQuery,
                        args,
                        null, null, null, null);
                count = c.getCount();
                c.close();
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService","mDbHelper.getReadableDatabase() exception", e);
        }

        return count;
    }

    public void save(String sensorName, float[] sensorValues, long timestamp){

        boolean sensorLogEnabled = App.getPrefs().getBoolean(Constants.PREFS_IS_SENSOR_LOG_ENABLED, false);
        if(!sensorLogEnabled){
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

        int deleted = 0;

        try {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            if(db != null){
                deleted = db.delete(DataEntry.TABLE_NAME, null, null);
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getWritableDatabase() exception", e);
        }

        return deleted;
    }

    public int deleteSensorRows(Sensor sensor){

        int deleted = 0;

        try {
            if(sensor != null){

                String whereQuery = DataEntry.COLUMN_NAME_SENSOR_NAME + "=?";
                String[] args = new String[]{sensor.getName()};
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                if(db != null){
                    deleted = db.delete(DataEntry.TABLE_NAME, whereQuery, args);
                }
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getWritableDatabase() exception", e);
        }

        return deleted;
    }

    public int trimData(){

        long time = System.currentTimeMillis();
        String duration = Preferences.getStorageDuration();
        String[] parts = duration.split(" ");
        int num = Integer.valueOf(parts[0]);
        long deleteOffset;
        if(parts[1].equals("min")){
            deleteOffset = time - (60 * 1000 * num);
        } else {
            deleteOffset = time - (3600 * 1000 * num);
        }

        String whereClause = DataEntry.COLUMN_NAME_TIMESTAMP + " < ?";
        String[] whereArgs = new String[]{ String.valueOf(deleteOffset) };

        int deleted = 0;
        try {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            if(db != null){
                deleted = db.delete(DataEntry.TABLE_NAME, whereClause, whereArgs);
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getWritableDatabase() exception", e);
        }

        return deleted;
    }

    public int rowsOverLimit(){

        long time = System.currentTimeMillis();
        String duration = Preferences.getStorageDuration();
        String[] parts = duration.split(" ");
        int num = Integer.valueOf(parts[0]);
        long offset;
        if(parts[1].equals("min")){
            offset = time - (60 * 1000 * num);
        } else {
            offset = time - (3600 * 1000 * num);
        }

        String whereClause = DataEntry.COLUMN_NAME_TIMESTAMP + " < ?";
        String[] whereArgs = new String[]{ String.valueOf(offset) };

        int count = 0;
        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            if(db != null){
                Cursor c = db.query(
                        DataEntry.TABLE_NAME,
                        new String[]{DataEntry._ID},
                        whereClause,
                        whereArgs,
                        null, null, null, null);
                count = c.getCount();
                c.close();
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getWritableDatabase() exception", e);
        }

        return count;
    }

    /**
     * { "sensorName": {
     *   "timestamp": {
     *       "0": "x",
     *       "1": "y",
     *       ...
     *   },
     *   ...
     * }, ... }
     * @return
     */
    public Map getData(){

        Map data = new HashMap<String, Map>();

        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            if(db != null){
                Cursor c = db.query(
                    DataEntry.TABLE_NAME,
                    new String[]{
                        DataEntry.COLUMN_NAME_TIMESTAMP,
                        DataEntry.COLUMN_NAME_SENSOR_NAME,
                        DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE,
                        DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX,
                    }, null, null, null, null, null, null);
                if(c.moveToFirst()){
                    while (c.isAfterLast() == false) {
                        String name = c.getString(c.getColumnIndex(DataEntry.COLUMN_NAME_SENSOR_NAME));
                        String timestamp = c.getString(c.getColumnIndex(DataEntry.COLUMN_NAME_TIMESTAMP));
                        String idx = c.getString(c.getColumnIndex(DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX));
                        float val = c.getFloat(c.getColumnIndex(DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE));

                        if(data.get(name) == null){
                            data.put(name, new HashMap<Long, Map>());
                        }
                        Map sensorData = (Map)data.get(name);
                        if(sensorData.get(timestamp) == null){
                            sensorData.put(timestamp, new HashMap<String, Float>());
                        }
                        Map timestampData = (Map)sensorData.get(timestamp);
                        timestampData.put(idx, val);
                        sensorData.put(timestamp, timestampData);
                        data.put(name, sensorData);
                        c.moveToNext();
                    }
                }
                c.close();
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getWritableDatabase() exception", e);
        }

        return data;

    }
}
