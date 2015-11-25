package com.ivarprudnikov.sensors;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.ivarprudnikov.sensors.async.AsyncActionUrlNetworkTask;
import com.ivarprudnikov.sensors.config.Constants;
import com.ivarprudnikov.sensors.config.Preferences;
import com.ivarprudnikov.sensors.storage.ActionResult;
import com.ivarprudnikov.sensors.storage.ActionUrl;

import java.util.List;
import java.util.Map;

public class OnExportAlarmBroadcastReceiver extends BroadcastReceiver {

    private static final long START_OFFSET = 997;

    public OnExportAlarmBroadcastReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if(action != null && action.equals(Constants.INTENT_ACTION_TRIGGER_EXPORT)){
            long actionUrlId = intent.getIntExtra(Constants.INTENT_KEY_ACTION_URL_ID, -1);
            // if has time then it is scheduled for execution
            long regTime = intent.getLongExtra(Constants.INTENT_KEY_ACTION_URL_REGISTRATION_TIME, -1);
            new ExportTask().execute(actionUrlId, regTime);
        } else if(action != null && action.equals(Constants.INTENT_ACTION_TRIGGER_FROM_BOOT)){
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            OnBootBroadcastReceiver.completeWakefulIntent(intent);
            reregister();
        } else if(action != null && action.equals(Constants.INTENT_ACTION_TRIGGER_REREGISTER_EXPORT)){
            reregister();
        }
    }

    private void reregister(){
        List<ActionUrl> actions = App.getDbService().getActionUrlList();
        if(actions.size() > 0){
            for( ActionUrl a : actions ){
                if(a.getFrequency() > 0){
                    new ExportTask().execute((long)a.getId());
                }
            }
        }
    }

    private class ExportTask extends AsyncTask<Long, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Long... params) {

            Long actionUrlId = params[0];
            Long registrationTime = params[1];

            if(actionUrlId == null){
                return null;
            }

            final ActionUrl au = App.getDbService().getActionUrlById(actionUrlId);

            // check if this trigger still applies, (action url exists?, interval changed?)
            boolean isEnabled = au != null && au.getFrequency() > 0;

            // check if it is save/update/re-register call
            // calls that need execution have registration timestamp
            ////////////////////////////////////////////////////

            if(isEnabled && (registrationTime == null || registrationTime < 0)){
                Log.d("ExportTask", "only reregistering " + String.valueOf(registrationTime));
                rescheduleAlarm(au);
            } else if (isEnabled) {
                boolean isChanged = au.getLast_updated() > registrationTime;
                if(!isChanged){
                    rescheduleAlarm(au);
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

                    // Post data and store result
                    //////////////////////

                    // TODO: change into service

                    AsyncActionUrlNetworkTask.OnResponseListener latestFinishedListener = new AsyncActionUrlNetworkTask.OnResponseListener() {
                        @Override
                        public void onResponseFinished(int statusCode) {
                            boolean isSuccess = statusCode < 300 && statusCode >= 200;
                            ActionResult ar = new ActionResult(au.getId(), isSuccess, (Long)data.get("from_time"), (Long)data.get("to_time"));
                            App.getDbService().saveExportResult(ar);
                        }
                    };
                    final AsyncActionUrlNetworkTask mTaskLatest = new AsyncActionUrlNetworkTask(App.getApp(), latestFinishedListener, au, data);
                    mTaskLatest.execute();
                } else {
                    Log.d("ExportTask", "prefs disabled");
                }
            } else {
                Log.d("ExportTask", "export disabled");
            }

            return null;
        }

        void rescheduleAlarm(ActionUrl au){
            Context ctx = App.getApp();
            AlarmManager mgr = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(ctx, OnExportAlarmBroadcastReceiver.class);
            alarmIntent.setAction(Constants.INTENT_ACTION_TRIGGER_EXPORT);

            alarmIntent.putExtra(Constants.INTENT_KEY_ACTION_URL_ID, au.getId());
            alarmIntent.putExtra(Constants.INTENT_KEY_ACTION_URL_REGISTRATION_TIME, System.currentTimeMillis() );

            PendingIntent pi = PendingIntent.getBroadcast(ctx, 0, alarmIntent, 0);
            mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + START_OFFSET + au.getFrequency(),
                    pi);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

}
