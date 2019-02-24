package com.thebluealliance.androidclient.activities;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.di.components.DaggerFragmentComponent;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.fragments.ContributorsFragment;
import com.thebluealliance.androidclient.listeners.ClickListenerModule;
import com.thebluealliance.androidclient.subscribers.SubscriberModule;

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

    @Override
    public FragmentComponent getComponent() {
        if (mComponent == null) {
            TBAAndroid application = ((TBAAndroid) getApplication());
            mComponent = DaggerFragmentComponent.builder()
              .applicationComponent(application.getComponent())
              .datafeedModule(application.getDatafeedModule())
              .binderModule(application.getBinderModule())
              .databaseWriterModule(application.getDatabaseWriterModule())
              .authModule(application.getAuthModule())
              .subscriberModule(new SubscriberModule(this))
              .clickListenerModule(new ClickListenerModule(this))
              .build();
        }
        return mComponent;
    }

    public void inject() {
        getComponent().inject(this);
    }
}
