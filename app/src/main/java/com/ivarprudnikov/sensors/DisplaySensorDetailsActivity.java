package com.ivarprudnikov.sensors;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;


public class DisplaySensorDetailsActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private List<Sensor> mSensorList;

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

    ImageButton mOpenChartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sensor_details);

        // Set the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // List of Sensors Available
        mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // get intent that was used to open this view
        Intent intent = getIntent();

        // extract passed data
        String sensorName = intent.getStringExtra(Constants.INTENT_KEY_SENSOR_NAME);

        // figure out which sensor was selected
        Sensor selectedSensor = null;
        for(Sensor s : mSensorList){
            if(s.getName().equals(sensorName)){
                selectedSensor = s;
            }
        }

        final Sensor fSelectedSensor = selectedSensor;

        // if for some reason sensor was not identified
        // and/or if data was not passed to this activity
        // return to list of sensors with some toast message
        if(fSelectedSensor == null){
            Toast.makeText(DisplaySensorDetailsActivity.this, Constants.TEXT_NO_SENSOR_FOUND, Toast.LENGTH_SHORT).show();
            Intent intentToList = new Intent(DisplaySensorDetailsActivity.this, MainActivity.class);
            startActivity(intentToList);
            return;
        }

        ((TextView)findViewById(R.id.sensorDetailsNameValue)).setText(selectedSensor.getName());
        ((TextView)findViewById(R.id.sensorDetailsVendorValue)).setText(selectedSensor.getVendor());
        ((TextView)findViewById(R.id.sensorDetailsTypeValue)).setText(Integer.toString(selectedSensor.getType()));

        if(selectedSensor.getType() < sensorTypes.size())
            ((TextView)findViewById(R.id.sensorDetailsTypeResolvedValue)).setText( sensorTypes.get(selectedSensor.getType()) );
        else
            ((TextView)findViewById(R.id.sensorDetailsTypeResolvedValue)).setText( Constants.TEXT_SENSOR_TYPE_UNRESOLVABLE );

        ((TextView)findViewById(R.id.sensorDetailsVersionValue)).setText(Integer.toString(selectedSensor.getVersion()) );
        ((TextView)findViewById(R.id.sensorDetailsMaxRangeValue)).setText( Float.toString(selectedSensor.getMaximumRange()) );
        ((TextView)findViewById(R.id.sensorDetailsResolutionValue)).setText( Float.toString(selectedSensor.getResolution()) );
        ((TextView)findViewById(R.id.sensorDetailsPowerValue)).setText(Float.toString(selectedSensor.getPower()));
        ((TextView)findViewById(R.id.sensorDetailsMinDelayValue)).setText(Integer.toString(selectedSensor.getMinDelay()));

        // compatibility issues with lower than API ver 20
        try {
            ((TextView)findViewById(R.id.sensorDetailsReportingModeValue)).setText( reportingModes.get(selectedSensor.getReportingMode()));
            ((TextView)findViewById(R.id.sensorDetailsTypeStringValue)).setText(selectedSensor.getStringType());
            ((TextView)findViewById(R.id.sensorDetailsMaxDelayValue)).setText(Integer.toString(selectedSensor.getMaxDelay()));
            ((TextView)findViewById(R.id.sensorDetailsFifoMaxEventValue)).setText(Integer.toString(selectedSensor.getFifoMaxEventCount()));
            ((TextView)findViewById(R.id.sensorDetailsFifoReservedEventValue)).setText(Integer.toString(selectedSensor.getFifoReservedEventCount()));
            ((TextView)findViewById(R.id.sensorDetailsIsWakeUpValue)).setText(Boolean.toString(selectedSensor.isWakeUpSensor()));
        } catch(Exception e){}

        // hidden methods
        //selectedSensor.getRequiredPermission();
        //selectedSensor.getHandle();

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_sensor_details, menu);
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
