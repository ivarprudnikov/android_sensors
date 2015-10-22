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

package com.ivarprudnikov.sensors.config;

import android.content.SharedPreferences;

import com.ivarprudnikov.sensors.App;

import java.util.ArrayList;
import java.util.Map;

public class Preferences {

    public static boolean isRelatedToSensorRegistration(String key){
        return key.matches(Constants.PREFS_SENSOR_ENABLED_PREFIX + ".*") ||
                key.matches(Constants.PREFS_IS_SENSOR_LOG_ENABLED);
    }

    public static boolean isDataStorageEnabled(){
        return App.getPrefs().getBoolean(Constants.PREFS_IS_SENSOR_LOG_ENABLED, false);
    }

    public static boolean areAnySensorListenersEnabled(){
        return getEnabledSensorNames().size() > 0;
    }

    public static void setDataStorageEnabled(boolean val){
        SharedPreferences.Editor editor = App.getPrefs().edit();
        editor.putBoolean(Constants.PREFS_IS_SENSOR_LOG_ENABLED, val);
        editor.apply();
    }

    public static ArrayList<String> getEnabledSensorNames(){
        SharedPreferences prefs = App.getPrefs();
        Map<String,?> allPrefs = prefs.getAll();
        ArrayList<String> names = new ArrayList<>();
        for(String key : allPrefs.keySet()){
            if(key.matches(Constants.PREFS_SENSOR_ENABLED_PREFIX + ".*") && prefs.getBoolean(key, false)){
                String name = key.replaceFirst(Constants.PREFS_SENSOR_ENABLED_PREFIX, "");
                names.add(name);
            }
        }
        return names;
    }

    public static String getStorageDuration(){
        return App.getPrefs().getString(Constants.PREFS_STORAGE_DURATION, "5 min");
    }

    public static void setStorageDuration(String val){
        SharedPreferences.Editor editor = App.getPrefs().edit();
        editor.putString(Constants.PREFS_STORAGE_DURATION, val);
        editor.apply();
    }

    public static String getStorageLimitAction(){
        return App.getPrefs().getString(Constants.PREFS_STORAGE_LIMIT_ACTION, "Overwrite older data");
    }

    public static void setStorageLimitAction(String val){
        SharedPreferences.Editor editor = App.getPrefs().edit();
        editor.putString(Constants.PREFS_STORAGE_LIMIT_ACTION, val);
        editor.apply();
    }

    public static boolean isStorageLimitStop(){
        return getStorageLimitAction().equals("Turn off data storage");
    }
    public static boolean isStorageLimitOverwrite(){
        return getStorageLimitAction().equals("Overwrite older data");
    }
}
