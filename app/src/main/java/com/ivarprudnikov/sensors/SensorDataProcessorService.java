package com.ivarprudnikov.sensors;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ivarprudnikov.sensors.config.Preferences;
import com.ivarprudnikov.sensors.storage.SensorDataDbService;

import java.util.ArrayList;
import java.util.List;

public class SensorDataProcessorService extends Service implements SensorEventListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;
    private SensorManager mSensorManager;
    private List<Sensor> mSensorList;
    private SensorDataDbService mSensorDataDbService;
    private SharedPreferences mSharedPreferences;

    public SensorDataProcessorService(){}

    // Define how the handler will process messages
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        // Define how to handle any incoming messages here
        @Override
        public void handleMessage(Message message) {

            Bundle b = message.getData();
            String EVENT_TYPE = b.getString("EVENT");
            if(EVENT_TYPE != null){
                switch(EVENT_TYPE){
                    case "SENSOR":
                        mSensorDataDbService.save(b.getString("NAME"), b.getFloatArray("VALUES"), b.getLong("TIMESTAMP"));
                        break;
                    default:
                        Log.i("handleMessage", b.toString());
                }
            } else {
            }
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

        mSensorDataDbService = new SensorDataDbService(this);

        mSharedPreferences = Preferences.getPrefs(this);
    }

    // Fires when a service is started up
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerSensorListeners();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        // Send empty message to background thread
        mServiceHandler.sendEmptyMessage(0);
        // Keep service around "sticky"
        return START_STICKY;
    }

    public void registerSensorListeners(){
        mSensorManager.unregisterListener(this);
        if(Preferences.isDataStorageEnabled(SensorDataProcessorService.this)){
            ArrayList<String> allEnabled = Preferences.getEnabledSensorNames(SensorDataProcessorService.this);
            for(Sensor s : mSensorList){
                if(allEnabled.contains(s.getName())){
                    mSensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        }
    }

    // Defines the shutdown sequence
    @Override
    public void onDestroy() {
        // Cleanup service before destruction
        mSensorManager.unregisterListener(this);
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        mHandlerThread.quit();
    }

    // Binding is another way to communicate between service and activity
    // Not needed here, local broadcasts will be used instead
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        registerSensorListeners();
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
        Message m = Message.obtain();
        Bundle b = new Bundle();
        b.putString("EVENT","SENSOR");
        b.putString("NAME", event.sensor.getName());
        b.putFloatArray("VALUES", event.values);
        b.putLong("TIMESTAMP", event.timestamp);
        m.setData(b);
        mServiceHandler.sendMessage(m);
    }
}
