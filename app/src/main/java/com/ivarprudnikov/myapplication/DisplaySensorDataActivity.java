package com.ivarprudnikov.myapplication;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class DisplaySensorDataActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private List<Sensor> mSensorList;
    private Sensor mSelectedSensor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sensor_data);

        // keep this activity alive as data is always refreshed on screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

        // if for some reason sensor was not identified
        // and/or if data was not passed to this activity
        // return to list of sensors with some toast message
        if(mSelectedSensor == null){
            Toast.makeText(DisplaySensorDataActivity.this, Constants.TEXT_NO_SENSOR_FOUND, Toast.LENGTH_SHORT).show();
            Intent intentToList = new Intent(DisplaySensorDataActivity.this, MainActivity.class);
            startActivity(intentToList);
            return;
        }

        ((TextView)findViewById(R.id.sensorDataNameValue)).setText(mSelectedSensor.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_sensor_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(DisplaySensorDataActivity.this, DisplaySensorDetailsActivity.class);
                intent.putExtra(Constants.INTENT_KEY_SENSOR_NAME, mSelectedSensor.getName());
                startActivity(intent);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {

        TextView texAccuracy = (TextView)findViewById(R.id.sensorDataAccuracyValue);

        switch(accuracy){
            case SensorManager.SENSOR_STATUS_NO_CONTACT:
                texAccuracy.setText("SENSOR_STATUS_NO_CONTACT");
                break;
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                texAccuracy.setText("SENSOR_STATUS_UNRELIABLE");
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                texAccuracy.setText("SENSOR_STATUS_ACCURACY_LOW");
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                texAccuracy.setText("SENSOR_STATUS_ACCURACY_MEDIUM");
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                texAccuracy.setText("SENSOR_STATUS_ACCURACY_HIGH");
                break;
            default:
                texAccuracy.setText("OTHER");
                break;

        }
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

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
