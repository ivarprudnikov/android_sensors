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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.ivarprudnikov.sensors.AsyncActionUrlStorage;
import com.ivarprudnikov.sensors.R;
import com.ivarprudnikov.sensors.storage.ActionUrl;


public class ExportDataCreateActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mUrl;

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
//        Map data = App.getDbService().getData();
//        AsyncNetworkTask.OnResponseListener l = new AsyncNetworkTask.OnResponseListener() {
//            @Override
//            public void onResponseFinished(int statusCode) {
//                Toast.makeText(ExportDataCreateActivity.this,
//                        "Got response status: " + String.valueOf(statusCode),
//                        Toast.LENGTH_SHORT).show();
//            }
//        };
//        final AsyncNetworkTask mTask = new AsyncNetworkTask(ExportDataCreateActivity.this, l, mUrl.getText().toString(), data);
//        mTask.execute();

//        App.getDbService().saveExportAction(mUrl.getText().toString(), 0, false);

        final AsyncActionUrlStorage mTask = new AsyncActionUrlStorage(new AsyncActionUrlStorage.OnQueryResponseListener() {
            @Override
            public void onQueryResponseFinished(String result) {
                finish();
            }
        });
        mTask.execute(new ActionUrl(mUrl.getText().toString(), 0));

    }

}
