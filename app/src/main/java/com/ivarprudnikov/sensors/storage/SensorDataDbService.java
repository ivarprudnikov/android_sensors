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

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.text.TextUtils;
import android.util.Log;

import com.ivarprudnikov.sensors.App;
import com.ivarprudnikov.sensors.OnExportAlarmBroadcastReceiver;
import com.ivarprudnikov.sensors.config.Constants;
import com.ivarprudnikov.sensors.config.Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
            String whereQuery = SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX + "=?";
            String[] args = new String[]{"0"};

            if(sensor != null){
                whereQuery = SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX + "=? AND " + SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_NAME + "=?";
                args = new String[]{"0", sensor.getName()};
            }

            if(db != null){
                Cursor c = db.query(
                        SensorDataContract.DataEntry.TABLE_NAME,
                        new String[]{SensorDataContract.DataEntry._ID},
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

            values.put(SensorDataContract.DataEntry.COLUMN_NAME_TIMESTAMP, timestamp);
            values.put(SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_NAME, sensorName);
            values.put(SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE, val);
            values.put(SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX, i);

            try {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                if(db != null){
                    db.insert(SensorDataContract.DataEntry.TABLE_NAME, null, values);
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
                deleted = db.delete(SensorDataContract.DataEntry.TABLE_NAME, null, null);
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

                String whereQuery = SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_NAME + "=?";
                String[] args = new String[]{sensor.getName()};
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                if(db != null){
                    deleted = db.delete(SensorDataContract.DataEntry.TABLE_NAME, whereQuery, args);
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

        String whereClause = SensorDataContract.DataEntry.COLUMN_NAME_TIMESTAMP + " < ?";
        String[] whereArgs = new String[]{ String.valueOf(deleteOffset) };

        int deleted = 0;
        try {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            if(db != null){
                deleted = db.delete(SensorDataContract.DataEntry.TABLE_NAME, whereClause, whereArgs);
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

        String whereClause = SensorDataContract.DataEntry.COLUMN_NAME_TIMESTAMP + " < ?";
        String[] whereArgs = new String[]{ String.valueOf(offset) };

        int count = 0;
        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            if(db != null){
                Cursor c = db.query(
                        SensorDataContract.DataEntry.TABLE_NAME,
                        new String[]{SensorDataContract.DataEntry._ID},
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

    public Map getData(){

        Map data = new HashMap<>();

        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            if(db != null){
                Cursor c = db.query(
                        SensorDataContract.DataEntry.TABLE_NAME,
                        new String[]{
                                SensorDataContract.DataEntry.COLUMN_NAME_TIMESTAMP,
                                SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_NAME,
                                SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE,
                                SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX,
                        }, null, null, null, null, null, null);
                data = getSensorDataFromCursor(c);
                c.close();
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getWritableDatabase() exception", e);
        }

        return data;
    }

    public Map getLatestData(){

        Map data = new HashMap<>();

        String[] selectColumns = new String[]{
                SensorDataContract.DataEntry.COLUMN_NAME_TIMESTAMP,
                SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_NAME,
                SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE,
                SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX
        };

        String query = "select " +
                TextUtils.join(",", selectColumns) +
                " from " + SensorDataContract.DataEntry.TABLE_NAME +
                " where " + SensorDataContract.DataEntry.COLUMN_NAME_TIMESTAMP + " > coalesce((" +
                " select max(" + SensorDataContract.ActionResult.COLUMN_NAME_DATA_TO_TIME + ")" +
                " from " + SensorDataContract.ActionResult.TABLE_NAME +
                " where " + SensorDataContract.ActionResult.COLUMN_NAME_IS_SUCCESS + " > 0" +
                " ),0)";

        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            if(db != null){
                Cursor c = db.rawQuery(query, new String[]{});
                data = getSensorDataFromCursor(c);
                c.close();
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getWritableDatabase() exception", e);
        }

        return data;
    }

    /**
     * {
     *   "sensor_names": ["sensorName", ...]
     *   "from_time": 0,
     *   "to_time": 10,
     *   "data": {
     *     "sensorName": {
     *       "timestamp": {
     *         "0": "x",
     *         "1": "y",
     *         ...
     *       },
     *       ...
     *     },
     *     ...
     *   }
     * }
     * @return {Map} sensor data
     */
    public Map getSensorDataFromCursor(final Cursor c){

        // TODO: convert to List<DataEntry> and then possibly to ExportObject eliminating Map

        Map resp = new HashMap<>();
        Map<String, Map> data = new HashMap<>();
        List<String> sensors = new ArrayList<>();
        List<Long> times = new ArrayList<>();
        int totalEvents = 0;

        if(c.moveToFirst()){
            while (c.isAfterLast() == false) {
                String name = c.getString(c.getColumnIndex(SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_NAME));
                String timestamp = c.getString(c.getColumnIndex(SensorDataContract.DataEntry.COLUMN_NAME_TIMESTAMP));
                String idx = c.getString(c.getColumnIndex(SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE_INDEX));
                float val = c.getFloat(c.getColumnIndex(SensorDataContract.DataEntry.COLUMN_NAME_SENSOR_DATA_VALUE));

                if(data.get(name) == null){
                    data.put(name, new HashMap<Long, Map>());
                    sensors.add(name);
                }
                Map sensorData = (Map)data.get(name);
                if(sensorData.get(timestamp) == null){
                    sensorData.put(timestamp, new HashMap<String, Float>());
                    totalEvents++;
                    try {
                        times.add(Long.valueOf(timestamp, 10));
                    } catch(NumberFormatException e){}
                }
                Map timestampData = (Map)sensorData.get(timestamp);
                timestampData.put(idx, val);
                sensorData.put(timestamp, timestampData);
                data.put(name, sensorData);
                c.moveToNext();
            }
        }

        resp.put("data", data);
        resp.put("sensors_names", sensors);
        resp.put("total_events", totalEvents);

        if(data.keySet().size() > 0){
            Collections.sort(times);
            resp.put("from_time", times.get(0));
            resp.put("to_time", times.get((times.size() - 1)));
        }

        return resp;
    }

    public void saveExportAction(ActionUrl action){

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(SensorDataContract.ActionUrl.COLUMN_NAME_URL, action.getUrl());
        values.put(SensorDataContract.ActionUrl.COLUMN_NAME_FREQUENCY, action.getFrequency());
        values.put(SensorDataContract.ActionUrl.COLUMN_NAME_TIMESTAMP, action.getTimestamp());
        values.put(SensorDataContract.ActionUrl.COLUMN_NAME_LAST_UPDATED, action.getTimestamp());

        Integer actionId = action.getId();

        try {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            if(db != null){
                if(action.getId() != null){
                    db.update(SensorDataContract.ActionUrl.TABLE_NAME, values, SensorDataContract.ActionUrl._ID+"=?", new String[]{ String.valueOf(action.getId()) });
                } else {
                    actionId = (int)db.insert(SensorDataContract.ActionUrl.TABLE_NAME, null, values);
                }

                // Make sure Alarm is triggered
                Intent i = new Intent(App.getApp(), OnExportAlarmBroadcastReceiver.class);
                i.setAction(Constants.INTENT_ACTION_TRIGGER_EXPORT);
                i.putExtra(Constants.INTENT_KEY_ACTION_URL_ID, actionId);
                sendBroadcast(i);
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getWritableDatabase() exception", e);
        }

    }

    public void saveExportResult(ActionResult result){

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(SensorDataContract.ActionResult.COLUMN_NAME_TIMESTAMP, result.getTimestamp());
        values.put(SensorDataContract.ActionResult.COLUMN_NAME_ACTION_ID, result.getAction_id());
        values.put(SensorDataContract.ActionResult.COLUMN_NAME_IS_SUCCESS, result.is_success());
        values.put(SensorDataContract.ActionResult.COLUMN_NAME_DATA_FROM_TIME, result.getData_from_time());
        values.put(SensorDataContract.ActionResult.COLUMN_NAME_DATA_TO_TIME, result.getData_to_time());

        try {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            if(db != null){
                if(result.getId() != null){
                    db.update(SensorDataContract.ActionResult.TABLE_NAME, values, SensorDataContract.ActionResult._ID+"=?", new String[]{ String.valueOf(result.getId()) });
                } else {
                    db.insert(SensorDataContract.ActionResult.TABLE_NAME, null, values);
                }
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getWritableDatabase() exception", e);
        }

    }

    public List<ActionResult> getActionResultList(ActionUrl action){
        List<ActionResult> data = new ArrayList<>();

        String whereClause = null;
        String[] whereArgs = null;
        String orderBy = SensorDataContract.ActionResult.COLUMN_NAME_TIMESTAMP + " DESC";

        if(action != null){
            whereClause = SensorDataContract.ActionResult.COLUMN_NAME_ACTION_ID + "=?";
            whereArgs = new String[]{ String.valueOf(action.getId()) };
        }

        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            if(db != null){
                Cursor c = db.query(
                        SensorDataContract.ActionResult.TABLE_NAME,
                        new String[]{
                                SensorDataContract.ActionResult._ID,
                                SensorDataContract.ActionResult.COLUMN_NAME_TIMESTAMP,
                                SensorDataContract.ActionResult.COLUMN_NAME_ACTION_ID,
                                SensorDataContract.ActionResult.COLUMN_NAME_IS_SUCCESS,
                                SensorDataContract.ActionResult.COLUMN_NAME_DATA_FROM_TIME,
                                SensorDataContract.ActionResult.COLUMN_NAME_DATA_TO_TIME
                        }, whereClause, whereArgs, null, null, orderBy, null);
                if(c.moveToFirst()){
                    while (c.isAfterLast() == false) {

                        Integer id = c.getInt(c.getColumnIndex(SensorDataContract.ActionResult._ID));
                        long timestamp = c.getLong(c.getColumnIndex(SensorDataContract.ActionResult.COLUMN_NAME_TIMESTAMP));
                        Integer action_id = c.getInt(c.getColumnIndex(SensorDataContract.ActionResult.COLUMN_NAME_ACTION_ID));
                        boolean is_success = c.getInt(c.getColumnIndex(SensorDataContract.ActionResult.COLUMN_NAME_IS_SUCCESS)) > 0;
                        long from = c.getLong(c.getColumnIndex(SensorDataContract.ActionResult.COLUMN_NAME_DATA_FROM_TIME));
                        long to = c.getLong(c.getColumnIndex(SensorDataContract.ActionResult.COLUMN_NAME_DATA_TO_TIME));

                        data.add(new ActionResult(id, timestamp, action_id, is_success, from, to));
                        c.moveToNext();
                    }
                }
                c.close();
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getReadableDatabase() exception", e);
        }

        return data;
    }

    public List<ActionUrl> getActionUrlList(){

        List<ActionUrl> data = new ArrayList<ActionUrl>();

        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            if(db != null){
                Cursor c = db.query(
                        SensorDataContract.ActionUrl.TABLE_NAME,
                        new String[]{
                                SensorDataContract.ActionUrl._ID,
                                SensorDataContract.ActionUrl.COLUMN_NAME_URL,
                                SensorDataContract.ActionUrl.COLUMN_NAME_FREQUENCY,
                                SensorDataContract.ActionUrl.COLUMN_NAME_TIMESTAMP,
                                SensorDataContract.ActionUrl.COLUMN_NAME_LAST_UPDATED
                        }, null, null, null, null, null, null);
                if(c.moveToFirst()){
                    while (c.isAfterLast() == false) {

                        Integer id = c.getInt(c.getColumnIndex(SensorDataContract.ActionUrl._ID));
                        String url = c.getString(c.getColumnIndex(SensorDataContract.ActionUrl.COLUMN_NAME_URL));
                        long freq = c.getLong(c.getColumnIndex(SensorDataContract.ActionUrl.COLUMN_NAME_FREQUENCY));
                        long timestamp = c.getLong(c.getColumnIndex(SensorDataContract.ActionUrl.COLUMN_NAME_TIMESTAMP));
                        long lastUpdated = c.getLong(c.getColumnIndex(SensorDataContract.ActionUrl.COLUMN_NAME_LAST_UPDATED));

                        data.add(new ActionUrl(id, url, freq, timestamp, lastUpdated));
                        c.moveToNext();
                    }
                }
                c.close();
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getReadableDatabase() exception", e);
        }

        return data;

    }

    public ActionUrl getActionUrlById(long actionUrlId){

        ActionUrl data = null;

        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            if(db != null){
                Cursor c = db.query(
                        SensorDataContract.ActionUrl.TABLE_NAME,
                        new String[]{
                                SensorDataContract.ActionUrl._ID,
                                SensorDataContract.ActionUrl.COLUMN_NAME_URL,
                                SensorDataContract.ActionUrl.COLUMN_NAME_FREQUENCY,
                                SensorDataContract.ActionUrl.COLUMN_NAME_TIMESTAMP,
                                SensorDataContract.ActionUrl.COLUMN_NAME_LAST_UPDATED
                        },
                        SensorDataContract.ActionUrl._ID + "=?",
                        new String[]{
                                String.valueOf(actionUrlId)
                        }, null, null, null, null);
                if(c.moveToFirst()){

                    Integer id = c.getInt(c.getColumnIndex(SensorDataContract.ActionUrl._ID));
                    String url = c.getString(c.getColumnIndex(SensorDataContract.ActionUrl.COLUMN_NAME_URL));
                    long freq = c.getLong(c.getColumnIndex(SensorDataContract.ActionUrl.COLUMN_NAME_FREQUENCY));
                    long timestamp = c.getLong(c.getColumnIndex(SensorDataContract.ActionUrl.COLUMN_NAME_TIMESTAMP));
                    long lastUpdated = c.getLong(c.getColumnIndex(SensorDataContract.ActionUrl.COLUMN_NAME_LAST_UPDATED));

                    data = new ActionUrl(id, url, freq, timestamp, lastUpdated);
                    c.moveToNext();
                }
                c.close();
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getReadableDatabase() exception", e);
        }

        return data;

    }

    public int deleteActionUrlRows(ActionUrl url){

        int deleted = 0;

        try {
            if(url != null){

                String whereQuery = SensorDataContract.ActionUrl._ID + "=?";
                String[] args = new String[]{String.valueOf(url.getId())};
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                if(db != null){
                    deleted = db.delete(SensorDataContract.ActionUrl.TABLE_NAME, whereQuery, args);
                }
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getWritableDatabase() exception", e);
        }

        return deleted;
    }

    public int deleteActionResultRows(ActionUrl url){

        int deleted = 0;

        try {
            if(url != null){

                String whereQuery = SensorDataContract.ActionResult.COLUMN_NAME_ACTION_ID + "=?";
                String[] args = new String[]{String.valueOf(url.getId())};
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                if(db != null){
                    deleted = db.delete(SensorDataContract.ActionResult.TABLE_NAME, whereQuery, args);
                }
            }
        } catch(SQLiteException e){
            Log.e("SensorDataDbService", "mDbHelper.getWritableDatabase() exception", e);
        }

        return deleted;
    }
}
