package com.ivarprudnikov.sensors;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Start alarm that will trigger {OnAlarmBroadcastReceiver} which makes
 * sure that background service is alive
 */
public class OnBootBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final long START_AT_OFFSET = 1 * 60 * 1000;  // 1 minute

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent startServiceIntent = new Intent(context, OnAlarmBroadcastReceiver.class);
        startServiceIntent.setAction("com.ivarprudnikov.sensors.ACTION_TRIGGER_FROM_BOOT");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, startServiceIntent, 0);

        mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + START_AT_OFFSET,
                pi);
    }
}
