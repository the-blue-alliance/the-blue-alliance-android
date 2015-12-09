package com.thebluealliance.androidclient.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.PopulateContributors;
import com.thebluealliance.androidclient.di.components.DaggerFragmentComponent;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.listeners.ClickListenerModule;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.subscribers.SubscriberModule;

/**
 * Created by Nathan on 6/20/2014.
 */
public class ContributorsActivity extends DatafeedActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contributors);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewCompat.setElevation(toolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        setSupportActionBar(toolbar);

        setupActionBar();

        ((ListView) findViewById(android.R.id.list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String login = ((ListElement) ((ListViewAdapter) adapterView.getAdapter()).getItem(position)).getKey();
                String url = "https://github.com/" + login;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        setSearchEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new PopulateContributors(this).execute();
    }

    @Override
    public void onCreateNavigationDrawer() {
        setNavigationDrawerEnabled(false);
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
    public void showWarningMessage(CharSequence warningMessage) {

    }

    @Override
    public void hideWarningMessage() {

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
