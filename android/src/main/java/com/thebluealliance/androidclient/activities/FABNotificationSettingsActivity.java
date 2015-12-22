package com.thebluealliance.androidclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.animation.ValueAnimatorCompat;
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
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.interfaces.LoadModelSettingsCallback;
import com.thebluealliance.androidclient.interfaces.ModelSettingsCallbacks;

public abstract class FABNotificationSettingsActivity extends DatafeedActivity implements View.OnClickListener, ModelSettingsCallbacks, LoadModelSettingsCallback {

    private CoordinatorLayout mCoordinatorLayout;
    private RelativeLayout mNotificationSettings;
    private FloatingActionButton mOpenNotificationSettingsButton;
    private FloatingActionButton mCloseNotificationSettingsButton;
    private View mForegroundDim;

    private Toolbar mNotificationSettingsToolbar;
    private Handler mFabHandler = new Handler();

    private NotificationSettingsFragment mSettingsFragment;

    private UpdateUserModelSettingsTaskFragment mSaveSettingsTaskFragment;

    private boolean mIsMyTBAEnabled;

    private boolean mIsSettingsPanelOpen = false;

    private boolean mSaveInProgress = false;

    private boolean mFabVisible = true;
    private ValueAnimator mRunningFabAnimation;
    private AnimatorSet mRunningPanelAnimation;

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

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        mNotificationSettings = (RelativeLayout) findViewById(R.id.notification_settings);
        mOpenNotificationSettingsButton = (FloatingActionButton) findViewById(R.id.open_notification_settings_button);
        mOpenNotificationSettingsButton.setOnClickListener(this);

        mCloseNotificationSettingsButton = (FloatingActionButton) findViewById(R.id.close_notification_settings_button);
        mCloseNotificationSettingsButton.setOnClickListener(this);

        // Hide the notification settings button if myTBA isn't enabled
        if (!AccountHelper.isMyTBAEnabled(this)) {
            mNotificationSettings.setVisibility(View.INVISIBLE);
        }

        mNotificationSettingsToolbar = (Toolbar) findViewById(R.id.notification_settings_toolbar);
        mNotificationSettingsToolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        mNotificationSettingsToolbar.setTitle("Team Settings");
        mNotificationSettingsToolbar.setNavigationOnClickListener(v -> onNotificationSettingsCloseButtonClick());
        mNotificationSettingsToolbar.setNavigationContentDescription(R.string.close);
        ViewCompat.setElevation(mNotificationSettingsToolbar, getResources().getDimension(R.dimen.toolbar_elevation));

        mForegroundDim = findViewById(R.id.activity_foreground_dim);

        // We check this so that we can hide the fab and prevent it from being subsequently shown
        // if myTBA is not enabled
        mIsMyTBAEnabled = AccountHelper.isMyTBAEnabled(this);

        if (!mIsMyTBAEnabled) {
            hideFab(false);
        }

        // Setup the settings menu

        Log.d(Constants.LOG_TAG, "Model: " + modelKey);
        if (savedInstanceState != null) {
            mIsSettingsPanelOpen = savedInstanceState.getBoolean(SETTINGS_PANEL_OPEN);
            if (mIsSettingsPanelOpen) {
                mOpenNotificationSettingsButton.setVisibility(View.INVISIBLE);
                mCloseNotificationSettingsButton.setVisibility(View.VISIBLE);
                mNotificationSettings.setVisibility(View.VISIBLE);

                // Set up system UI (status bar background and icon color
                getDrawerLayout().setStatusBarBackgroundColor(getResources().getColor(R.color
                        .accent_dark));
                int vis = getWindow().getDecorView().getSystemUiVisibility();
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                getWindow().getDecorView().setSystemUiVisibility(vis);
            } else {
                mOpenNotificationSettingsButton.setVisibility(View.VISIBLE);
                mCloseNotificationSettingsButton.setVisibility(View.INVISIBLE);
                mNotificationSettings.setVisibility(View.INVISIBLE);
            }
            savedPreferenceState = savedInstanceState.getBundle(NotificationSettingsFragment.SAVED_STATE_BUNDLE);
        }

