package com.ivarprudnikov.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ivarprudnikov.sensors.config.Constants;
import com.ivarprudnikov.sensors.storage.ActionUrl;

public class OnExportAlarmBroadcastReceiver extends BroadcastReceiver {

    private static final long START_OFFSET = 997; // primary number

    public OnExportAlarmBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if(action != null && action.equals("com.ivarprudnikov.sensors.ACTION_TRIGGER_EXPORT")){

            int actionUrlId = intent.getIntExtra(Constants.INTENT_KEY_ACTION_URL_ID, -1);
            processActionUrl(actionUrlId);

            // TODO: check if this trigger still applies, (action url exists?, interval changed?)

            // TODO: check if async task still in progress

            // TODO: execute async task

            // TODO: reschedule alarm for this action

        }

    }

    public void processActionUrl(int id){

        final AsyncActionUrlLoader mTask = new AsyncActionUrlLoader(new AsyncActionUrlLoader.OnQueryResponseListener() {
            @Override
            public void onQueryResponseFinished(ActionUrl result) {

                boolean isEnabled = result != null && result.getFrequency() > 0;
                boolean isChanged = (result.getLast_updated() + result.getFrequency() + START_OFFSET) > System.currentTimeMillis();
                if(isEnabled && !isChanged){

                }


            }
        });

        mTask.execute(id);

    }
}
