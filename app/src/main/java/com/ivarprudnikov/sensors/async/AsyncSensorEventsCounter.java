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

package com.ivarprudnikov.sensors.async;

import android.content.Context;
import android.hardware.Sensor;
import android.os.AsyncTask;

import com.ivarprudnikov.sensors.App;

import java.text.DecimalFormatSymbols;

public class AsyncSensorEventsCounter extends AsyncTask<Void, Void, String> {

    OnQueryResponseListener listener;
    Context mContext;
    Sensor sensor;

    public interface OnQueryResponseListener {
        public void OnQueryResponseFinished(String operationResponse);
    }

    public AsyncSensorEventsCounter(Context ctx, OnQueryResponseListener listener, Sensor sensor) {
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