        mSaveSettingsTaskFragment = (UpdateUserModelSettingsTaskFragment) getSupportFragmentManager().findFragmentByTag(SAVE_SETTINGS_TASK_FRAGMENT_TAG);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SETTINGS_PANEL_OPEN, mIsSettingsPanelOpen);
        // Only save the preference state if they've already been successfully loaded
        // Also, only save them if the settings panel is open. Otherwise, clear them on rotate
        if (mSettingsFragment != null && mSettingsFragment.arePreferencesLoaded() && mIsSettingsPanelOpen) {
            Bundle b = new Bundle();
            mSettingsFragment.writeStateToBundle(b);
            outState.putBundle(NotificationSettingsFragment.SAVED_STATE_BUNDLE, b);
        }
    }

    @Override
    protected void setModelKey(String key, ModelType modelType) {
        super.setModelKey(key, modelType);
        // Now that we have a model key, we can create a settings fragment for the appropriate model type
        mSettingsFragment = NotificationSettingsFragment.newInstance(modelKey, modelType, savedPreferenceState);
        getFragmentManager().beginTransaction().replace(R.id.settings_list, mSettingsFragment).commit();

        // Disable the submit settings button so we can't hit it before the content is loaded
        // This prevents accidentally wiping settings (see #317)
        mCloseNotificationSettingsButton.setEnabled(false);
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
            if (!mSaveInProgress) {
                openNotificationSettingsView();
            }
        } else if (v.getId() == R.id.close_notification_settings_button) {
            // The user wants to save the preferences
            if (mSaveSettingsTaskFragment == null) {
                mSaveSettingsTaskFragment = new UpdateUserModelSettingsTaskFragment(mSettingsFragment.getSettings());
                getSupportFragmentManager().beginTransaction().add(mSaveSettingsTaskFragment, SAVE_SETTINGS_TASK_FRAGMENT_TAG).commit();
                mSaveInProgress = true;

                final android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                final Fragment settingsFragment = fm.findFragmentByTag(SAVE_SETTINGS_TASK_FRAGMENT_TAG);
                mFabHandler.postDelayed(() -> {
                    closeNotificationSettingsWindow();
                    if (settingsFragment != null) {
                        fm.beginTransaction().remove(settingsFragment).commitAllowingStateLoss();
                    }
                    mSaveSettingsTaskFragment = null;
                }, 1);
            }
        } else {
            Log.d(Constants.LOG_TAG, "Clicked id: " + v.getId() + " tag: " + v.getTag() + " view: " + v.toString());
        }
    }

    private void onNotificationSettingsCloseButtonClick() {
        closeNotificationSettingsWindow();
        // Cancel any changes made by the user
        mSettingsFragment.restoreInitialState();
    }

    private void openNotificationSettingsView() {
        mSettingsFragment.restoreInitialState();
        mCloseNotificationSettingsButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent)));

        // this is the center of the button in relation to the main view. This provides the center of the clipping circle for the notification settings view.
        int centerOfButtonOutsideX = (mOpenNotificationSettingsButton.getLeft() + mOpenNotificationSettingsButton.getRight()) / 2;
        int centerOfButtonOutsideY = (mOpenNotificationSettingsButton.getTop() + mOpenNotificationSettingsButton.getBottom()) / 2;

        float finalRadius = (float) Math.sqrt(Math.pow(centerOfButtonOutsideX - mNotificationSettings.getLeft(), 2) + Math.pow(centerOfButtonOutsideY - mNotificationSettings.getTop(), 2));

        Animator settingsPanelAnimator;
        // Only show the circular reveal on API >= 5.0
        mNotificationSettings.setVisibility(View.VISIBLE);
        if (Utilities.hasLApis()) {
            settingsPanelAnimator = ViewAnimationUtils.createCircularReveal(mNotificationSettings, centerOfButtonOutsideX, centerOfButtonOutsideY, 0, finalRadius);
            settingsPanelAnimator.setDuration(ANIMATION_DURATION);
            settingsPanelAnimator.setInterpolator(new DecelerateInterpolator());
        } else {
            settingsPanelAnimator = ValueAnimator.ofFloat(1, 0);
            final int notificationSettingsHeight = mNotificationSettings.getHeight();
            ((ValueAnimator) settingsPanelAnimator).addUpdateListener(animation -> mNotificationSettings.setTranslationY((float) notificationSettingsHeight * (float) animation.getAnimatedValue()));
            settingsPanelAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            settingsPanelAnimator.setDuration(ANIMATION_DURATION);
        }

        mOpenNotificationSettingsButton.setVisibility(View.INVISIBLE);

        ValueAnimator closeButtonScaleUp = ValueAnimator.ofFloat(0, 1).setDuration(ANIMATION_DURATION);
        closeButtonScaleUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mCloseNotificationSettingsButton.setVisibility(View.VISIBLE);
            }
        });
        closeButtonScaleUp.addUpdateListener(animation -> {
            ViewCompat.setScaleX(mCloseNotificationSettingsButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(mCloseNotificationSettingsButton, (float) animation.getAnimatedValue());
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
        dimAnimation.addUpdateListener(animation -> mForegroundDim.setAlpha((float) animation.getAnimatedValue()));
        dimAnimation.setDuration(ANIMATION_DURATION);

        // Change the system UI color on 6.0+
        ValueAnimator systemUiAnimator = ValueAnimator.ofFloat(0, 1).setDuration(1);
        systemUiAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int vis = getWindow().getDecorView().getSystemUiVisibility();
                    // Set light
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    getWindow().getDecorView().setSystemUiVisibility(vis);
                }
            }
        });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.play(settingsPanelAnimator);
        animationSet.play(systemUiAnimator).after(ANIMATION_DURATION / 2);
        animationSet.play(closeButtonScaleUp).after(ANIMATION_DURATION / 2);
        animationSet.play(colorAnimation).with(settingsPanelAnimator);
        animationSet.play(dimAnimation).with(settingsPanelAnimator);
        animationSet.start();

        mRunningPanelAnimation = animationSet;
        animationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRunningPanelAnimation = null;
            }
        });

        mIsSettingsPanelOpen = true;
    }

    private void closeNotificationSettingsWindow() {
        int centerOfButtonOutsideX = (mOpenNotificationSettingsButton.getLeft() + mOpenNotificationSettingsButton.getRight()) / 2;
        int centerOfButtonOutsideY = (mOpenNotificationSettingsButton.getTop() + mOpenNotificationSettingsButton.getBottom()) / 2;

        float finalRadius = (float) Math.sqrt(Math.pow(centerOfButtonOutsideX - mNotificationSettings.getLeft(), 2) + Math.pow(centerOfButtonOutsideY - mNotificationSettings.getTop(), 2));

        Animator settingsPanelAnimator;
        if (Utilities.hasLApis()) {
            settingsPanelAnimator = ViewAnimationUtils.createCircularReveal(mNotificationSettings, centerOfButtonOutsideX, centerOfButtonOutsideY, finalRadius, 0);
            settingsPanelAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    mNotificationSettings.setVisibility(View.INVISIBLE);
                }
            });
            settingsPanelAnimator.setDuration(ANIMATION_DURATION);
            settingsPanelAnimator.setInterpolator(new AccelerateInterpolator());
        } else {
            settingsPanelAnimator = ValueAnimator.ofFloat(0, 1);
            final int notificationSettingsHeight = mNotificationSettings.getHeight();
            ((ValueAnimator) settingsPanelAnimator).addUpdateListener(animation -> mNotificationSettings.setTranslationY((float) notificationSettingsHeight * (float) animation.getAnimatedValue()));
            settingsPanelAnimator.setDuration(ANIMATION_DURATION);
            settingsPanelAnimator.setInterpolator(new AccelerateInterpolator());
            settingsPanelAnimator.start();
        }

        ValueAnimator closeButtonScaleDown = ValueAnimator.ofFloat(1, 0).setDuration(ANIMATION_DURATION);
        closeButtonScaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCloseNotificationSettingsButton.setVisibility(View.INVISIBLE);
            }
        });
        closeButtonScaleDown.addUpdateListener(animation -> {
            ViewCompat.setScaleX(mCloseNotificationSettingsButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(mCloseNotificationSettingsButton, (float) animation.getAnimatedValue());
        });
        closeButtonScaleDown.setDuration(ANIMATION_DURATION / 2);

        ValueAnimator openButtonScaleUp = ValueAnimator.ofFloat(0, 1).setDuration(ANIMATION_DURATION);
        openButtonScaleUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mOpenNotificationSettingsButton.setVisibility(View.VISIBLE);
            }
        });
        openButtonScaleUp.addUpdateListener(animation -> {
            ViewCompat.setScaleX(mOpenNotificationSettingsButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(mOpenNotificationSettingsButton, (float) animation.getAnimatedValue());
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
        dimAnimation.addUpdateListener(animation -> mForegroundDim.setAlpha((float) animation.getAnimatedValue()));
        dimAnimation.setDuration(ANIMATION_DURATION);

        // Change the system UI color on 6.0+
        ValueAnimator systemUiAnimator = ValueAnimator.ofFloat(0, 0).setDuration(1);
        systemUiAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int vis = getWindow().getDecorView().getSystemUiVisibility();
                    // Set dark
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    getWindow().getDecorView().setSystemUiVisibility(vis);
                }
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(settingsPanelAnimator);
        animatorSet.play(systemUiAnimator).after(ANIMATION_DURATION / 3);
        animatorSet.play(closeButtonScaleDown).after(ANIMATION_DURATION / 2);
        animatorSet.play(colorAnimation).with(settingsPanelAnimator);
        animatorSet.play(dimAnimation).with(settingsPanelAnimator);
        animatorSet.play(openButtonScaleUp).after(settingsPanelAnimator);
        animatorSet.start();

        mRunningPanelAnimation = animatorSet;
        mRunningPanelAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRunningPanelAnimation = null;
            }
        });

        mIsSettingsPanelOpen = false;
    }

    public void showFab(boolean animate) {
        if (mFabVisible) {
            return;
        }
        if (!mIsMyTBAEnabled) {
            hideFab(false);
            return;
        }
        mFabVisible = true;
        if (mRunningFabAnimation != null) {
            mRunningFabAnimation.cancel();
        }
        if (!animate) {
            mOpenNotificationSettingsButton.setVisibility(View.GONE);
            return;
        }
        ValueAnimator fabScaleUp = ValueAnimator.ofFloat(0, 1);
        fabScaleUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mOpenNotificationSettingsButton.setVisibility(View.VISIBLE);
            }
        });
        fabScaleUp.addUpdateListener(animation -> {
            ViewCompat.setScaleX(mOpenNotificationSettingsButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(mOpenNotificationSettingsButton, (float) animation.getAnimatedValue());
        });
        fabScaleUp.setDuration(FAB_ANIMATION_DURATION);
        fabScaleUp.setInterpolator(new DecelerateInterpolator());
        fabScaleUp.start();
        mRunningFabAnimation = fabScaleUp;
    }

    public void hideFab(boolean animate) {
        if (!mFabVisible) {
            return;
        }
        mFabVisible = false;
        if (mRunningFabAnimation != null) {
            mRunningFabAnimation.cancel();
        }
        if (!animate) {
            mOpenNotificationSettingsButton.setVisibility(View.GONE);
            return;
        }
        ValueAnimator fabScaleDown = ValueAnimator.ofFloat(1, 0);
        fabScaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mOpenNotificationSettingsButton.setVisibility(View.VISIBLE);
            }
        });
        fabScaleDown.addUpdateListener(animation -> {
            ViewCompat.setScaleX(mOpenNotificationSettingsButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(mOpenNotificationSettingsButton, (float) animation.getAnimatedValue());
        });
        fabScaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mOpenNotificationSettingsButton.setVisibility(View.GONE);
            }
        });
        fabScaleDown.setDuration(FAB_ANIMATION_DURATION);
        fabScaleDown.setInterpolator(new AccelerateInterpolator());
        fabScaleDown.start();
        mRunningFabAnimation = fabScaleDown;
    }

    public void setSettingsToolbarTitle(String title) {
        mNotificationSettingsToolbar.setTitle(title);
    }

    @Override
    public void onSuccess() {
        Runnable runnable = () -> {
            showSnackbar("Settings updated successfully");

            Integer colorFrom = getResources().getColor(R.color.accent);
            Integer colorTo = getResources().getColor(R.color.green);

            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(animator -> mOpenNotificationSettingsButton.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue())));
            colorAnimation.setDuration(FAB_COLOR_ANIMATION_DURATION);

            ValueAnimator reverseColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorFrom);
            reverseColorAnimation.addUpdateListener(animator -> mOpenNotificationSettingsButton.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue())));
            reverseColorAnimation.setDuration(FAB_COLOR_ANIMATION_DURATION);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(colorAnimation);
            animatorSet.play(reverseColorAnimation).after(2000);
            animatorSet.start();
        };
        runAfterNotificationSettingsPanelIsClosed(runnable);

        // Tell the settings fragment to reload the now-updated settings
        mSettingsFragment.refreshSettingsFromDatabase();

        // Save finished
        mSaveInProgress = false;
    }

    @Override
    public void onNoOp() {
        Runnable runnable = () -> {
            showSnackbar("Settings not changed");
        };
        runAfterNotificationSettingsPanelIsClosed(runnable);

        mSaveInProgress = false;
    }

    @Override
    public void onError() {
        Runnable runnable = () -> {
            showSnackbar("Error updating settings");
            // Something went wrong, restore the initial state
            mSettingsFragment.restoreInitialState();

            Integer colorFrom = getResources().getColor(R.color.accent);
            Integer colorTo = getResources().getColor(R.color.red);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(animator -> mOpenNotificationSettingsButton.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue())));
            colorAnimation.setDuration(FAB_COLOR_ANIMATION_DURATION);

            ValueAnimator reverseColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorFrom);
            reverseColorAnimation.addUpdateListener(animator -> mOpenNotificationSettingsButton.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue())));
            reverseColorAnimation.setDuration(FAB_COLOR_ANIMATION_DURATION);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(colorAnimation);
            animatorSet.play(reverseColorAnimation).after(2000);
            animatorSet.start();
        };
        runAfterNotificationSettingsPanelIsClosed(runnable);

        mSaveInProgress = false;
    }

    @Override
    public void onBackPressed() {
        if (mIsSettingsPanelOpen) {
            closeNotificationSettingsWindow();
            return;
        }
        super.onBackPressed();
    }

    public void onSettingsLoaded() {
        // Re-enable the submit button
        mCloseNotificationSettingsButton.setEnabled(true);
    }

    private void showSnackbar(int messageResId) {
        showSnackbar(getResources().getString(messageResId));
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, 2000);
        TextView text = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        if (text != null) {
            text.setTextColor(getResources().getColor(R.color.white));
        }
        snackbar.show();
    }

    /**
     * Used to defer an operation until after the notifications setting panel has finished
     * animating
     * closed.
     * <p>
     * If the panel has already finished animating when this is called, the Runnable will be run
     * immediately.
     */
    private void runAfterNotificationSettingsPanelIsClosed(Runnable runnable) {
        if (mRunningPanelAnimation == null) {
            runnable.run();
        } else {
            mRunningPanelAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    runnable.run();
                }
            });
        }
    }
}
