package com.ivarprudnikov.myapplication;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;


public class DisplaySensorDataActivity extends ActionBarActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private List<Sensor> mSensorList;
    private Sensor mSelectedSensor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sensor_data);

        // Set the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // List of Sensors Available
        mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // get intent that was used to open this view
        Intent intent = getIntent();

        // extract passed data
        String sensorName = intent.getStringExtra(Constants.INTENT_KEY_SENSOR_NAME);

        // figure out which sensor was selected
        for(Sensor s : mSensorList){
            if(s.getName().equals(sensorName)){
                mSelectedSensor = s;
            }
        }

        TextView texName = (TextView)findViewById(R.id.sensorDataNameValue);
        if(mSelectedSensor != null)
            texName.setText(mSelectedSensor.getName());
        else
            texName.setText(Constants.TEXT_NO_SENSOR_FOUND);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_sensor_data, menu);
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

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        // float lux = event.values[0];
        // Do something with this sensor value.

        TextView texData = (TextView)findViewById(R.id.sensorDataDataValue);
        float[] vals = event.values;
        StringBuilder sb = new StringBuilder();

        for(float v : vals){
            sb.append(v);
            sb.append(" \n");
        }

        texData.setText( sb.toString() );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSelectedSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}