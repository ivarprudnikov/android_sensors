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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.google.api.client.http.HttpResponse;
import com.ivarprudnikov.sensors.config.Constants;
import com.ivarprudnikov.sensors.config.Preferences;
import com.ivarprudnikov.sensors.storage.ActionResult;
import com.ivarprudnikov.sensors.storage.ActionUrl;
import com.ivarprudnikov.sensors.util.HttpUtil;

import java.io.IOException;
import java.util.Map;

public class SensorDataSenderService extends Service {

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;

    private static final long START_OFFSET = 997;

    public SensorDataSenderService(){}

    // Define how the handler will process messages
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        // Define how to handle any incoming messages here
        @Override
        public void handleMessage(Message message) {

            Bundle b = message.getData();

            long actionUrlId = b.getInt(Constants.INTENT_KEY_ACTION_URL_ID, -1);
            // if has time then it is scheduled for execution
            long registrationTime = b.getLong(Constants.INTENT_KEY_ACTION_URL_REGISTRATION_TIME, -1);

            final ActionUrl au = App.getDbService().getActionUrlById(actionUrlId);

            // check if this trigger still applies, (action url exists?, interval changed?)
            boolean isEnabled = au != null && au.getFrequency() > 0;

            // check if it is save/update/re-register call
            // calls that need execution have registration timestamp
            ////////////////////////////////////////////////////

            if(isEnabled && registrationTime < 0){
                Log.d("ExportTask", "only reregistering " + String.valueOf(registrationTime));
                reschedule(au);
            } else if (isEnabled) {
                boolean isChanged = au.getLast_updated() > registrationTime;
                if(!isChanged){
                    reschedule(au);
                    Log.d("ExportTask", "reregistering and executing");
                } else {
                    Log.d("ExportTask", "only executing");
                }

                // check if data storage is enabled
                // and if subscribed to any sensors at all
                //////////////////////

                if(Preferences.isDataStorageEnabled() && Preferences.areAnySensorListenersEnabled()){

                    Log.d("ExportTask", "prefs enabled");

                    // TODO: check if async task still in progress, otherwise will result in posting duplicated data

                    // Load latest data
                    //////////////////////

                    final Map data = App.getDbService().getLatestData();

                    int statusCode = postData(au, data);

                    storeResult(au, data, statusCode);

                } else {
                    Log.d("ExportTask", "prefs disabled");
                }
            } else {
                Log.d("ExportTask", "export disabled");
            }
        }

        int postData(ActionUrl au, Map data){

            HttpResponse response = null;
            int statusCode = -1;
            try {
                response = HttpUtil.post(au.getUrl(), data, null);
            } catch (IOException e){
            }

            if(response != null){
                statusCode = response.getStatusCode();
                try {
                    response.disconnect();
                } catch (IOException e){
                }
            }

            return statusCode;
        }

        void storeResult(ActionUrl au, Map data, int statusCode){
            boolean isSuccess = statusCode < 300 && statusCode >= 200;
            ActionResult ar = new ActionResult(au.getId(), isSuccess, (Long)data.get("from_time"), (Long)data.get("to_time"));
            App.getDbService().saveExportResult(ar);
        }

        /**
         * Trigger receiver via alarm which will start this service again
         * @param au
         */
        void reschedule(ActionUrl au){

            Context ctx = App.getApp();
            AlarmManager mgr = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(ctx, OnExportAlarmBroadcastReceiver.class);
            i.setAction(Constants.INTENT_ACTION_TRIGGER_EXPORT);
            i.putExtra(Constants.INTENT_KEY_ACTION_URL_ID, au.getId());
            i.putExtra(Constants.INTENT_KEY_ACTION_URL_REGISTRATION_TIME, System.currentTimeMillis() );

            PendingIntent pi = PendingIntent.getBroadcast(ctx, 0, i, 0);
            mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + START_OFFSET + au.getFrequency(),
                    pi);
        }
    }

    // Fires when a service is first initialized
    public void onCreate() {
        super.onCreate();

        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("SensorDataSenderService.HandlerThread");
        mHandlerThread.start();

        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());
    }

    /**
     * Fires when a service is started up
     * And every time it is being triggered by
     * activities or alarm
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle intentData = intent.getExtras();
        if(intentData != null){
            Message msg = new Message();
            msg.setData(intentData);
            mServiceHandler.sendMessage(msg);
        }
        // Keep service around "sticky"
        return START_STICKY;
    }

    // Defines the shutdown sequence
    @Override
    public void onDestroy() {
        // Cleanup service before destruction
        mHandlerThread.quit();
    }

    // Binding is another way to communicate between service and activity
    // Not needed here, local broadcasts will be used instead
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
