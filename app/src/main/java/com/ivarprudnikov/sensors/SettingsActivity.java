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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.ivarprudnikov.sensors.config.Preferences;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button mDeleteButton = (Button)findViewById(R.id.deleteDataButton);
        mDeleteButton.setClickable(true);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog("Delete all data?", "This cannot be undone. You could try deleting individual sensor data first.");
            }
        });

        Spinner spinnerHours = (Spinner) findViewById(R.id.hours);
        ArrayAdapter<CharSequence> adapterHours = ArrayAdapter.createFromResource(this,
                R.array.available_durations_in_hours, android.R.layout.simple_spinner_item);
        adapterHours.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHours.setAdapter(adapterHours);
        String selectedDuration = Preferences.getStorageDuration();
        spinnerHours.setSelection(adapterHours.getPosition(selectedDuration));
        spinnerHours.setOnItemSelectedListener(this);

        Spinner spinnerLimitAction = (Spinner) findViewById(R.id.limitAction);
        ArrayAdapter<CharSequence> adapterLimitAction = ArrayAdapter.createFromResource(this,
                R.array.data_limit_action, android.R.layout.simple_spinner_item);
        adapterLimitAction.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLimitAction.setAdapter(adapterLimitAction);
        String selectedLimitAction = Preferences.getStorageLimitAction();
        spinnerLimitAction.setSelection(adapterLimitAction.getPosition(selectedLimitAction));
        spinnerLimitAction.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void showDeleteConfirmationDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rowCount = App.getDbService().deleteAllRows();
                        Toast.makeText(SettingsActivity.this, String.valueOf(rowCount) + " items removed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do Something Here
                    }
                }).show();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        String selectedValue = (String)parent.getItemAtPosition(pos);

        switch (parent.getId()){
            case R.id.hours:
                Preferences.setStorageDuration(selectedValue);
                break;
            case R.id.limitAction:
                Preferences.setStorageLimitAction(selectedValue);
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
