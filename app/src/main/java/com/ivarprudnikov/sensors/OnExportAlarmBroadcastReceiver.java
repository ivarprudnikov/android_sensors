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
