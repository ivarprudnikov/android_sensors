/**
 * This file is part of com.ivarprudnikov.sensors package.
 *
 * com.ivarprudnikov.sensors is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * com.ivarprudnikov.sensors is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with com.ivarprudnikov.sensors.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ivarprudnikov.sensors.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.ivarprudnikov.sensors.AsyncActionUrlStorage;
import com.ivarprudnikov.sensors.R;
import com.ivarprudnikov.sensors.storage.ActionUrl;


public class ExportDataFormActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mUrl;
    private EditText mFrequency;
    private ActionUrl action;

    private static final int REQUEST_CHOOSER = 8213;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_data_create);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_clear_mtrl_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mUrl = (EditText)findViewById(R.id.editUrl);
        mFrequency = (EditText)findViewById(R.id.editFrequency);

        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null && bundle.size() > 0){
            action = bundle.getParcelable("item");
        }
        if(action != null){
            mUrl.setText(action.getUrl());
            mFrequency.setText(String.valueOf(action.getFrequency()));
        }

        // Create the ACTION_GET_CONTENT Intent
        Intent getContentIntent = FileUtils.createGetContentIntent();
        Intent intent = Intent.createChooser(getContentIntent, "Select a file");
        startActivityForResult(intent, REQUEST_CHOOSER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_export_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if(Patterns.WEB_URL.matcher(mUrl.getText().toString()).matches()){
                    save();
                } else {
                    mUrl.setError("URL is invalid");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void save(){
        final AsyncActionUrlStorage mTask = new AsyncActionUrlStorage(new AsyncActionUrlStorage.OnQueryResponseListener() {
            @Override
            public void onQueryResponseFinished(String result) {
                finish();
            }
        });

        long frequency = 0;
        try {
            frequency = Long.valueOf(mFrequency.getText().toString());
        } catch (Exception e){
            Log.d("frequency exception", e.getMessage());
        }

        if(action != null){
            action.setUrl(mUrl.getText().toString());
            action.setFrequency(frequency);
            action.setLast_updated(System.currentTimeMillis());
        } else {
            action = new ActionUrl(mUrl.getText().toString(), frequency);
        }
        mTask.execute(action);
    }

}
