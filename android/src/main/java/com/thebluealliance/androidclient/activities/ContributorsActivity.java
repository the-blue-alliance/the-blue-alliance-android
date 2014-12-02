package com.thebluealliance.androidclient.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.PopulateContributors;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListElement;

/**
 * Created by Nathan on 6/20/2014.
 */
public class ContributorsActivity extends RefreshableHostActivity implements RefreshListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contributors);

        setupActionBar();

        ((ListView) findViewById(android.R.id.list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String login = ((ListElement) ((ListViewAdapter) adapterView.getAdapter()).getItem(position)).getKey();
                String url = "https://github.com/" + login;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        registerRefreshListener(this);

        setSearchEnabled(false);
    }

    @Override
    public void onCreateNavigationDrawer() {
        setNavigationDrawerEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRefresh(this);
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle(getString(R.string.contributors));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (isDrawerOpen()) {
                closeDrawer();
                return true;
            }
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showWarningMessage(String message) {

    }

    @Override
    public void hideWarningMessage() {

    }

    @Override
    public void onRefreshStart(boolean actionItemPressed) {
        if (ConnectionDetector.isConnectedToInternet(this)) {
            new PopulateContributors(this).execute();
        } else {
            findViewById(android.R.id.list).setVisibility(View.GONE);
            findViewById(R.id.no_data).setVisibility(View.VISIBLE);
            findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefreshStop() {

    }
}
