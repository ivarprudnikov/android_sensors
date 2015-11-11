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

import android.os.AsyncTask;

import com.ivarprudnikov.sensors.storage.ActionUrl;

public class AsyncActionUrlLoader extends AsyncTask<Integer, Void, ActionUrl> {

    OnQueryResponseListener listener;

    public interface OnQueryResponseListener {
        public void onQueryResponseFinished(ActionUrl operationResponse);
    }

    public AsyncActionUrlLoader(OnQueryResponseListener listener) {
        this.listener = listener;
    }

    @Override
    protected ActionUrl doInBackground(Integer... params) {
        // TODO: load from db
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ActionUrl result) {
        if( listener != null ) {
            listener.onQueryResponseFinished(result);
        }
    }
}
