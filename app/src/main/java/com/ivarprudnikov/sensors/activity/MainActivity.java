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

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ivarprudnikov.sensors.App;
import com.ivarprudnikov.sensors.AsyncSensorEventsCounter;
import com.ivarprudnikov.sensors.OnAlarmBroadcastReceiver;
import com.ivarprudnikov.sensors.R;
import com.ivarprudnikov.sensors.SensorAdapter;
import com.ivarprudnikov.sensors.config.Constants;
import com.ivarprudnikov.sensors.config.Preferences;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ListView mHomeSensorListView;
    private View mHomeSensorListHeaderView;
    private SensorManager mSensorManager;
    private List<Sensor> mSensorList;
    private SensorAdapter mSensorAdapter;
    private TextView mDataCountText;
    private TextView mStatusText;
    private AsyncSensorEventsCounter.OnQueryResponseListener countListener;
    private CompoundButton isDataStorageEnabledSwitch;
    private Toolbar mToolbar;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LayoutInflater inflater = getLayoutInflater();

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // set shared prefs to register listeners later
        mSharedPreferences = App.getPrefs();

        // Set the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // List of Sensors Available
        mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // set sensor values in view
        mHomeSensorListView = (ListView) findViewById(R.id.listView);

        // Add header view
        mHomeSensorListHeaderView = inflater.inflate(R.layout.activity_main_header, null);
        mHomeSensorListView.addHeaderView(mHomeSensorListHeaderView, null, false);

        // do not allow focus on children as they will swallow events
        mHomeSensorListView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        // attach data
        mSensorAdapter = new SensorAdapter(this, mSensorList);
        mHomeSensorListView.setAdapter(mSensorAdapter);

        // get reference to status
        mStatusText = (TextView)mHomeSensorListHeaderView.findViewById(R.id.status);

        // Initiate data counter
        mDataCountText = (TextView)mHomeSensorListHeaderView.findViewById(R.id.dataCount);
        mDataCountText.setText("...");
        countListener = new AsyncSensorEventsCounter.OnQueryResponseListener() {
            @Override
            public void OnQueryResponseFinished(String resp) {
                mDataCountText.setText(resp);
                syncStatus();
            }
        };

        // create data counter handler which will loop and set fresh value in view
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                final AsyncSensorEventsCounter mTask = new AsyncSensorEventsCounter(MainActivity.this, countListener, null);
                mTask.execute();
                h.postDelayed(this, 3000);
            }
        }, 500);

        boolean switchValue = Preferences.isDataStorageEnabled();
        isDataStorageEnabledSwitch = (CompoundButton)findViewById(R.id.isDataStorageEnabled);
        isDataStorageEnabledSwitch.setChecked(switchValue);
        isDataStorageEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = App.getPrefs().edit();
                editor.putBoolean(Constants.PREFS_IS_SENSOR_LOG_ENABLED, isChecked);
                editor.commit();
            }
        });

        // Construct Intent to start alarm which makes sure
        // that background service is running
        Intent i = new Intent(this, OnAlarmBroadcastReceiver.class);
        sendBroadcast(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorAdapter.notifyDataSetChanged();
        syncStatus();
        syncToggleState();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_view:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.export_view:
                startActivity(new Intent(MainActivity.this, ExportDataActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void syncToggleState(){
        boolean switchValue = Preferences.isDataStorageEnabled();
        isDataStorageEnabledSwitch.setChecked(switchValue);
    }

    public void syncStatus(){
        String eventsCount = mDataCountText.getText().toString();
        if(!Preferences.isDataStorageEnabled() && eventsCount.equals("0")){
            mStatusText.setText("Enable storage first");
            mStatusText.setVisibility(View.VISIBLE);
        } else if(Preferences.isDataStorageEnabled() && !Preferences.areAnySensorListenersEnabled()){
            mStatusText.setText("Now enable sensors");
            mStatusText.setVisibility(View.VISIBLE);
        } else {
            mStatusText.setVisibility(View.GONE);
            mStatusText.setText("");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(Preferences.isRelatedToSensorRegistration(key)){
            syncStatus();
            syncToggleState();
            mSensorAdapter.notifyDataSetChanged();
        }
    }
}
