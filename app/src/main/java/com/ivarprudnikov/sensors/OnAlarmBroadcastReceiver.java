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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.ivarprudnikov.sensors.config.Constants;

/**
 * Make sure background service is started
 */
public class OnAlarmBroadcastReceiver extends BroadcastReceiver {

    private static final long START_AT_OFFSET = 1000;    // 1 sec
    private static final long INTERVAL = 5 * 60 * 1000;  // 5 minutes
    private static boolean IS_ALARM_SET = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null && action.equals(Constants.INTENT_ACTION_TRIGGER_FROM_BOOT)){
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            OnBootBroadcastReceiver.completeWakefulIntent(intent);
        }
        // Construct Intent to start background service
        Intent i = new Intent(context, SensorDataProcessorService.class);
        // Start the service
        context.startService(i);

        if(!IS_ALARM_SET){
            // call the same receiver after specified time again
            AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent startServiceIntent = new Intent(context, OnAlarmBroadcastReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, startServiceIntent, 0);
            mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + START_AT_OFFSET,
                    INTERVAL,
                    pi);
            IS_ALARM_SET = true;
        }

    }
}
