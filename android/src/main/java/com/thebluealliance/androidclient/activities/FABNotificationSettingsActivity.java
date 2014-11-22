package com.thebluealliance.androidclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.fragments.mytba.NotificationSettingsFragment;
import com.thebluealliance.androidclient.fragments.tasks.UpdateUserModelSettingsTaskFragment;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.interfaces.ModelSettingsCallbacks;

/**
 * Created by Nathan on 11/6/2014.
 */
public abstract class FABNotificationSettingsActivity extends RefreshableHostActivity implements View.OnClickListener, ModelSettingsCallbacks {

    private RelativeLayout notificationSettings;
    private FloatingActionButton openNotificationSettingsButton;
    private View openNotificationSettingsButtonContainer;
    private FloatingActionButton closeNotificationSettingsButton;
    private View closeNotificationSettingsButtonContainer;

    private Toolbar notificationSettingsToolbar;

    private NotificationSettingsFragment settings;

    private UpdateUserModelSettingsTaskFragment saveSettingsTaskFragment;

    private boolean isSettingsPanelOpen = false;

    private boolean saveInProgress = false;

    private static final String SETTINGS_PANEL_OPEN = "settings_panel_open";

    private static final String SAVE_SETTINGS_TASK_FRAGMENT_TAG = "task_fragment_tag";

