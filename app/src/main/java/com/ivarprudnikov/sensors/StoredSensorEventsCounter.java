package com.ivarprudnikov.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.os.AsyncTask;

import java.text.DecimalFormatSymbols;

public class StoredSensorEventsCounter extends AsyncTask<Void, Void, String> {

    OnQueryResponseListener listener;
    Context mContext;
    Sensor sensor;

    public interface OnQueryResponseListener {
        public void OnQueryResponseFinished(String operationResponse);
    }

    public StoredSensorEventsCounter(Context ctx, OnQueryResponseListener listener, Sensor sensor) {
        this.mContext = ctx;
        this.listener = listener;
        this.sensor = sensor;
    }

    @Override
    protected String doInBackground(Void... params) {
        int data = App.getDbService().countSensorEvents(sensor);
        if(data < 0){
            return DecimalFormatSymbols.getInstance().getInfinity();
        }
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
