package com.ivarprudnikov.sensors;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Start alarm that regularly triggers {OnAlarmBroadcastReceiver} which makes
 * sure that background service is alive
 */
public class OnBootBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final int INTERVAL = 300000;  // 5 minutes
    private static final int START_AT_OFFSET = 600000;  // 10 minutes

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent startServiceIntent = new Intent(context, OnAlarmBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, startServiceIntent, 0);

        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + START_AT_OFFSET,
                INTERVAL,
                pi);
    }
}
