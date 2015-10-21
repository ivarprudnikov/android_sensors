package com.ivarprudnikov.sensors;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ivarprudnikov.sensors.config.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DisplaySensorDataActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private List<Sensor> mSensorList;
    private Sensor mSelectedSensor = null;
    private LineChart mChart;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sensor_data);

        // prepare toolbar
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplaySensorDataActivity.this, DisplaySensorDetailsActivity.class);
                intent.putExtra(Constants.INTENT_KEY_SENSOR_NAME, mSelectedSensor.getName());
                startActivity(intent);
            }
        });

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


        mChart = (LineChart) findViewById(R.id.chart);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.getAxisLeft().setStartAtZero(false);
        mChart.getAxisRight().setStartAtZero(false);

    }

    private void setChartData(float[] sensorValues) {

        for (int i = 0; i < sensorValues.length; i++) {
            LineDataSet data = mChart.getLineData().getDataSetByIndex(i);
            List<Entry> yVals = data.getYVals();
            int yValsSize = yVals.size();
            Entry entr = new Entry(sensorValues[i], yValsSize);

            if(yValsSize < Constants.CHART_MAX_HORIZONTAL_POINTS) {
                data.addEntry(entr);
            } else {
                data.removeEntry(0);

                // reduce index of each entry
                // as it is what defines location of point
                // on x axis. This will get effect of moving
                // chart.
                for (int j = 0; j < yVals.size(); j++) {
                    yVals.get(j).setXIndex(j);
                }

                data.addEntry(entr);
            }

        }
        mChart.notifyDataSetChanged();
        mChart.invalidate();

    }


    private void initialiseChartDatasets(int lineCount){

        int count = Constants.CHART_MAX_HORIZONTAL_POINTS;

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>(lineCount);
        for (int i = 0; i < lineCount; i++) {
            LineDataSet lineDataSet = createLineDataset((i + ""));
            dataSets.add(lineDataSet);
        }

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        if(mChart.getLineData() == null){
            mChart.setData(data);
            mChart.invalidate();
        }
    }

    public LineDataSet createLineDataset(String label){
        ArrayList<Entry> yVals = new ArrayList<Entry>(Constants.CHART_MAX_HORIZONTAL_POINTS);
        LineDataSet set1 = new LineDataSet(yVals, label);

        Random rnd = new Random();
        //int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        int color = Color.argb(255, rnd.nextInt(161), rnd.nextInt(161), rnd.nextInt(161));

        set1.setColor(color);
        set1.setLineWidth(1f);

        set1.setFillAlpha(65);
        set1.setFillColor(color);

        set1.setCircleColor(color);
        set1.setCircleSize(3f);
        set1.setDrawCircleHole(false);
        set1.setDrawCircles(false);

        set1.setDrawValues(false);
        set1.setValueTextSize(9f);

        return set1;
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

        // check if data list is initialized
        if(mChart.getLineData() == null)
            initialiseChartDatasets(event.values.length);

        setChartData(event.values);

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
