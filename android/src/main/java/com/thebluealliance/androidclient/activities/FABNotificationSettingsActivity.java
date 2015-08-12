package com.thebluealliance.androidclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.fragments.mytba.NotificationSettingsFragment;
import com.thebluealliance.androidclient.fragments.tasks.UpdateUserModelSettingsTaskFragment;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.interfaces.LoadModelSettingsCallback;
import com.thebluealliance.androidclient.interfaces.ModelSettingsCallbacks;

public abstract class FABNotificationSettingsActivity extends LegacyRefreshableHostActivity implements View.OnClickListener, ModelSettingsCallbacks, LoadModelSettingsCallback {

    private CoordinatorLayout coordinator;
    private RelativeLayout notificationSettings;
    private FloatingActionButton openNotificationSettingsButton;
    private FloatingActionButton closeNotificationSettingsButton;
    private View foregroundDim;

    private Toolbar notificationSettingsToolbar;
    private Handler fabHandler = new Handler();

    private NotificationSettingsFragment settings;

    private UpdateUserModelSettingsTaskFragment saveSettingsTaskFragment;

    private boolean isSettingsPanelOpen = false;

    private boolean saveInProgress = false;

    private boolean fabVisible = true;
    private ValueAnimator runningFabAnimation;
    private AnimatorSet runningPanelAnimation;

    private static final String SETTINGS_PANEL_OPEN = "settings_panel_open";

    private static final String SAVE_SETTINGS_TASK_FRAGMENT_TAG = "task_fragment_tag";

    // In milliseconds
    private static final int ANIMATION_DURATION = 500;
    private static final int FAB_ANIMATION_DURATION = 250;
    private static final int FAB_COLOR_ANIMATION_DURATION = 250;

    private static final float UNDIMMED_ALPHA = 0.0f;

    private static final float DIMMED_ALPHA = 0.7f;

