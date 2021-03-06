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

package com.ivarprudnikov.sensors.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ivarprudnikov.sensors.App;
import com.ivarprudnikov.sensors.AsyncSensorEventsCounter;
import com.ivarprudnikov.sensors.R;
import com.ivarprudnikov.sensors.config.Constants;
import com.ivarprudnikov.sensors.config.Preferences;

import java.util.Arrays;
import java.util.List;


public class DisplaySensorDetailsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SensorManager mSensorManager;
    private List<Sensor> mSensorList;
    private CompoundButton isSensorEnabledSwitch;
    ImageButton mOpenChartButton;
    private Toolbar mToolbar;
    private Sensor mSelectedSensor = null;
    private TextView mDataCountText;
    private TextView mStatusText;
    private AsyncSensorEventsCounter.OnQueryResponseListener countListener;
    private SharedPreferences mSharedPreferences;

    public List<String> sensorTypes = Arrays.asList(
            "", // padding because sensor types start at 1
            "TYPE_ACCELEROMETER",
            "TYPE_MAGNETIC_FIELD",
            "TYPE_ORIENTATION",
            "TYPE_GYROSCOPE",
            "TYPE_LIGHT",
            "TYPE_PRESSURE",
            "TYPE_TEMPERATURE",
            "TYPE_PROXIMITY",
            "TYPE_GRAVITY",
            "TYPE_LINEAR_ACCELERATION",
            "TYPE_ROTATION_VECTOR",
            "TYPE_RELATIVE_HUMIDITY",
            "TYPE_AMBIENT_TEMPERATURE",
            "TYPE_MAGNETIC_FIELD_UNCALIBRATED",
            "TYPE_GAME_ROTATION_VECTOR",
            "TYPE_GYROSCOPE_UNCALIBRATED",
            "TYPE_SIGNIFICANT_MOTION",
            "TYPE_STEP_DETECTOR",
            "TYPE_STEP_COUNTER",
            "TYPE_GEOMAGNETIC_ROTATION_VECTOR",
            "TYPE_HEART_RATE",
            "TYPE_TILT_DETECTOR",
            "TYPE_WAKE_GESTURE",
            "TYPE_GLANCE_GESTURE",
            "TYPE_PICK_UP_GESTURE");

    public List<String> reportingModes = Arrays.asList(
            "REPORTING_MODE_CONTINUOUS",
            "REPORTING_MODE_ON_CHANGE",
            "REPORTING_MODE_ONE_SHOT",
            "REPORTING_MODE_SPECIAL_TRIGGER");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sensor_details);

        // prepare toolbar
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // set shared prefs to register listeners later
        mSharedPreferences = App.getPrefs();

        // Set the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // List of Sensors Available
        mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // get intent that was used to open this view
        Intent intent = getIntent();

        // extract passed data
        final String fSensorName = intent.getStringExtra(Constants.INTENT_KEY_SENSOR_NAME);

        // figure out which sensor was selected
        for(Sensor s : mSensorList){
            if(s.getName().equals(fSensorName)){
                mSelectedSensor = s;
            }
        }

        final Sensor fSelectedSensor = mSelectedSensor;

        // if for some reason sensor was not identified
        // and/or if data was not passed to this activity
        // return to list of sensors with some toast message
        if(fSelectedSensor == null){
            Toast.makeText(DisplaySensorDetailsActivity.this, Constants.TEXT_NO_SENSOR_FOUND, Toast.LENGTH_SHORT).show();
            Intent intentToList = new Intent(DisplaySensorDetailsActivity.this, MainActivity.class);
            startActivity(intentToList);
            return;
        }

        ((TextView)findViewById(R.id.sensorDetailsNameValue)).setText(mSelectedSensor.getName());
        ((TextView)findViewById(R.id.sensorDetailsVendorValue)).setText(mSelectedSensor.getVendor());
        ((TextView)findViewById(R.id.sensorDetailsTypeValue)).setText(Integer.toString(mSelectedSensor.getType()));

        if(mSelectedSensor.getType() < sensorTypes.size())
            ((TextView)findViewById(R.id.sensorDetailsTypeResolvedValue)).setText( sensorTypes.get(mSelectedSensor.getType()) );
        else
            ((TextView)findViewById(R.id.sensorDetailsTypeResolvedValue)).setText( Constants.TEXT_SENSOR_TYPE_UNRESOLVABLE );

        ((TextView)findViewById(R.id.sensorDetailsVersionValue)).setText(Integer.toString(mSelectedSensor.getVersion()) );
        ((TextView)findViewById(R.id.sensorDetailsMaxRangeValue)).setText( Float.toString(mSelectedSensor.getMaximumRange()) );
        ((TextView)findViewById(R.id.sensorDetailsResolutionValue)).setText( Float.toString(mSelectedSensor.getResolution()) );
        ((TextView)findViewById(R.id.sensorDetailsPowerValue)).setText(Float.toString(mSelectedSensor.getPower()));
        ((TextView)findViewById(R.id.sensorDetailsMinDelayValue)).setText(Integer.toString(mSelectedSensor.getMinDelay()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((TextView)findViewById(R.id.sensorDetailsFifoMaxEventValue)).setText(Integer.toString(mSelectedSensor.getFifoMaxEventCount()));
            ((TextView)findViewById(R.id.sensorDetailsFifoReservedEventValue)).setText(Integer.toString(mSelectedSensor.getFifoReservedEventCount()));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            ((TextView)findViewById(R.id.sensorDetailsTypeStringValue)).setText(mSelectedSensor.getStringType());
        }

        if (Build.VERSION.SDK_INT >= 21) {
            ((TextView)findViewById(R.id.sensorDetailsReportingModeValue)).setText( reportingModes.get(mSelectedSensor.getReportingMode()));
            ((TextView)findViewById(R.id.sensorDetailsMaxDelayValue)).setText(Integer.toString(mSelectedSensor.getMaxDelay()));
            ((TextView)findViewById(R.id.sensorDetailsIsWakeUpValue)).setText(Boolean.toString(mSelectedSensor.isWakeUpSensor()));
        }

        // hidden methods
        //mSelectedSensor.getRequiredPermission();
        //mSelectedSensor.getHandle();

        // Handle button tap
        mOpenChartButton = (ImageButton) findViewById(R.id.chartButton);
        mOpenChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplaySensorDetailsActivity.this, DisplaySensorDataActivity.class);
                intent.putExtra(Constants.INTENT_KEY_SENSOR_NAME, fSelectedSensor.getName());
                startActivity(intent);
            }
        });

        // prepare sensor toggle
        final String fSensorKey = Constants.PREFS_SENSOR_ENABLED_PREFIX + fSensorName;
        boolean switchValue = App.getPrefs().getBoolean(fSensorKey, false);
        isSensorEnabledSwitch = (CompoundButton)findViewById(R.id.isSensorListenerEnabled);
        isSensorEnabledSwitch.setChecked(switchValue);
        isSensorEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = App.getPrefs().edit();
                editor.putBoolean(fSensorKey, isChecked);
                editor.commit();
            }
        });

        // Initiate data counter
        mDataCountText = (TextView)findViewById(R.id.dataCount);
        mDataCountText.setText("...");
        countListener = new AsyncSensorEventsCounter.OnQueryResponseListener() {
            @Override
            public void OnQueryResponseFinished(String resp) {
                mDataCountText.setText(resp);
            }
        };

        // get reference to status
        mStatusText = (TextView)findViewById(R.id.status);

        // create data counter handler which will loop and set fresh value in view
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                final AsyncSensorEventsCounter mTask = new AsyncSensorEventsCounter(DisplaySensorDetailsActivity.this, countListener, mSelectedSensor);
                mTask.execute();
                h.postDelayed(this, 3000);
            }
        }, 500);

    }

    @Override
    protected void onResume() {
        super.onResume();
        syncStatus();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(Preferences.isRelatedToSensorRegistration(key)){
            syncStatus();
        }
    }

    public void syncStatus(){

        boolean switchValue = false;
        if(mSelectedSensor != null){
            String sensorKey = Constants.PREFS_SENSOR_ENABLED_PREFIX + mSelectedSensor.getName();
            switchValue = App.getPrefs().getBoolean(sensorKey, false);
        }

        // give a hint to user if he anticipates data storage
        if(!Preferences.isDataStorageEnabled() && switchValue == true){
            mStatusText.setText("Data storage is disabled");
            mStatusText.setVisibility(View.VISIBLE);
        } else {
            mStatusText.setVisibility(View.GONE);
            mStatusText.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensor_details, menu);
        return true;
    }

    private void showDeleteConfirmationDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rowCount = App.getDbService().deleteSensorRows(mSelectedSensor);
                        Toast.makeText(DisplaySensorDetailsActivity.this, String.valueOf(rowCount) + " items removed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do Something Here
                    }
                }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_action:
                showDeleteConfirmationDialog("Delete selected sensor data?", "This cannot be undone.");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
