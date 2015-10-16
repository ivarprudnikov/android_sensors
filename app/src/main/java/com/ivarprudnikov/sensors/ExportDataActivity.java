package com.ivarprudnikov.sensors;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;


public class ExportDataActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_data);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExportDataActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mUrl = (EditText)findViewById(R.id.editUrl);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_export, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:

                if(Patterns.WEB_URL.matcher(mUrl.getText().toString()).matches()){
                    send();
                } else {
                    mUrl.setError("URL is invalid");
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void send(){
        Toast.makeText(ExportDataActivity.this, "Sending ...", Toast.LENGTH_SHORT).show();
        Map data = App.getDbService().getData();

        AsyncNetworkTask.OnResponseListener l = new AsyncNetworkTask.OnResponseListener() {
            @Override
            public void onResponseFinished(int statusCode) {
                Toast.makeText(ExportDataActivity.this,
                        "Got response status: " + String.valueOf(statusCode),
                        Toast.LENGTH_SHORT).show();
            }
        };
        final AsyncNetworkTask mTask = new AsyncNetworkTask(ExportDataActivity.this, l, mUrl.getText().toString(), data);
        mTask.execute();

    }

}
