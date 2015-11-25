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

package com.ivarprudnikov.sensors.adapter;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ivarprudnikov.sensors.App;
import com.ivarprudnikov.sensors.R;
import com.ivarprudnikov.sensors.activity.DisplaySensorDetailsActivity;
import com.ivarprudnikov.sensors.config.Constants;

import java.util.List;

public class SensorAdapter extends ArrayAdapter<Sensor> {

    // View lookup cache
    private static class ViewHolder {
        RadioButton btn;
        TextView line1;
        TextView line2;
    }

    public SensorAdapter(Context context, List<Sensor> sensors) {
        super(context, R.layout.activity_main_list_item, sensors);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Sensor sensor = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.activity_main_list_item, parent, false);
            viewHolder.btn = (RadioButton) convertView.findViewById(R.id.sensorStatus);
            viewHolder.line1 = (TextView) convertView.findViewById(R.id.line1);
            viewHolder.line2 = (TextView) convertView.findViewById(R.id.line2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        convertView.setClickable(true);
        convertView.setFocusable(false);

        // Populate the data into the template view using the data object
        String fSensorKey = Constants.PREFS_SENSOR_ENABLED_PREFIX + sensor.getName();
        boolean isDataStorageEnabled = App.getPrefs().getBoolean(Constants.PREFS_IS_SENSOR_LOG_ENABLED, false);
        boolean switchValue = App.getPrefs().getBoolean(fSensorKey, false);

        viewHolder.btn.setChecked(switchValue);
        viewHolder.btn.setVisibility(RadioButton.INVISIBLE);
        viewHolder.btn.setVisibility(RadioButton.VISIBLE);

        // visually distinguish in case data is being stored
        if(isDataStorageEnabled == true){
            viewHolder.btn.setEnabled(true);
        } else {
            viewHolder.btn.setEnabled(false);
        }
        viewHolder.btn.setClickable(false);
        viewHolder.btn.setFocusable(false);
        viewHolder.line1.setText(sensor.getName());
        viewHolder.line2.setText(sensor.getVendor());

        // attach click listener
        // open detailed view of sensor
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DisplaySensorDetailsActivity.class);
                intent.putExtra(Constants.INTENT_KEY_SENSOR_NAME, sensor.getName());
                getContext().startActivity(intent);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
