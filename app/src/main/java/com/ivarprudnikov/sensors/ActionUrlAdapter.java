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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ivarprudnikov.sensors.activity.ExportDataFormActivity;
import com.ivarprudnikov.sensors.activity.ExportResult;
import com.ivarprudnikov.sensors.storage.ActionResult;
import com.ivarprudnikov.sensors.storage.ActionUrl;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ActionUrlAdapter extends ArrayAdapter<ActionUrl> {

    // View lookup cache
    private static class ViewHolder {
        TextView line1;
        TextView line2;
        TextView line3;
        ImageButton menuTrigger;
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
            viewHolder.line3 = (TextView) convertView.findViewById(R.id.line3);
            viewHolder.menuTrigger = (ImageButton) convertView.findViewById(R.id.menuTrigger);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.menuTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog(action);
            }
        });

        convertView.setClickable(true);
        convertView.setFocusable(false);

        viewHolder.line1.setText(action.getUrl());

        if(action.getFrequency() > 0)
            viewHolder.line2.setText(String.valueOf(action.getFrequency()));
        else
            viewHolder.line2.setText("Disabled");

        Date t = new Date();
        t.setTime(action.getTimestamp());
        viewHolder.line3.setText(t.toString());

        // Return the completed view to render on screen
        return convertView;
    }

    private void showOptionsDialog(final ActionUrl action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final String[] actionsArray = new String[]{ "Send all data", "Send latest data", "Edit", "Delete", "Export results" };
        builder.setTitle("Options")
                .setItems(actionsArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                sendAllData(action);
                                break;
                            case 1:
                                sendLatestData(action);
                                break;
                            case 2:
                                openEditActionActivity(action);
                                break;
                            case 3:
                                showDeleteConfirmationDialog(action);
                                break;
                            case 4:
                                openExportResultsActivity(action);
                                break;
                        }
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void sendAllData(final ActionUrl action){
        sendActionData(action, App.getDbService().getData());
    }

    private void sendLatestData(final ActionUrl action){
        sendActionData(action, App.getDbService().getLatestData());
    }

    private void sendActionData(final ActionUrl action, final Map data){
        AsyncNetworkTask.OnResponseListener latestFinishedListener = new AsyncNetworkTask.OnResponseListener() {
            @Override
            public void onResponseFinished(int statusCode) {
                boolean isSuccess = statusCode < 300 && statusCode >= 200;
                ActionResult ar = new ActionResult(action.getId(), isSuccess, (Long)data.get("from_time"), (Long)data.get("to_time"));
                App.getDbService().saveExportResult(ar);
                Toast.makeText(getContext(), "Got response status: " + String.valueOf(statusCode), Toast.LENGTH_SHORT).show();
            }
        };
        final AsyncNetworkTask mTaskLatest = new AsyncNetworkTask(getContext(), latestFinishedListener, action.getUrl(), data);
        mTaskLatest.execute();
    }

    private void openEditActionActivity(ActionUrl action){
        Bundle bundle = new Bundle();
        bundle.putParcelable("item", action);
        Intent intent = new Intent(getContext(), ExportDataFormActivity.class);
        intent.putExtras(bundle);
        getContext().startActivity(intent);
    }

    private void openExportResultsActivity(ActionUrl action){
        Bundle bundle = new Bundle();
        bundle.putParcelable("item", action);
        Intent intent = new Intent(getContext(), ExportResult.class);
        intent.putExtras(bundle);
        getContext().startActivity(intent);
    }

    private void showDeleteConfirmationDialog(final ActionUrl action) {
        new AlertDialog.Builder(getContext())
                .setTitle("Are you sure?")
                .setMessage("Do you want to permanently remove action with url: " + action.getUrl())
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        App.getDbService().deleteActionUrlRows(action);
                        remove(action);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do Something Here
                    }
                }).show();
    }
}
