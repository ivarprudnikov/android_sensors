package com.ivarprudnikov.sensors.storage;

import android.content.Context;
import android.content.ContextWrapper;
import android.hardware.SensorEvent;
import android.os.Environment;
import android.util.Log;

import com.ivarprudnikov.sensors.config.Constants;
import com.ivarprudnikov.sensors.config.Preferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class SensorDataLogService extends ContextWrapper {

    public SensorDataLogService (Context ctx){
        super(ctx);
    }

    public void write(SensorEvent event){
        float[] vals = event.values;
        StringBuilder sb = new StringBuilder();
        for(float v : vals){
            sb.append(v);
            sb.append(" \n");
        }
        write(sb.toString());
    }

    public void write(String text){

        boolean sensorLogEnabled = Preferences.getPrefs(SensorDataLogService.this).getBoolean(Constants.PREFS_IS_SENSOR_LOG_ENABLED, false);
        if(sensorLogEnabled == false){
            return;
        }

        File log = new File(Environment.getExternalStorageDirectory(), Constants.PREFS_SENSOR_LOG_FILE_NAME);
        boolean shouldAppendToFile = log.exists();
        String message = text + " - " + new Date().toString();
        try {
            FileWriter fw = new FileWriter(log.getAbsolutePath(), shouldAppendToFile);
            BufferedWriter out = new BufferedWriter(fw);
            out.write(message);
            out.write("\n");
            out.close();
            Log.i("SensorDataLogService", message);
        }
        catch (IOException e) {
            Log.e("SensorDataLogService", "Exception appending to log file", e);
        }
    }
}
