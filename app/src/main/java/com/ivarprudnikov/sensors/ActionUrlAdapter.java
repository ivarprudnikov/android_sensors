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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ivarprudnikov.sensors.storage.ActionUrl;

import java.util.List;

public class ActionUrlAdapter extends ArrayAdapter<ActionUrl> {

    // View lookup cache
    private static class ViewHolder {
        TextView line1;
        TextView line2;
    }

    public ActionUrlAdapter(Context context, List<ActionUrl> actions) {
        super(context, R.layout.activity_export_data_list_item, actions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final ActionUrl action = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.activity_export_data_list_item, parent, false);
            viewHolder.line1 = (TextView) convertView.findViewById(R.id.line1);
            viewHolder.line2 = (TextView) convertView.findViewById(R.id.line2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        convertView.setClickable(true);
        convertView.setFocusable(false);

        viewHolder.line1.setText(action.getUrl());
        viewHolder.line2.setText(String.valueOf(action.getFrequency()));

        // Return the completed view to render on screen
        return convertView;
    }
}
