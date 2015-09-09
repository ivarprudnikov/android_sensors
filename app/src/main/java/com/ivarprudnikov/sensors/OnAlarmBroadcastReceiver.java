package com.ivarprudnikov.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Make sure background service is started
 */
public class OnAlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        OnBootBroadcastReceiver.completeWakefulIntent(intent);
        // Construct Intent to start background service
        Intent i = new Intent(context, SensorDataProcessorService.class);
        // Start the service
        context.startService(i);
    }
}