    private Bundle savedPreferenceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.activity_fab_notification_settings);

        coordinator = (CoordinatorLayout) findViewById(R.id.coordinator);

        notificationSettings = (RelativeLayout) findViewById(R.id.notification_settings);
        openNotificationSettingsButton = (FloatingActionButton) findViewById(R.id.open_notification_settings_button);
        openNotificationSettingsButton.setOnClickListener(this);

        closeNotificationSettingsButton = (FloatingActionButton) findViewById(R.id.close_notification_settings_button);
        closeNotificationSettingsButton.setOnClickListener(this);

        // Hide the notification settings button if myTBA isn't enabled
        if (!AccountHelper.isMyTBAEnabled(this)) {
            notificationSettings.setVisibility(View.INVISIBLE);
        }

        notificationSettingsToolbar = (Toolbar) findViewById(R.id.notification_settings_toolbar);
        notificationSettingsToolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        notificationSettingsToolbar.setTitle("Team Settings");
        notificationSettingsToolbar.setNavigationOnClickListener(v -> onNotificationSettingsCloseButtonClick());
        notificationSettingsToolbar.setNavigationContentDescription(R.string.close);
        ViewCompat.setElevation(notificationSettingsToolbar, getResources().getDimension(R.dimen.toolbar_elevation));

        foregroundDim = findViewById(R.id.activity_foreground_dim);

        // Setup the settings menu

        Log.d(Constants.LOG_TAG, "Model: " + modelKey);
        if (savedInstanceState != null) {
            isSettingsPanelOpen = savedInstanceState.getBoolean(SETTINGS_PANEL_OPEN);
            if (isSettingsPanelOpen) {
                openNotificationSettingsButton.setVisibility(View.INVISIBLE);
                closeNotificationSettingsButton.setVisibility(View.VISIBLE);
                notificationSettings.setVisibility(View.VISIBLE);
                if (Utilities.hasLApis()) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.accent_dark));
                }
            } else {
                openNotificationSettingsButton.setVisibility(View.VISIBLE);
                closeNotificationSettingsButton.setVisibility(View.INVISIBLE);
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

        // Disable the submit settings button so we can't hit it before the content is loaded
        // This prevents accidently wiping settings (see #317)
        closeNotificationSettingsButton.setEnabled(false);
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
            if (!saveInProgress) {
                openNotificationSettingsView();
            }
        } else if (v.getId() == R.id.close_notification_settings_button) {
            // The user wants to save the preferences
            if (saveSettingsTaskFragment == null) {
                saveSettingsTaskFragment = new UpdateUserModelSettingsTaskFragment(settings.getSettings());
                getSupportFragmentManager().beginTransaction().add(saveSettingsTaskFragment, SAVE_SETTINGS_TASK_FRAGMENT_TAG).commit();
                saveInProgress = true;

                final android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                final Fragment settingsFragment = fm.findFragmentByTag(SAVE_SETTINGS_TASK_FRAGMENT_TAG);
                fabHandler.postDelayed(() -> {
                    closeNotificationSettingsWindow();
                    if (settingsFragment != null) {
                        fm.beginTransaction().remove(settingsFragment).commitAllowingStateLoss();
                    }
                    saveSettingsTaskFragment = null;
                }, 1);
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
        settings.restoreInitialState();
        closeNotificationSettingsButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent)));

        // this is the center of the button in relation to the main view. This provides the center of the clipping circle for the notification settings view.
        int centerOfButtonOutsideX = (openNotificationSettingsButton.getLeft() + openNotificationSettingsButton.getRight()) / 2;
        int centerOfButtonOutsideY = (openNotificationSettingsButton.getTop() + openNotificationSettingsButton.getBottom()) / 2;

        float finalRadius = (float) Math.sqrt(Math.pow(centerOfButtonOutsideX - notificationSettings.getLeft(), 2) + Math.pow(centerOfButtonOutsideY - notificationSettings.getTop(), 2));

        Animator settingsPanelAnimator;
        // Only show the circular reveal on API >= 5.0
        notificationSettings.setVisibility(View.VISIBLE);
        if (Utilities.hasLApis()) {
            settingsPanelAnimator = ViewAnimationUtils.createCircularReveal(notificationSettings, centerOfButtonOutsideX, centerOfButtonOutsideY, 0, finalRadius);
            settingsPanelAnimator.setDuration(ANIMATION_DURATION);
            settingsPanelAnimator.setInterpolator(new DecelerateInterpolator());
        } else {
            settingsPanelAnimator = ValueAnimator.ofFloat(1, 0);
            final int notificationSettingsHeight = notificationSettings.getHeight();
            ((ValueAnimator) settingsPanelAnimator).addUpdateListener(animation -> notificationSettings.setTranslationY((float) notificationSettingsHeight * (float) animation.getAnimatedValue()));
            settingsPanelAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            settingsPanelAnimator.setDuration(ANIMATION_DURATION);
        }

        openNotificationSettingsButton.setVisibility(View.INVISIBLE);

        ValueAnimator closeButtonScaleUp = ValueAnimator.ofFloat(0, 1).setDuration(ANIMATION_DURATION);
        closeButtonScaleUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                closeNotificationSettingsButton.setVisibility(View.VISIBLE);
            }
        });
        closeButtonScaleUp.addUpdateListener(animation -> {
            ViewCompat.setScaleX(closeNotificationSettingsButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(closeNotificationSettingsButton, (float) animation.getAnimatedValue());
        });
        closeButtonScaleUp.setDuration(ANIMATION_DURATION / 2);

        // Animate the status bar color change
        Integer colorFrom = getResources().getColor(R.color.primary_dark);
        Integer colorTo = getResources().getColor(R.color.accent_dark);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(animator -> {
            getDrawerLayout().setStatusBarBackgroundColor((Integer) animator.getAnimatedValue());
            // We have to invalidate so that the view redraws the background
            getDrawerLayout().invalidate();
        });
        colorAnimation.setDuration(ANIMATION_DURATION);

        ValueAnimator dimAnimation = ValueAnimator.ofFloat(UNDIMMED_ALPHA, DIMMED_ALPHA);
        dimAnimation.addUpdateListener(animation -> foregroundDim.setAlpha((float) animation.getAnimatedValue()));
        dimAnimation.setDuration(ANIMATION_DURATION);

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.play(settingsPanelAnimator);
        animationSet.play(closeButtonScaleUp).after(ANIMATION_DURATION / 2);
        animationSet.play(colorAnimation).with(settingsPanelAnimator);
        animationSet.play(dimAnimation).with(settingsPanelAnimator);
        animationSet.start();

        runningPanelAnimation = animationSet;
        animationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                runningPanelAnimation = null;
            }
        });

        isSettingsPanelOpen = true;
    }

    private void closeNotificationSettingsWindow() {
        int centerOfButtonOutsideX = (openNotificationSettingsButton.getLeft() + openNotificationSettingsButton.getRight()) / 2;
        int centerOfButtonOutsideY = (openNotificationSettingsButton.getTop() + openNotificationSettingsButton.getBottom()) / 2;

        float finalRadius = (float) Math.sqrt(Math.pow(centerOfButtonOutsideX - notificationSettings.getLeft(), 2) + Math.pow(centerOfButtonOutsideY - notificationSettings.getTop(), 2));

        Animator settingsPanelAnimator;
        if (Utilities.hasLApis()) {
            settingsPanelAnimator = ViewAnimationUtils.createCircularReveal(notificationSettings, centerOfButtonOutsideX, centerOfButtonOutsideY, finalRadius, 0);
            settingsPanelAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    notificationSettings.setVisibility(View.INVISIBLE);
                }
            });
            settingsPanelAnimator.setDuration(ANIMATION_DURATION);
            settingsPanelAnimator.setInterpolator(new AccelerateInterpolator());
        } else {
            settingsPanelAnimator = ValueAnimator.ofFloat(0, 1);
            final int notificationSettingsHeight = notificationSettings.getHeight();
            ((ValueAnimator) settingsPanelAnimator).addUpdateListener(animation -> notificationSettings.setTranslationY((float) notificationSettingsHeight * (float) animation.getAnimatedValue()));
            settingsPanelAnimator.setDuration(ANIMATION_DURATION);
            settingsPanelAnimator.setInterpolator(new AccelerateInterpolator());
            settingsPanelAnimator.start();
        }

        ValueAnimator closeButtonScaleDown = ValueAnimator.ofFloat(1, 0).setDuration(ANIMATION_DURATION);
        closeButtonScaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                closeNotificationSettingsButton.setVisibility(View.INVISIBLE);
            }
        });
        closeButtonScaleDown.addUpdateListener(animation -> {
            ViewCompat.setScaleX(closeNotificationSettingsButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(closeNotificationSettingsButton, (float) animation.getAnimatedValue());
        });
        closeButtonScaleDown.setDuration(ANIMATION_DURATION / 2);

        ValueAnimator openButtonScaleUp = ValueAnimator.ofFloat(0, 1).setDuration(ANIMATION_DURATION);
        openButtonScaleUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                openNotificationSettingsButton.setVisibility(View.VISIBLE);
            }
        });
        openButtonScaleUp.addUpdateListener(animation -> {
            ViewCompat.setScaleX(openNotificationSettingsButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(openNotificationSettingsButton, (float) animation.getAnimatedValue());
        });
        openButtonScaleUp.setDuration(ANIMATION_DURATION / 2);

        // Animate the status bar color change
        Integer colorFrom = getResources().getColor(R.color.accent_dark);
        Integer colorTo = getResources().getColor(R.color.primary_dark);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(animator -> {
            getDrawerLayout().setStatusBarBackgroundColor((Integer) animator.getAnimatedValue());
            // We have to invalidate so that the view redraws the background
            getDrawerLayout().invalidate();
        });
        colorAnimation.setDuration(ANIMATION_DURATION);

        // Undim the foreground
        ValueAnimator dimAnimation = ValueAnimator.ofFloat(DIMMED_ALPHA, UNDIMMED_ALPHA);
        dimAnimation.addUpdateListener(animation -> foregroundDim.setAlpha((float) animation.getAnimatedValue()));
        dimAnimation.setDuration(ANIMATION_DURATION);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(settingsPanelAnimator);
        animatorSet.play(closeButtonScaleDown).after(ANIMATION_DURATION / 2);
        animatorSet.play(colorAnimation).with(settingsPanelAnimator);
        animatorSet.play(dimAnimation).with(settingsPanelAnimator);
        animatorSet.play(openButtonScaleUp).after(settingsPanelAnimator);
        animatorSet.start();

        runningPanelAnimation = animatorSet;
        runningPanelAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                runningPanelAnimation = null;
            }
        });

        isSettingsPanelOpen = false;
    }

    public void showFab(boolean animate) {
        if (fabVisible) {
            return;
        }
        fabVisible = true;
        if (runningFabAnimation != null) {
            runningFabAnimation.cancel();
        }
        if (!animate) {
            openNotificationSettingsButton.setVisibility(View.GONE);
            return;
        }
        ValueAnimator fabScaleUp = ValueAnimator.ofFloat(0, 1);
        fabScaleUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                openNotificationSettingsButton.setVisibility(View.VISIBLE);
            }
        });
        fabScaleUp.addUpdateListener(animation -> {
            ViewCompat.setScaleX(openNotificationSettingsButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(openNotificationSettingsButton, (float) animation.getAnimatedValue());
        });
        fabScaleUp.setDuration(FAB_ANIMATION_DURATION);
        fabScaleUp.setInterpolator(new DecelerateInterpolator());
        fabScaleUp.start();
        runningFabAnimation = fabScaleUp;
    }

    public void hideFab(boolean animate) {
        if (!fabVisible) {
            return;
        }
        fabVisible = false;
        if (runningFabAnimation != null) {
            runningFabAnimation.cancel();
        }
        if (!animate) {
            openNotificationSettingsButton.setVisibility(View.GONE);
            return;
        }
        ValueAnimator fabScaleDown = ValueAnimator.ofFloat(1, 0);
        fabScaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                openNotificationSettingsButton.setVisibility(View.VISIBLE);
            }
        });
        fabScaleDown.addUpdateListener(animation -> {
            ViewCompat.setScaleX(openNotificationSettingsButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(openNotificationSettingsButton, (float) animation.getAnimatedValue());
        });
        fabScaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                openNotificationSettingsButton.setVisibility(View.GONE);
            }
        });
        fabScaleDown.setDuration(FAB_ANIMATION_DURATION);
        fabScaleDown.setInterpolator(new AccelerateInterpolator());
        fabScaleDown.start();
        runningFabAnimation = fabScaleDown;
    }

    public void setSettingsToolbarTitle(String title) {
        notificationSettingsToolbar.setTitle(title);
    }

    @Override
    public void onSuccess() {
        Runnable runnable = () -> {
            showSnackbar("Settings updated successfully");

            Integer colorFrom = getResources().getColor(R.color.accent);
            Integer colorTo = getResources().getColor(R.color.green);

            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(animator -> openNotificationSettingsButton.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue())));
            colorAnimation.setDuration(FAB_COLOR_ANIMATION_DURATION);

            ValueAnimator reverseColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorFrom);
            reverseColorAnimation.addUpdateListener(animator -> openNotificationSettingsButton.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue())));
            reverseColorAnimation.setDuration(FAB_COLOR_ANIMATION_DURATION);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(colorAnimation);
            animatorSet.play(reverseColorAnimation).after(2000);
            animatorSet.start();
        };
        runAfterNotificationSettingsPanelIsClosed(runnable);

        // Tell the settings fragment to reload the now-updated settings
        settings.refreshSettingsFromDatabase();

        // Save finished
        saveInProgress = false;
    }

    @Override
    public void onNoOp() {
        Runnable runnable = () -> {
            showSnackbar("Settings not changed");
        };
        runAfterNotificationSettingsPanelIsClosed(runnable);

        saveInProgress = false;
    }

    @Override
    public void onError() {
        Runnable runnable = () -> {
            showSnackbar("Error updating settings");
            // Something went wrong, restore the initial state
            settings.restoreInitialState();

            Integer colorFrom = getResources().getColor(R.color.accent);
            Integer colorTo = getResources().getColor(R.color.red);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(animator -> openNotificationSettingsButton.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue())));
            colorAnimation.setDuration(FAB_COLOR_ANIMATION_DURATION);

            ValueAnimator reverseColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorFrom);
            reverseColorAnimation.addUpdateListener(animator -> openNotificationSettingsButton.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue())));
            reverseColorAnimation.setDuration(FAB_COLOR_ANIMATION_DURATION);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(colorAnimation);
            animatorSet.play(reverseColorAnimation).after(2000);
            animatorSet.start();
        };
        runAfterNotificationSettingsPanelIsClosed(runnable);

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

    public void onSettingsLoaded() {
        // Re-enable the submit button
        closeNotificationSettingsButton.setEnabled(true);
    }

    private void showSnackbar(int messageResId) {
        showSnackbar(getResources().getString(messageResId));
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(coordinator, message, 2000);
        TextView text = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        if (text != null) {
            text.setTextColor(getResources().getColor(R.color.white));
        }
        snackbar.show();
    }

    /**
     * Used to defer an operation until after the notifications setting panel has finished animating closed.
     *
     * If the panel has already finished animating when this is called, the Runnable will be run immediately.
     *
     * @param runnable
     */
    private void runAfterNotificationSettingsPanelIsClosed(Runnable runnable) {
        if (runningPanelAnimation == null) {
            runnable.run();
        } else {
            runningPanelAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    runnable.run();
                }
            });
        }
    }
}
