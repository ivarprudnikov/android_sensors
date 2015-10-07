package com.ivarprudnikov.sensors;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ivarprudnikov.sensors.storage.SensorDataDbService;

public class App extends Application {

    private static App sApp;
    private static SharedPreferences sSharedPreferences;
    private static SensorDataDbService mSensorDataDbService;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
    }

    public static App getApp(){
        return sApp;
    }

    public static SharedPreferences getPrefs(){
        if(sSharedPreferences != null){
            return sSharedPreferences;
        }
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(sApp);
        return sSharedPreferences;
    }

    public static SensorDataDbService getDbService(){
        if(mSensorDataDbService != null){
            return mSensorDataDbService;
        }
        mSensorDataDbService = new SensorDataDbService(sApp);
        return mSensorDataDbService;
    }

}
