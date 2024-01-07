package com.thebluealliance.androidclient.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AlertDialog;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.types.ModelType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides the features that should be in every activity in the app: a navigation drawer, a search
 * button, and the ability to show and hide warning messages. Also provides Android Beam
 * functionality.
 */
public abstract class BaseActivity extends NavigationDrawerActivity
        implements NfcAdapter.CreateNdefMessageCallback {

    @IntDef({WARNING_OFFLINE, WARNING_FIRST_API_DOWN, WARNING_EVENT_DOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WarningMessageType {}

    public static final int WARNING_OFFLINE = 3;
    public static final int WARNING_FIRST_API_DOWN = 2;
    public static final int WARNING_EVENT_DOWN = 1;

    public Set<Integer> activeMessages = new HashSet<>();

    String beamUri;
    String shareUri;
    boolean searchEnabled = true;
    boolean shareEnabled = false;
    String modelKey = "";
    ModelType modelType;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Hide the shadow below the Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_menu, menu);
        if (!searchEnabled) {
            menu.findItem(R.id.search).setVisible(false);
        }
        if (!shareEnabled) {
            menu.findItem(R.id.share).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(this, SearchResultsActivity.class));
                return true;
            case R.id.share:
                if (shareUri == null) return true;
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareUri);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_prompt)));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * By default, all activities that extend BaseActivity will show global warning messages. If
     * you don't want a subclass to show warning messages, override this and return false;
     *
     * @return true if this activity should show warning messages, false if otherwise
     */
    public boolean shouldShowWarningMessages() {
        return true;
    }

    public void showWarningMessage(@WarningMessageType int messageType) {
        if (!shouldShowWarningMessages()) {
            return;
        }
        activeMessages.add(messageType);

        // The latest message to be shown might not be the highest priority
        // Find the highest priority active message and show that one
        displayMessageForMessageType(getHighestPriorityMessage());

    }

    private void displayMessageForMessageType(@WarningMessageType int type) {
        View container = findViewById(R.id.root_snackbar_container);
        TextView message = (TextView) findViewById(R.id.root_snackbar_message);
        TextView action = (TextView) findViewById(R.id.root_snackbar_action);

        // Set default visibilities
        container.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        action.setVisibility(View.GONE);

        switch (type) {
            case WARNING_OFFLINE:
                message.setText(R.string.warning_offline_message);
                action.setVisibility(View.VISIBLE);
                action.setText(R.string.warning_more);
                action.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.warning_more_info);
                    builder.setMessage(R.string.warning_offline_explanation);
                    builder.setCancelable(true);
                    builder.setNeutralButton(getString(R.string.warning_close),
                            (dialog, which) -> dialog.cancel()
                    );
                    builder.create().show();
                });
                break;
            case WARNING_EVENT_DOWN:
                message.setText(R.string.warning_event_down_message);
                action.setVisibility(View.VISIBLE);
                action.setText(R.string.warning_more);
                action.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.warning_more_info);
                    builder.setMessage(R.string.warning_event_down_explanation);
                    builder.setCancelable(true);
                    builder.setNeutralButton(getString(R.string.warning_close),
                            (dialog, which) -> dialog.cancel()
                    );
                    builder.create().show();
                });
                break;
            case WARNING_FIRST_API_DOWN:
                message.setText(R.string.warning_first_down_message);
                action.setVisibility(View.VISIBLE);
                action.setText(R.string.warning_more);
                action.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.warning_more_info);
                    builder.setMessage(R.string.warning_first_down_explanation);
                    builder.setCancelable(true);
                    builder.setNeutralButton(getString(R.string.warning_close),
                            (dialog, which) -> dialog.cancel()
                    );
                    builder.create().show();
                });
                break;
        }
    }

    public void dismissWarningMessage(@WarningMessageType int messageType) {
        activeMessages.remove(messageType);
        if (activeMessages.isEmpty()) {
            // There are no more active messages; hide the container
            findViewById(R.id.root_snackbar_container).setVisibility(View.GONE);
        } else {
            // There are more active messages; find the highest priority one and display it
            displayMessageForMessageType(getHighestPriorityMessage());
        }
    }

    public void dismissAllWarningMessages() {
        activeMessages.clear();
        findViewById(R.id.root_snackbar_container).setVisibility(View.GONE);
    }

    private @WarningMessageType int getHighestPriorityMessage() {
        @WarningMessageType int highestPriority = Integer.MIN_VALUE;
        for (int type : activeMessages) {
            if (type > highestPriority) {
                highestPriority = type;
            }
        }

        return highestPriority;
    }

    public void setShareUri(String uri) {
        shareUri = uri;
    }

    public void setShareEnabled(boolean enabled) {
        shareEnabled = enabled;
    }

    public void setBeamUri(String uri) {
        beamUri = uri;
    }

    protected void setSearchEnabled(boolean enabled) {
        searchEnabled = enabled;
        invalidateOptionsMenu();
    }

    protected void setModelKey(String key, ModelType type) {
        modelKey = key;
        modelType = type;
    }
}
