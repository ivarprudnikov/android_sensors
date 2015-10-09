package com.ivarprudnikov.sensors;

import android.content.Context;
import android.os.AsyncTask;

import java.text.DecimalFormatSymbols;

public class StoredSensorEventsCounter extends AsyncTask<Void, Void, String> {

    OnQueryResponseListener listener;
    Context mContext;

    public interface OnQueryResponseListener {
        public void OnQueryResponseFinished(String operationResponse);
    }

    public StoredSensorEventsCounter(Context ctx, OnQueryResponseListener listener) {
        this.mContext = ctx;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        int data = App.getDbService().countSensorEvents();
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
