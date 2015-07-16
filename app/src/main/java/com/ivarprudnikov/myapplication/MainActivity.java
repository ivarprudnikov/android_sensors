package com.ivarprudnikov.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mHomeSensorListView;
    private SensorManager mSensorManager;
    private List<Sensor> mSensorList;
    private List<String> mSensorNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Activity currentActivity = this;

        // Set the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // List of Sensors Available
        mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        mSensorNames = new ArrayList<String>();
        for(Sensor s : mSensorList){
            mSensorNames.add(s.getName());
        }

        // construct list holder for view
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                mSensorNames );

        // set sensor values in view
        mHomeSensorListView = (ListView) findViewById(R.id.listView);

        // do not allow focus on children as they will swallow events
        mHomeSensorListView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        // attach data
        mHomeSensorListView.setAdapter(arrayAdapter);

        // attach click listener
        // open detailed view of sensor
        mHomeSensorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(currentActivity, DisplaySensorDetailsActivity.class);
                Sensor selectedSensor = mSensorList.get(position);
                selectedSensor.getName();
                intent.putExtra(Constants.INTENT_KEY_SENSOR_NAME, selectedSensor.getName());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
