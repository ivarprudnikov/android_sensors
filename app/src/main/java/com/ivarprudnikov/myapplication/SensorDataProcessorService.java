package com.ivarprudnikov.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class SensorDataProcessorService extends Service {

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;

    public SensorDataProcessorService() {}

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
    }

    // Fires when a service is started up
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Message m = Message.obtain();
//        Bundle b = new Bundle();
//        b.putSerializable("x","y");
//        m.setData(b);

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

    public void writeToLog(String dataString){
        File log = new File(Environment.getExternalStorageDirectory(), "SensorLog.txt");
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
