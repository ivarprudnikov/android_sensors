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
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.ivarprudnikov.sensors.async.AsyncActionUrlStorage;
import com.ivarprudnikov.sensors.R;
import com.ivarprudnikov.sensors.storage.ActionUrl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class ExportDataFormActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mUrl;
    private EditText mFrequency;
    private EditText mCertificate;
    private ActionUrl action;
    private byte[] mCertificateData;
    private Button mAddCertificate;
    private Button mRemoveCertificate;

    private static final int REQUEST_CHOOSER = 8213;
    private static final int MAX_FILE_SIZE = 10240;

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
        mCertificate = (EditText)findViewById(R.id.editCertificate);
        mAddCertificate = (Button)findViewById(R.id.certificateAdd);
        mRemoveCertificate = (Button)findViewById(R.id.certificateRemove);

        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null && bundle.size() > 0){
            action = bundle.getParcelable("item");
        }
        if(action != null){
            mUrl.setText(action.getUrl());
            mFrequency.setText(String.valueOf(action.getFrequency()));
            mCertificateData = action.getClient_certificate();
        }

        mAddCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCertificateSelector();
            }
        });

        mRemoveCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCertificate();
            }
        });

        syncCertificateViewParts();
    }

    /**
     * Show/hide certificate add/remove buttons
     */
    public void syncCertificateViewParts(){
        if(mCertificateData != null && mCertificateData.length > 0){
            mCertificate.setVisibility(View.VISIBLE);
            mAddCertificate.setVisibility(View.GONE);
            mRemoveCertificate.setVisibility(View.VISIBLE);
        } else {
            mCertificate.setVisibility(View.GONE);
            mAddCertificate.setVisibility(View.VISIBLE);
            mRemoveCertificate.setVisibility(View.GONE);
        }
    }

    /**
     * Open file selector for the user to pick certificate file
     */
    public void showCertificateSelector(){
        // Create the ACTION_GET_CONTENT Intent
        Intent getContentIntent = FileUtils.createGetContentIntent();
        Intent intent = Intent.createChooser(getContentIntent, "Select a file");
        startActivityForResult(intent, REQUEST_CHOOSER);
    }

    public void removeCertificate(){
        mCertificateData = null;
        syncCertificateViewParts();
    }

    public byte[] fileToByteArray(File file) {
        if (file.length() > MAX_FILE_SIZE || file == null) {
            return null;
        }

        byte[] buffer = new byte[(int) file.length()];
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            if (in.read(buffer) == -1) {
                throw new IOException("EOF reached while trying to read the whole file");
            }
        } catch(IOException e){
            buffer = null;
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
            }
        }
        return buffer;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSER:
                if (resultCode == RESULT_OK) {
                    final Uri uri = data.getData();
                    // Get the File path from the Uri
                    String path = FileUtils.getPath(this, uri);
                    // Alternatively, use FileUtils.getFile(Context, Uri)
                    if (path != null && FileUtils.isLocal(path)) {
                        File file = new File(path);
                        mCertificateData = fileToByteArray(file);
                    }
                    syncCertificateViewParts();
                }
                break;
        }
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
            action.setClient_certificate(mCertificateData);
        } else {
            action = new ActionUrl(mUrl.getText().toString(), frequency, mCertificateData);
        }
        mTask.execute(action);
    }

}
