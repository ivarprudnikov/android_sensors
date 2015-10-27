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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.ivarprudnikov.sensors.ActionUrlAdapter;
import com.ivarprudnikov.sensors.App;
import com.ivarprudnikov.sensors.R;
import com.ivarprudnikov.sensors.storage.ActionUrl;

import java.util.ArrayList;
import java.util.List;


public class ExportDataActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    ImageButton mAddButton;
    ActionUrlAdapter mListAdapter;
    ListView mListView;

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
                finish();
            }
        });

        // Handle button tap
        mAddButton = (ImageButton) findViewById(R.id.addButton);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExportDataActivity.this, ExportDataCreateActivity.class);
                startActivity(intent);
            }
        });

        mListView = (ListView) findViewById(R.id.listView);

        // do not allow focus on children as they will swallow events
        mListView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        mListAdapter = new ActionUrlAdapter(this, new ArrayList<ActionUrl>());
        mListView.setAdapter(mListAdapter);
        syncActionUrls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncActionUrls();
    }

    public void syncActionUrls(){
        List<ActionUrl> mActionsUrlList = App.getDbService().getActionUrlList();
        mListAdapter.clear();
        for(ActionUrl au : mActionsUrlList){
            mListAdapter.add(au);
        }
        mListAdapter.notifyDataSetChanged();
    }

}
