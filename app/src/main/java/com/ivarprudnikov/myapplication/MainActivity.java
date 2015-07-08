package com.ivarprudnikov.myapplication;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView homeSensorListView;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // List of Sensors Available
        List<Sensor> mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        List<String> sensorNames = new ArrayList<String>();
        for(Sensor s : mSensorList){
            sensorNames.add(s.getName());
        }

        // construct list holder for view
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                sensorNames );

        // set sensor values in view
        homeSensorListView = (ListView) findViewById(R.id.listView);

        // do not allow focus on children as they will swallow events
        homeSensorListView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        // attach data
        homeSensorListView.setAdapter(arrayAdapter);

        // attach header
        // TODO
        // homeSensorListView.addHeaderView(arrayAdapter);

        // attach click listener
        homeSensorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String info = ((TextView) arg1).getText().toString();
                Toast.makeText(getBaseContext(), "Item " + info, Toast.LENGTH_LONG).show();
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
