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
