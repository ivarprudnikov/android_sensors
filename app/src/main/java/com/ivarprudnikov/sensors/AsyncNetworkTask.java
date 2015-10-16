package com.ivarprudnikov.sensors;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.http.HttpResponse;
import com.ivarprudnikov.sensors.util.HttpUtil;

import java.io.IOException;
import java.util.Map;

public class AsyncNetworkTask extends AsyncTask<Void, Void, String> {

    OnResponseListener listener;
    Context mContext;
    int statusCode;
    String url;
    Map data;

    public interface OnResponseListener {
        public void onResponseFinished(int code);
    }

    public AsyncNetworkTask(Context ctx, OnResponseListener listener, String url, Map data) {
        this.mContext = ctx;
        this.listener = listener;
        this.url = url;
        this.data = data;
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpResponse response = null;
        try {
            response = HttpUtil.post(url, data, null);
        } catch (IOException e){
            statusCode = -1;
        }

        if(response != null){
            statusCode = response.getStatusCode();
            try {
                response.disconnect();
            } catch (IOException e){
                statusCode = -1;
            }

        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if( listener != null ) {
            listener.onResponseFinished(statusCode);
        }
    }
}
