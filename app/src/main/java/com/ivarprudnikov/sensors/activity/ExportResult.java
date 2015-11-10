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
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ivarprudnikov.sensors.ActionResultAdapter;
import com.ivarprudnikov.sensors.App;
import com.ivarprudnikov.sensors.R;
import com.ivarprudnikov.sensors.storage.ActionUrl;
import com.ivarprudnikov.sensors.storage.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class ExportResult extends AppCompatActivity {

    private Toolbar mToolbar;
    private ActionUrl action;
    TextView mEmptyListHelperView;
    ListView mListView;
    ActionResultAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_result);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null && bundle.size() > 0){
            action = bundle.getParcelable("item");
        } else {
            action = null;
        }

        mEmptyListHelperView = (TextView) findViewById(R.id.emptyListHelperView);

        mListView = (ListView) findViewById(R.id.listView);

        // do not allow focus on children as they will swallow events
        mListView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        mListAdapter = new ActionResultAdapter(this, new ArrayList<ActionResult>());
        mListView.setAdapter(mListAdapter);
        syncResults();
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncResults();
    }

    public void syncResults(){
        List<ActionResult> mList = App.getDbService().getActionResultList(action);
        if(mList.size() > 0){
            mEmptyListHelperView.setVisibility(View.GONE);
        } else {
            mEmptyListHelperView.setVisibility(View.VISIBLE);
        }
        mListAdapter.clear();
        for(ActionResult ar : mList){
            mListAdapter.add(ar);
        }
        mListAdapter.notifyDataSetChanged();
    }
}
