package com.ivarprudnikov.sensors;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ivarprudnikov.sensors.config.Constants;
import com.ivarprudnikov.sensors.config.Preferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class DisplayLogActivity extends AppCompatActivity {

    private Switch isSensorLogEnabledSwitch;
    private TextView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_log);

        logView = (TextView)findViewById(R.id.logView);

        boolean switchValue = Preferences.getPrefs(DisplayLogActivity.this).getBoolean(Constants.PREFS_IS_SENSOR_LOG_ENABLED, false);
        isSensorLogEnabledSwitch = (Switch)findViewById(R.id.isSensorLogEnabled);
        isSensorLogEnabledSwitch.setChecked(switchValue);
        isSensorLogEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = Preferences.getPrefs(DisplayLogActivity.this).edit();
                editor.putBoolean(Constants.PREFS_IS_SENSOR_LOG_ENABLED, isChecked);
                editor.commit();
            }
        });

        refreshLog();

    }

    public void refreshLog(){

        ArrayList<String> lines = new ArrayList<String>();
        String line;
        BufferedReader in = null;
        File file = new File(Environment.getExternalStorageDirectory(), Constants.PREFS_SENSOR_LOG_FILE_NAME);

        if(file.exists() == false){
            return;
        }

        try {
            in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            Log.e("DisplayLogActivity", "Exception reading from log file", e);
        } catch (IOException e) {
            Log.e("DisplayLogActivity", "Exception reading from log file", e);
        }

        Collections.reverse(lines);

        String logContent = TextUtils.join("\n", lines);

        logView.setText(logContent);
        logView.requestLayout();
    }

    public void deleteLog(){

        File file = new File(Environment.getExternalStorageDirectory(), Constants.PREFS_SENSOR_LOG_FILE_NAME);
        if(file.exists()){
            boolean result = file.delete();
            if(result == true){
                logView.setText("");
                logView.requestLayout();
            } else {
                Toast.makeText(DisplayLogActivity.this, "Could not delete log", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_log) {
            deleteLog();
        } else if (id == R.id.refresh_log) {
            refreshLog();
        }

        return super.onOptionsItemSelected(item);
    }
}
