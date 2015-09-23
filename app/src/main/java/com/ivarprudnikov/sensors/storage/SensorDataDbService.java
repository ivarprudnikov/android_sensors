package com.ivarprudnikov.sensors.storage;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.ivarprudnikov.sensors.config.Constants;
import com.ivarprudnikov.sensors.config.Preferences;
import com.ivarprudnikov.sensors.storage.SensorDataContract.DataEntry;

public class SensorDataDbService extends ContextWrapper {

    private SensorDataDbHelper mDbHelper;
    private SensorDataLogService mSensorDataLogService;

    public SensorDataDbService (Context ctx){
        super(ctx);
        mDbHelper = new SensorDataDbHelper(ctx);
        mSensorDataLogService = new SensorDataLogService(ctx);
    }

    public void save(String sensorName, float[] sensorValues, long nanotimestamp){

        boolean sensorLogEnabled = Preferences.getPrefs(SensorDataDbService.this).getBoolean(Constants.PREFS_IS_SENSOR_LOG_ENABLED, false);
        if(sensorLogEnabled == false){
            return;
        }

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        try {
            for(int i = 0; i < sensorValues.length; i++){
                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                float val = sensorValues[i];

                values.put(DataEntry.COLUMN_NAME_TIMESTAMP, Long.valueOf(nanotimestamp).intValue());
                values.put(DataEntry.COLUMN_NAME_SENSOR_NAME, sensorName);
                values.put(DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE, Float.valueOf(val).toString());
                values.put(DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX, i);

                db.insert(DataEntry.TABLE_NAME, null, values);
            }
        } catch(SQLiteException e){
            mSensorDataLogService.write(e.getMessage());
        }


        db.close();

    }

}
