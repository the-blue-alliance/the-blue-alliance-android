package com.thebluealliance.androidclient.activities;

import android.os.Bundle;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.ContributorsFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ContributorsActivity extends DatafeedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contributors);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewCompat.setElevation(toolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        setSupportActionBar(toolbar);

        setupActionBar();

        ContributorsFragment contributorsFragment = ContributorsFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.contributors_fragment_container, contributorsFragment).commit();

        setSearchEnabled(false);
        setRefreshEnabled(false);
    }

    @Override
    public void onCreateNavigationDrawer() {
        setNavigationDrawerEnabled(false);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            setActionBarTitle(getString(R.string.contributors));
        }
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
}
