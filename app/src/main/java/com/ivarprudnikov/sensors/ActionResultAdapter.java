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

import com.ivarprudnikov.sensors.storage.ActionResult;

import java.util.Date;
import java.util.List;

public class ActionResultAdapter extends ArrayAdapter<ActionResult> {

    // View lookup cache
    private static class ViewHolder {
        TextView line1;
        TextView line2;
        TextView line3;
        TextView line4;
        TextView line5;
    }

    public ActionResultAdapter(Context context, List<ActionResult> actions) {
        super(context, R.layout.activity_export_result_list_item, actions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final ActionResult result = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.activity_export_result_list_item, parent, false);
            viewHolder.line1 = (TextView) convertView.findViewById(R.id.line1);
            viewHolder.line2 = (TextView) convertView.findViewById(R.id.line2);
            viewHolder.line3 = (TextView) convertView.findViewById(R.id.line3);
            viewHolder.line4 = (TextView) convertView.findViewById(R.id.line4);
            viewHolder.line5 = (TextView) convertView.findViewById(R.id.line5);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        convertView.setClickable(false);
        convertView.setFocusable(false);

        Date t = new Date();
        t.setTime(result.getTimestamp());
        viewHolder.line1.setText(t.toString());

        viewHolder.line2.setText(String.valueOf(result.getAction_id()));

        viewHolder.line3.setText(String.valueOf(result.is_success()));

        Date from = new Date();
        from.setTime(result.getData_from_time());
        viewHolder.line4.setText(from.toString());

        Date to = new Date();
        to.setTime(result.getData_to_time());
        viewHolder.line5.setText(to.toString());

        // Return the completed view to render on screen
        return convertView;
    }

}
