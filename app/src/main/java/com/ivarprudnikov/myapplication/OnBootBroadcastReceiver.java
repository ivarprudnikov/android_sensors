package com.ivarprudnikov.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Start alarm that makes sends intents to background service
 * to be sure that it is started
 */
public class OnBootBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final int PERIOD = 300000;  // 5 minutes

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent startServiceIntent = new Intent(context, OnAlarmBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, startServiceIntent, 0);

        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 60000,
                PERIOD,
                pi);
    }
}
