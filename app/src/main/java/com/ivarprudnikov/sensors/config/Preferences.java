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

    public static boolean isDataStorageEnabled(Context ctx){
        SharedPreferences prefs = getPrefs(ctx);
        return prefs.getBoolean(Constants.PREFS_IS_SENSOR_LOG_ENABLED, false);
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

    public static String getStorageDuration(Context ctx){
        SharedPreferences prefs = getPrefs(ctx);
        return prefs.getString(Constants.PREFS_STORAGE_DURATION, "5 min");
    }

    public static void setStorageDuration(Context ctx, String val){
        SharedPreferences.Editor editor = getPrefs(ctx).edit();
        editor.putString(Constants.PREFS_STORAGE_DURATION, val);
        editor.commit();
    }

    public static String getStorageLimitAction(Context ctx){
        SharedPreferences prefs = getPrefs(ctx);
        return prefs.getString(Constants.PREFS_STORAGE_LIMIT_ACTION, "Overwrite older data");
    }

    public static void setStorageLimitAction(Context ctx, String val){
        SharedPreferences.Editor editor = getPrefs(ctx).edit();
        editor.putString(Constants.PREFS_STORAGE_LIMIT_ACTION, val);
        editor.commit();
    }
}
