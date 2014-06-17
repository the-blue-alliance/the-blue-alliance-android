package com.thebluealliance.androidclient.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.FirstLaunchFragmentAdapter;
import com.thebluealliance.androidclient.background.firstlaunch.LoadAllData;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.views.DisableSwipeViewPager;

/**
 * Created by Nathan on 5/25/2014.
 */
public class LaunchActivity extends Activity implements View.OnClickListener {

    private static final String ALL_DATA_LOADED = "all_data_loaded";

    private DisableSwipeViewPager viewPager;

    private TextView loadingMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent() != null){
            Uri data = getIntent().getData();
            if(data != null && data.getHost()!= null && data.getHost().equals(getString(R.string.web_url_host))){
                //we caught an Action.VIEW intent, so
                //now we generate the proper intent to view
                //the requested content
                Intent intent = Utilities.getIntentForTBAUrl(this, data);
                if(intent != null){
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        }
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(ALL_DATA_LOADED, false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_launch);
        viewPager = (DisableSwipeViewPager) findViewById(R.id.view_pager);
        viewPager.setSwipeEnabled(false);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new FirstLaunchFragmentAdapter(this));
        loadingMessage = (TextView) findViewById(R.id.message);
        findViewById(R.id.welcome_next_page).setOnClickListener(this);
        findViewById(R.id.finish).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.welcome_next_page:
                beginLoadingIfConnected();
                break;
            case R.id.finish:
                startActivity(new Intent(this, HomeActivity.class));
        }
    }

    private void beginLoadingIfConnected() {
        if(ConnectionDetector.isConnectedToInternet(this)) {
            advanceToNextPage();
            beginLoading();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // Set title
            alertDialogBuilder.setTitle("Check connection");

            // Set dialog message
            alertDialogBuilder.setMessage(getString(R.string.warning_no_internet_connection)).setCancelable(false).setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    beginLoadingIfConnected();
                    dialog.dismiss();
                }
            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });

            // Create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // Show it
            alertDialog.show();
        }
    }

    public void advanceToNextPage() {
        if (viewPager.getCurrentItem() < viewPager.getAdapter().getCount() - 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    public void returnToPreviousPage() {
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private void beginLoading() {

        new LoadAllData(this).execute();
    }

    public void errorLoadingData(final String stacktrace) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set title
        alertDialogBuilder.setTitle(getString(R.string.fatal_error));

        // Set dialog message
        alertDialogBuilder.setMessage(getString(R.string.fatal_error_message)).setCancelable(false).setPositiveButton(R.string.contact_developer, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "contact@thebluealliance.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FATAL ERROR");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Version: " + BuildConfig.VERSION_NAME + "\nStacktrace:\n" + stacktrace);
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
                finish();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        // Create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // Show it
        alertDialog.show();
    }

    public void connectionLost() {

    }

    public void loadingFinished() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(ALL_DATA_LOADED, true).commit();
    }

    public void onLoadingProgressUpdate(LoadAllData.LoadProgressInfo info) {
        if (info.state == LoadAllData.LoadProgressInfo.STATE_NO_CONNECTION) {
            connectionLost();
        } else if (info.state == LoadAllData.LoadProgressInfo.STATE_LOADING) {
            loadingMessage.setText(info.message);
        } else if (info.state == LoadAllData.LoadProgressInfo.STATE_FINISHED) {
            loadingFinished();
            advanceToNextPage();
        } else if (info.state == LoadAllData.LoadProgressInfo.STATE_ERROR) {
            errorLoadingData(info.message);
        }
    }
}
