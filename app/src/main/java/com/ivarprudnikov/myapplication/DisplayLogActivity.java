package com.ivarprudnikov.myapplication;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class DisplayLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_log);
        refreshLog();
    }

    public void refreshLog(){
        ArrayList<String> lines = new ArrayList<String>();
        String line;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(new File(Environment.getExternalStorageDirectory(), "SensorLog.txt")));
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

        ((TextView)findViewById(R.id.logView)).setText(logContent);
    }

    public void deleteLog(){

        File file = new File(Environment.getExternalStorageDirectory(), "SensorLog.txt");
        boolean result = file.delete();
        if(result == true){
            ((TextView)findViewById(R.id.logView)).setText("");
        } else {
            Toast.makeText(DisplayLogActivity.this, "Could not delete log", Toast.LENGTH_SHORT).show();
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
