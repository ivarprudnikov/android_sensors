package com.ivarprudnikov.sensors;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Make sure background service is started
 */
public class OnAlarmBroadcastReceiver extends BroadcastReceiver {

    private static final long START_AT_OFFSET = 1000;    // 1 sec
    private static final long INTERVAL = 5 * 60 * 1000;  // 5 minutes
    private boolean isAlarmSet = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null && action.equals("com.ivarprudnikov.sensors.ACTION_TRIGGER_FROM_BOOT")){
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            OnBootBroadcastReceiver.completeWakefulIntent(intent);
        }
        // Construct Intent to start background service
        Intent i = new Intent(context, SensorDataProcessorService.class);
        // Start the service
        context.startService(i);

        if(!isAlarmSet){
            // call the same receiver after specified time again
            AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent startServiceIntent = new Intent(context, OnAlarmBroadcastReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, startServiceIntent, 0);
            mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + START_AT_OFFSET,
                    INTERVAL,
                    pi);
            isAlarmSet = true;
        }

    }
}
