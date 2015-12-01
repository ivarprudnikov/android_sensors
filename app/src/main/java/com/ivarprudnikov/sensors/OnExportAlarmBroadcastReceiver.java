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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ivarprudnikov.sensors.config.Constants;
import com.ivarprudnikov.sensors.storage.ActionUrl;

import java.util.List;

public class OnExportAlarmBroadcastReceiver extends BroadcastReceiver {

    public OnExportAlarmBroadcastReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if(action != null && action.equals(Constants.INTENT_ACTION_TRIGGER_EXPORT)){
            Intent i = (Intent)intent.clone();
            i.setClass(context, SensorDataSenderService.class);
            context.startService(i);
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
                    Context ctx = App.getApp();
                    Intent i = new Intent(ctx, SensorDataSenderService.class);
                    i.putExtra(Constants.INTENT_KEY_ACTION_URL_ID, a.getId());
                    ctx.startService(i);
                }
            }
        }
    }

}
