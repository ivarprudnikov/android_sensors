package com.ivarprudnikov.sensors;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SensorDataProcessorService extends Service implements SensorEventListener {

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;
    private SharedPreferences mSharedPreferences;
    private SensorManager mSensorManager;
    private List<Sensor> mSensorList;
    private List<Sensor> mSensorRegisteredList;

    public SensorDataProcessorService(){}

    public SharedPreferences getPrefs(){
        if(mSharedPreferences != null){
            return mSharedPreferences;
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(SensorDataProcessorService.this);
        return mSharedPreferences;
    }

    // Define how the handler will process messages
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        // Define how to handle any incoming messages here
        @Override
        public void handleMessage(Message message) {
            writeToLog(message.getData().toString());
            // ...
            // When needed, stop the service with
            // stopSelf();
        }
    }

    // Fires when a service is first initialized
    public void onCreate() {
        super.onCreate();
        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("SensorDataProcessorService.HandlerThread");
        mHandlerThread.start();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());
        // Set the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // List of Sensors Available
        mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
    }

    // Fires when a service is started up
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Message m = Message.obtain();
//        Bundle b = new Bundle();
//        b.putSerializable("x","y");
//        m.setData(b);

        // reregister sensors
        mSensorManager.unregisterListener(this);
        // TODO: pick up required sensors from prefs

        //mSensorRegisteredList = new ArrayList<Sensor>();
        Map<String,?> allPrefs = getPrefs().getAll();
        for(String key : allPrefs.keySet()){
            if(key.matches(Constants.PREFS_SENSOR_ENABLED_PREFIX + ".*")){
                Log.i("SensorDataProcessorSrvc", key);
            }
        }

        mSensorManager.registerListener(this, mSensorList.get(0), SensorManager.SENSOR_DELAY_NORMAL);

        // Send empty message to background thread
        mServiceHandler.sendEmptyMessage(0);
        // Keep service around "sticky"
        return START_STICKY;
    }

    // Defines the shutdown sequence
    @Override
    public void onDestroy() {
        // Cleanup service before destruction
        mHandlerThread.quit();
    }

    // Binding is another way to communicate between service and activity
    // Not needed here, local broadcasts will be used instead
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch(accuracy){
            case SensorManager.SENSOR_STATUS_NO_CONTACT:
                break;
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                break;
            default:
                break;
        }
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float[] vals = event.values;
        StringBuilder sb = new StringBuilder();
        for(float v : vals){
            sb.append(v);
            sb.append(" \n");
        }
        writeToLog(sb.toString());
    }


    public void writeToLog(String dataString){

        boolean sensorLogEnabled = getPrefs().getBoolean(Constants.PREFS_IS_SENSOR_LOG_ENABLED, false);
        if(sensorLogEnabled == false){
            return;
        }

        File log = new File(Environment.getExternalStorageDirectory(), Constants.PREFS_SENSOR_LOG_FILE_NAME);
        boolean shouldAppendToFile = log.exists();
        String message = dataString + " - " + new Date().toString();
        try {
            FileWriter fw = new FileWriter(log.getAbsolutePath(), shouldAppendToFile);
            BufferedWriter out = new BufferedWriter(fw);
            out.write(message);
            out.write("\n");
            out.close();
            Log.i("SensorDataProcessorSrvc", message);
        }
        catch (IOException e) {
            Log.e("SensorDataProcessorSrvc", "Exception appending to log file", e);
        }
    }
}
