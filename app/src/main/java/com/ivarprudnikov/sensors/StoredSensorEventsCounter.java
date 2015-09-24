package com.ivarprudnikov.sensors;

import android.content.Context;
import android.os.AsyncTask;

import com.ivarprudnikov.sensors.storage.SensorDataDbService;

public class StoredSensorEventsCounter extends AsyncTask<Void, Void, String> {

    OnQueryResponseListener listener;
    Context mContext;
    SensorDataDbService mSensorDataDbService;

    public interface OnQueryResponseListener {
        public void OnQueryResponseFinished(String operationResponse);
    }

    public StoredSensorEventsCounter(Context ctx, OnQueryResponseListener listener) {
        this.mContext = ctx;
        this.listener = listener;
        this.mSensorDataDbService = new SensorDataDbService(ctx);
    }

    @Override
    protected String doInBackground(Void... params) {
        int data = this.mSensorDataDbService.countSensorEvents();
        return Integer.valueOf(data).toString();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if( listener != null ) {
            listener.OnQueryResponseFinished(result);
        }
    }
}