    private Bundle savedPreferenceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.activity_fab_notification_settings);

        notificationSettings = (RelativeLayout) findViewById(R.id.notification_settings);
        openNotificationSettingsButton = (FloatingActionButton) findViewById(R.id.open_notification_settings_button);
        openNotificationSettingsButton.setOnClickListener(this);
        openNotificationSettingsButtonContainer = findViewById(R.id.open_notification_settings_button_container);
        closeNotificationSettingsButton = (FloatingActionButton) findViewById(R.id.close_notification_settings_button);
        closeNotificationSettingsButton.setOnClickListener(this);
        closeNotificationSettingsButtonContainer = findViewById(R.id.close_notification_settings_button_container);

        // Hide the notification settings button if myTBA isn't enabled
        if (!AccountHelper.isMyTBAEnabled(this)) {
            notificationSettings.setVisibility(View.INVISIBLE);
        }

        notificationSettingsToolbar = (Toolbar) findViewById(R.id.notification_settings_toolbar);
        notificationSettingsToolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        notificationSettingsToolbar.setTitle("Team Settings");
        notificationSettingsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNotificationSettingsCloseButtonClick();
            }
        });
        notificationSettingsToolbar.setNavigationContentDescription(R.string.close);

        // Setup the settings menu

        Log.d(Constants.LOG_TAG, "Model: " + modelKey);
        if (savedInstanceState != null) {
            isSettingsPanelOpen = savedInstanceState.getBoolean(SETTINGS_PANEL_OPEN);
            if (isSettingsPanelOpen) {
                openNotificationSettingsButtonContainer.setVisibility(View.INVISIBLE);
                closeNotificationSettingsButtonContainer.setVisibility(View.VISIBLE);
                notificationSettings.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.accent_dark));
                }
            } else {
                openNotificationSettingsButtonContainer.setVisibility(View.VISIBLE);
                closeNotificationSettingsButtonContainer.setVisibility(View.INVISIBLE);
                notificationSettings.setVisibility(View.INVISIBLE);
            }
            savedPreferenceState = savedInstanceState.getBundle(NotificationSettingsFragment.SAVED_STATE_BUNDLE);
        }

        saveSettingsTaskFragment = (UpdateUserModelSettingsTaskFragment) getSupportFragmentManager().findFragmentByTag(SAVE_SETTINGS_TASK_FRAGMENT_TAG);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SETTINGS_PANEL_OPEN, isSettingsPanelOpen);
        // Only save the preference state if they've already been successfully loaded
        // Also, only save them if the settings panel is open. Otherwise, clear them on rotate
        if (settings != null && settings.arePreferencesLoaded() && isSettingsPanelOpen) {
            Bundle b = new Bundle();
            settings.writeStateToBundle(b);
            outState.putBundle(NotificationSettingsFragment.SAVED_STATE_BUNDLE, b);
        }
    }

    @Override
    protected void setModelKey(String key, ModelHelper.MODELS modelType) {
        super.setModelKey(key, modelType);
        // Now that we have a model key, we can create a settings fragment for the appropriate model type
        settings = NotificationSettingsFragment.newInstance(modelKey, modelType, savedPreferenceState);
        getFragmentManager().beginTransaction().replace(R.id.settings_list, settings).commit();
    }

    @Override
    public void setContentView(int layoutResID) {
        FrameLayout root = (FrameLayout) findViewById(R.id.activity_content);
        LayoutInflater inflater = getLayoutInflater();
        inflater.inflate(layoutResID, root);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.open_notification_settings_button) {
            if(!saveInProgress) {
                openNotificationSettingsView();
            }
        } else if (v.getId() == R.id.close_notification_settings_button) {
            closeNotificationSettingsWindow();
            // The user wants to save the preferences
            if (saveSettingsTaskFragment == null) {
                saveSettingsTaskFragment = new UpdateUserModelSettingsTaskFragment(settings.getSettings());
                getSupportFragmentManager().beginTransaction().add(saveSettingsTaskFragment, SAVE_SETTINGS_TASK_FRAGMENT_TAG).commit();
                saveInProgress = true;
            }
        } else {
            Log.d(Constants.LOG_TAG, "Clicked id: " + v.getId() + " tag: " + v.getTag() + " view: " + v.toString());
        }
    }

    private void onNotificationSettingsCloseButtonClick() {
        closeNotificationSettingsWindow();
        // Cancel any changes made by the user
        settings.restoreInitialState();
    }

    private void openNotificationSettingsView() {
        // get the centers for the clipping circles
        // this is the center of the button itself, relative to its container. We need to use these coordinates to clip the button.
        int centerOfButtonInsideX = (openNotificationSettingsButton.getLeft() + openNotificationSettingsButton.getRight()) / 2;
        int centerOfButtonInsideY = (openNotificationSettingsButton.getTop() + openNotificationSettingsButton.getBottom()) / 2;

        // this is the center of the button in relation to the main view. This provides the center of the clipping circle for the notification settings view.
        int centerOfButtonOutsideX = (openNotificationSettingsButtonContainer.getLeft() + openNotificationSettingsButtonContainer.getRight()) / 2;
        int centerOfButtonOutsideY = (openNotificationSettingsButtonContainer.getTop() + openNotificationSettingsButtonContainer.getBottom()) / 2;

        float finalRadius = (float) Math.sqrt(Math.pow(centerOfButtonOutsideX - notificationSettings.getLeft(), 2) + Math.pow(centerOfButtonOutsideY - notificationSettings.getTop(), 2));

        if (notificationSettings.getVisibility() == View.INVISIBLE) {
            notificationSettings.setVisibility(View.VISIBLE);

            // Only create the circular reveal on L or greater. Otherwise, default to some other transition.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
                Animator anim = ViewAnimationUtils.createCircularReveal(notificationSettings, centerOfButtonOutsideX, centerOfButtonOutsideY, 0, finalRadius);
                anim.setDuration(500);

                // We create the circular reveals on the buttons container, because we can't create a clipping circle on the button itself
                openNotificationSettingsButtonContainer.setVisibility(View.INVISIBLE);

                closeNotificationSettingsButtonContainer.setVisibility(View.VISIBLE);
                final Animator closeButtonAnimator = ViewAnimationUtils.createCircularReveal(closeNotificationSettingsButtonContainer, centerOfButtonInsideX, centerOfButtonInsideY, 0, (closeNotificationSettingsButton.getWidth() / 2));
                closeButtonAnimator.setDuration(anim.getDuration());

                anim.start();
                closeButtonAnimator.start();

                // Animate the status bar color change

                Integer colorFrom = getResources().getColor(R.color.primary_dark);
                Integer colorTo = getResources().getColor(R.color.accent_dark);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
                    }

                });
                colorAnimation.setDuration(anim.getDuration());
                colorAnimation.start();
            } else {
                openNotificationSettingsButtonContainer.setVisibility(View.INVISIBLE);
                closeNotificationSettingsButtonContainer.setVisibility(View.VISIBLE);
                notificationSettings.setVisibility(View.VISIBLE);
            }
            isSettingsPanelOpen = true;
        }
    }

    private void closeNotificationSettingsWindow() {
        int centerOfButtonInsideX = (openNotificationSettingsButton.getLeft() + openNotificationSettingsButton.getRight()) / 2;
        int centerOfButtonInsideY = (openNotificationSettingsButton.getTop() + openNotificationSettingsButton.getBottom()) / 2;

        int centerOfButtonOutsideX = (openNotificationSettingsButtonContainer.getLeft() + openNotificationSettingsButtonContainer.getRight()) / 2;
        int centerOfButtonOutsideY = (openNotificationSettingsButtonContainer.getTop() + openNotificationSettingsButtonContainer.getBottom()) / 2;

        float finalRadius = (float) Math.sqrt(Math.pow(centerOfButtonOutsideX - notificationSettings.getLeft(), 2) + Math.pow(centerOfButtonOutsideY - notificationSettings.getTop(), 2));
        if (notificationSettings.getVisibility() == View.VISIBLE) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(notificationSettings, centerOfButtonOutsideX, centerOfButtonOutsideY, finalRadius, 0);
                anim.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        notificationSettings.setVisibility(View.INVISIBLE);
                    }
                });
                anim.setDuration(500);
                final Animator openButtonAnimator = ViewAnimationUtils.createCircularReveal(openNotificationSettingsButtonContainer, centerOfButtonInsideX, centerOfButtonInsideY, 0, (openNotificationSettingsButton.getWidth() / 2));
                openButtonAnimator.setDuration(anim.getDuration());
                Animator closeButtonAnimator = ViewAnimationUtils.createCircularReveal(closeNotificationSettingsButtonContainer, centerOfButtonInsideX, centerOfButtonInsideY, (closeNotificationSettingsButton.getWidth() / 2), 0);
                closeButtonAnimator.setDuration(anim.getDuration() / 2);
                closeButtonAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        closeNotificationSettingsButtonContainer.setVisibility(View.INVISIBLE);
                        openNotificationSettingsButtonContainer.setVisibility(View.VISIBLE);
                        openButtonAnimator.start();
                    }
                });
                closeButtonAnimator.start();
                anim.start();

                // Animate the status bar color change

                Integer colorFrom = getResources().getColor(R.color.accent_dark);
                Integer colorTo = getResources().getColor(R.color.primary_dark);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
                    }

                });
                colorAnimation.setDuration(anim.getDuration());
                colorAnimation.start();
            } else {
                openNotificationSettingsButtonContainer.setVisibility(View.VISIBLE);
                closeNotificationSettingsButtonContainer.setVisibility(View.INVISIBLE);
                notificationSettings.setVisibility(View.INVISIBLE);
            }
            isSettingsPanelOpen = false;
        }
    }

    public void showFab(boolean animate) {
        openNotificationSettingsButton.show(animate);
    }

    public void hideFab(boolean animate) {
        openNotificationSettingsButton.hide(animate);
    }

    public void setSettingsToolbarTitle(String title) {
        notificationSettingsToolbar.setTitle(title);
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().remove(fm.findFragmentByTag(SAVE_SETTINGS_TASK_FRAGMENT_TAG)).commit();
        saveSettingsTaskFragment = null;

        // Tell the settings fragment to reload the now-updated
        settings.refreshSettingsFromDatabase();

        // Save finished
        saveInProgress = false;
    }

    @Override
    public void onNoOp() {
        Toast.makeText(this, "No op", Toast.LENGTH_SHORT).show();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().remove(fm.findFragmentByTag(SAVE_SETTINGS_TASK_FRAGMENT_TAG)).commit();
        saveSettingsTaskFragment = null;

        saveInProgress = false;
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().remove(fm.findFragmentByTag(SAVE_SETTINGS_TASK_FRAGMENT_TAG)).commit();
        saveSettingsTaskFragment = null;

        // Something went wrong, restore the initial state
        settings.restoreInitialState();

        saveInProgress = false;
    }

    @Override
    public void onBackPressed() {
        if (isSettingsPanelOpen) {
            closeNotificationSettingsWindow();
            return;
        }
        super.onBackPressed();
    }
}
