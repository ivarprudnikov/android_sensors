package com.ivarprudnikov.sensors.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Map;

public class Preferences {

    private static SharedPreferences sSharedPreferences;

    public static SharedPreferences getPrefs(Context ctx){
        if(sSharedPreferences != null){
            return sSharedPreferences;
        }
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sSharedPreferences;
    }

    public static ArrayList<String> getEnabledSensorNames(Context ctx){
        SharedPreferences prefs = getPrefs(ctx);
        Map<String,?> allPrefs = prefs.getAll();
        ArrayList<String> names = new ArrayList<String>();
        for(String key : allPrefs.keySet()){
            if(key.matches(Constants.PREFS_SENSOR_ENABLED_PREFIX + ".*") && prefs.getBoolean(key, false) == true){
                String name = key.replaceFirst(Constants.PREFS_SENSOR_ENABLED_PREFIX, "");
                names.add(name);
            }
        }
        return names;
    }
}
