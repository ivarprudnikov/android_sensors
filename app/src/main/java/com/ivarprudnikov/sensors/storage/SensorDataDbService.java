package com.ivarprudnikov.sensors.storage;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.ivarprudnikov.sensors.config.Constants;
import com.ivarprudnikov.sensors.config.Preferences;
import com.ivarprudnikov.sensors.storage.SensorDataContract.DataEntry;

public class SensorDataDbService extends ContextWrapper {

    private SensorDataDbHelper mDbHelper;

    public SensorDataDbService (Context ctx){
        super(ctx);
        mDbHelper = new SensorDataDbHelper(ctx);
    }

    public void save(String sensorName, float[] sensorValues, long nanotimestamp){

        boolean sensorLogEnabled = Preferences.getPrefs(SensorDataDbService.this).getBoolean(Constants.PREFS_IS_SENSOR_LOG_ENABLED, false);
        if(sensorLogEnabled == false){
            return;
        }

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long microtimestamp = nanotimestamp / 1000;

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_NAME_TIMESTAMP, Long.valueOf(microtimestamp).intValue());
        values.put(DataEntry.COLUMN_NAME_SENSOR_NAME, sensorName);
        values.put(DataEntry.COLUMN_NAME_SENSOR_DATA, SensorDataConverter.floatArray2ByteArray(sensorValues));

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                DataEntry.TABLE_NAME,
                null,
                values);
    }

}
