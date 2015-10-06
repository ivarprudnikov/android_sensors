package com.ivarprudnikov.sensors;

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
import android.widget.Switch;
import android.widget.TextView;

import com.ivarprudnikov.sensors.config.Constants;
import com.ivarprudnikov.sensors.config.Preferences;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mHomeSensorListView;
    private View mHomeSensorListHeaderView;
    private SensorManager mSensorManager;
    private List<Sensor> mSensorList;
    private SensorAdapter mSensorAdapter;
    private TextView mDataCountText;
    private StoredSensorEventsCounter.OnQueryResponseListener countListener;
    private Switch isDataStorageEnabledSwitch;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LayoutInflater inflater = getLayoutInflater();

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

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

        // Initiate data counter
        mDataCountText = (TextView)mHomeSensorListHeaderView.findViewById(R.id.dataCount);
        mDataCountText.setText("...");
        countListener = new StoredSensorEventsCounter.OnQueryResponseListener() {
            @Override
            public void OnQueryResponseFinished(String resp) {
                mDataCountText.setText(resp);
            }
        };

        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                final StoredSensorEventsCounter mTask = new StoredSensorEventsCounter(MainActivity.this, countListener);
                mTask.execute();
                h.postDelayed(this, 3000);
            }
        }, 500);

        boolean switchValue = Preferences.isDataStorageEnabled(MainActivity.this);
        isDataStorageEnabledSwitch = (Switch)findViewById(R.id.isDataStorageEnabled);
        isDataStorageEnabledSwitch.setChecked(switchValue);
        isDataStorageEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = Preferences.getPrefs(MainActivity.this).edit();
                editor.putBoolean(Constants.PREFS_IS_SENSOR_LOG_ENABLED, isChecked);
                editor.commit();
            }
        });

        // Construct Intent to start background service
        Intent i = new Intent(this, SensorDataProcessorService.class);
        // Start the service
        startService(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorAdapter.notifyDataSetChanged();
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
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
