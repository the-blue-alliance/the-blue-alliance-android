package com.thebluealliance.androidclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
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
import com.thebluealliance.androidclient.fragments.mytba.MyTBASettingsFragment;
import com.thebluealliance.androidclient.fragments.tasks.UpdateUserModelSettingsTaskFragment;
import com.thebluealliance.androidclient.interfaces.LoadModelSettingsCallback;
import com.thebluealliance.androidclient.interfaces.ModelSettingsCallbacks;
import com.thebluealliance.androidclient.types.ModelType;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Activity which hosts a FAB that opens a myTBA model settings panel.
 */

public abstract class MyTBASettingsActivity extends DatafeedActivity implements View.OnClickListener, ModelSettingsCallbacks, LoadModelSettingsCallback {

    private static final String SETTINGS_PANEL_OPEN = "settings_panel_open";
    private static final String SAVE_SETTINGS_TASK_FRAGMENT_TAG = "task_fragment_tag";

    @Bind(R.id.coordinator) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.settings) RelativeLayout mSettingsContainer;
    @Bind(R.id.toggle_settings_button) FloatingActionButton mToggleSettingsPanelButton;
    @Bind(R.id.activity_foreground_dim) View mForegroundDim;
    @Bind(R.id.settings_toolbar) Toolbar mSettingsToolbar;

    private Handler mFabHandler = new Handler();

    private MyTBASettingsFragment mSettingsFragment;
    private UpdateUserModelSettingsTaskFragment mSaveSettingsTaskFragment;

    private boolean mIsMyTBAEnabled;
    private boolean mIsSettingsPanelOpen = false;
    private boolean mIsSaveEnalbed = false;
    private boolean mSaveInProgress = false;
    private boolean mFabVisible = true;

    // Track animations so we can cancel them if needed
    private AnimatorSet mFabColorAnimator;
    private ValueAnimator mRunningFabAnimation;
    private AnimatorSet mRunningPanelAnimation;

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

        super.setContentView(R.layout.activity_mytba_settings);

        ButterKnife.bind(this);

        mToggleSettingsPanelButton.setOnClickListener(this);

        mSettingsToolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        mSettingsToolbar.setTitle("Team Settings");
        mSettingsToolbar.setNavigationOnClickListener(v -> onSettingsCloseButtonClick());
        mSettingsToolbar.setNavigationContentDescription(R.string.close);
        ViewCompat.setElevation(mSettingsToolbar, getResources().getDimension(R.dimen.toolbar_elevation));

        // We check this so that we can hide the fab and prevent it from being subsequently shown
        // if myTBA is not enabled
        mIsMyTBAEnabled = AccountHelper.isMyTBAEnabled(this);

        if (!mIsMyTBAEnabled) {
            hideFab(false);
        }

        setupFabIconForSettingsPanelOpen(false);

        // Setup the settings menu
        Log.d(Constants.LOG_TAG, "Model: " + modelKey);
        if (savedInstanceState != null) {
            mIsSettingsPanelOpen = savedInstanceState.getBoolean(SETTINGS_PANEL_OPEN);
            setupFabIconForSettingsPanelOpen(mIsSettingsPanelOpen);
            if (mIsSettingsPanelOpen) {
                mSettingsContainer.setVisibility(View.VISIBLE);
                // Set up system UI (status bar background and icon color
                getDrawerLayout().setStatusBarBackgroundColor(getResources().getColor(R.color
                        .accent_dark));
                Utilities.setLightStatusBar(getWindow(), true);
            } else {
                mSettingsContainer.setVisibility(View.INVISIBLE);
            }
            savedPreferenceState = savedInstanceState.getBundle(MyTBASettingsFragment.SAVED_STATE_BUNDLE);
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
            outState.putBundle(MyTBASettingsFragment.SAVED_STATE_BUNDLE, b);
        }
    }

    @Override
    protected void setModelKey(String key, ModelType modelType) {
        super.setModelKey(key, modelType);
        // Now that we have a model key, we can create a settings fragment for the appropriate model type
        mSettingsFragment = MyTBASettingsFragment.newInstance(modelKey, modelType, savedPreferenceState);
        getFragmentManager().beginTransaction().replace(R.id.settings_list, mSettingsFragment).commit();

        // Disable the submit settings button so we can't hit it before the content is loaded
        // This prevents accidentally wiping settings (see #317)
        mIsSaveEnalbed = false;
    }

    @Override
    public void setContentView(int layoutResID) {
        FrameLayout root = (FrameLayout) findViewById(R.id.activity_content);
        LayoutInflater inflater = getLayoutInflater();
        inflater.inflate(layoutResID, root);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toggle_settings_button) {
            if (!mIsSettingsPanelOpen) {
                if (!mSaveInProgress) {
                    openSettingsPanel();
                }
            } else {
                // The user wants to save the preferences
                // Always close the panel
                mFabHandler.postDelayed(() -> {
                    closeSettingsPanel();
                }, 1);

                // If saving is disabled, don't attempt to save
                if (!mIsSaveEnalbed) {
                    return;
                }

                if (mSaveSettingsTaskFragment == null) {
                    mSaveSettingsTaskFragment = new UpdateUserModelSettingsTaskFragment(mSettingsFragment.getSettings());
                    getSupportFragmentManager().beginTransaction().add(mSaveSettingsTaskFragment, SAVE_SETTINGS_TASK_FRAGMENT_TAG).commit();
                    mSaveInProgress = true;

                    final android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                    final Fragment settingsFragment = fm.findFragmentByTag(SAVE_SETTINGS_TASK_FRAGMENT_TAG);
                    mFabHandler.postDelayed(() -> {
                        if (settingsFragment != null) {
                            fm.beginTransaction().remove(settingsFragment).commitAllowingStateLoss();
                        }
                        mSaveSettingsTaskFragment = null;
                    }, 1);
                }
            }
        }
    }

    /**
     * Since we reuse the same FAB for both the open and close buttons, we have to toggle the icon
     * displayed by the FAB. This method does that. If the panel is closed, the FAB displays a
     * star; if it is open, it displays a checkmark (for the "save" action).
     *
     * @param panelOpen if the settings panel is open
     */
    private void setupFabIconForSettingsPanelOpen(boolean panelOpen) {
        mToggleSettingsPanelButton.setImageResource(panelOpen ? R.drawable.ic_check_white_24dp : R.drawable.ic_star_white_24dp);
    }

    /**
     * Called when the user chooses to close the settings panel without saving their changes.
     */
    private void onSettingsCloseButtonClick() {
        closeSettingsPanel();
        // Cancel any changes made by the user
        mSettingsFragment.restoreInitialState();
    }

    private void openSettingsPanel() {
        mSettingsFragment.restoreInitialState();

        // Reset the color of the button
        if (mFabColorAnimator != null) {
            mFabColorAnimator.cancel();
        }
        mToggleSettingsPanelButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent)));

        // Hide the button immediately
        mToggleSettingsPanelButton.setVisibility(View.GONE);

        // this is the center of the button in relation to the main view. This provides the center of the clipping circle for the settings view.
        int centerOfButtonOutsideX = (mToggleSettingsPanelButton.getLeft() + mToggleSettingsPanelButton.getRight()) / 2;
        int centerOfButtonOutsideY = (mToggleSettingsPanelButton.getTop() + mToggleSettingsPanelButton.getBottom()) / 2;

        float finalRadius = (float) Math.sqrt(Math.pow(centerOfButtonOutsideX - mSettingsContainer.getLeft(), 2) + Math.pow(centerOfButtonOutsideY - mSettingsContainer.getTop(), 2));

        Animator settingsPanelAnimator;
        // Only show the circular reveal on API >= 5.0
        mSettingsContainer.setVisibility(View.VISIBLE);
        if (Utilities.hasLApis()) {
            settingsPanelAnimator = ViewAnimationUtils.createCircularReveal(mSettingsContainer, centerOfButtonOutsideX, centerOfButtonOutsideY, 0, finalRadius);
            settingsPanelAnimator.setDuration(ANIMATION_DURATION);
            settingsPanelAnimator.setInterpolator(new DecelerateInterpolator());
        } else {
            settingsPanelAnimator = ValueAnimator.ofFloat(1, 0);
            final int settingsContainerHeight = mSettingsContainer.getHeight();
            ((ValueAnimator) settingsPanelAnimator).addUpdateListener(animation -> mSettingsContainer.setTranslationY((float) settingsContainerHeight * (float) animation.getAnimatedValue()));
            settingsPanelAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            settingsPanelAnimator.setDuration(ANIMATION_DURATION);
        }

        ValueAnimator toggleButtonScaleUpAnimation = ValueAnimator.ofFloat(0, 1).setDuration(ANIMATION_DURATION);
        toggleButtonScaleUpAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mToggleSettingsPanelButton.setVisibility(View.VISIBLE);
                setupFabIconForSettingsPanelOpen(true);
            }
        });
        toggleButtonScaleUpAnimation.addUpdateListener(animation -> {
            ViewCompat.setScaleX(mToggleSettingsPanelButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(mToggleSettingsPanelButton, (float) animation.getAnimatedValue());
        });
        toggleButtonScaleUpAnimation.setDuration(ANIMATION_DURATION / 2);

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
                Utilities.setLightStatusBar(getWindow(), true);
            }
        });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.play(settingsPanelAnimator);
        animationSet.play(systemUiAnimator).after(ANIMATION_DURATION / 2);
        animationSet.play(toggleButtonScaleUpAnimation).after(ANIMATION_DURATION / 2);
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

    private void closeSettingsPanel() {
        int centerOfButtonOutsideX = (mToggleSettingsPanelButton.getLeft() + mToggleSettingsPanelButton.getRight()) / 2;
        int centerOfButtonOutsideY = (mToggleSettingsPanelButton.getTop() + mToggleSettingsPanelButton.getBottom()) / 2;

        float finalRadius = (float) Math.sqrt(Math.pow(centerOfButtonOutsideX - mSettingsContainer.getLeft(), 2) + Math.pow(centerOfButtonOutsideY - mSettingsContainer.getTop(), 2));

        Animator settingsPanelAnimator;
        if (Utilities.hasLApis()) {
            settingsPanelAnimator = ViewAnimationUtils.createCircularReveal(mSettingsContainer, centerOfButtonOutsideX, centerOfButtonOutsideY, finalRadius, 0);
            settingsPanelAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    mSettingsContainer.setVisibility(View.INVISIBLE);
                }
            });
            settingsPanelAnimator.setDuration(ANIMATION_DURATION);
            settingsPanelAnimator.setInterpolator(new AccelerateInterpolator());
        } else {
            settingsPanelAnimator = ValueAnimator.ofFloat(0, 1);
            final int settingsContainerHeight = mSettingsContainer.getHeight();
            ((ValueAnimator) settingsPanelAnimator).addUpdateListener(animation -> mSettingsContainer.setTranslationY((float) settingsContainerHeight * (float) animation.getAnimatedValue()));
            settingsPanelAnimator.setDuration(ANIMATION_DURATION);
            settingsPanelAnimator.setInterpolator(new AccelerateInterpolator());
            settingsPanelAnimator.start();
        }

        ValueAnimator toggleButtonScaleDownAnimation = ValueAnimator.ofFloat(1, 0).setDuration(ANIMATION_DURATION);
        toggleButtonScaleDownAnimation.addUpdateListener(animation -> {
            ViewCompat.setScaleX(mToggleSettingsPanelButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(mToggleSettingsPanelButton, (float) animation.getAnimatedValue());
        });
        toggleButtonScaleDownAnimation.setDuration(ANIMATION_DURATION / 2);

        ValueAnimator toggleButtonScaleUpAnimation = ValueAnimator.ofFloat(0, 1).setDuration(ANIMATION_DURATION);
        toggleButtonScaleUpAnimation.addUpdateListener(animation -> {
            ViewCompat.setScaleX(mToggleSettingsPanelButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(mToggleSettingsPanelButton, (float) animation.getAnimatedValue());
        });
        toggleButtonScaleUpAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                setupFabIconForSettingsPanelOpen(false);
            }
        });
        toggleButtonScaleUpAnimation.setDuration(ANIMATION_DURATION / 2);

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
                Utilities.setLightStatusBar(getWindow(), false);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(settingsPanelAnimator);
        animatorSet.play(systemUiAnimator).after(ANIMATION_DURATION / 3);
        animatorSet.play(toggleButtonScaleDownAnimation).after(ANIMATION_DURATION / 2);
        animatorSet.play(colorAnimation).with(settingsPanelAnimator);
        animatorSet.play(dimAnimation).with(settingsPanelAnimator);
        animatorSet.play(toggleButtonScaleUpAnimation).after(settingsPanelAnimator);
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
            mToggleSettingsPanelButton.setVisibility(View.GONE);
            return;
        }
        ValueAnimator fabScaleUp = ValueAnimator.ofFloat(0, 1);
        fabScaleUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mToggleSettingsPanelButton.setVisibility(View.VISIBLE);
            }
        });
        fabScaleUp.addUpdateListener(animation -> {
            ViewCompat.setScaleX(mToggleSettingsPanelButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(mToggleSettingsPanelButton, (float) animation.getAnimatedValue());
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
            mToggleSettingsPanelButton.setVisibility(View.GONE);
            return;
        }
        ValueAnimator fabScaleDown = ValueAnimator.ofFloat(1, 0);
        fabScaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mToggleSettingsPanelButton.setVisibility(View.VISIBLE);
            }
        });
        fabScaleDown.addUpdateListener(animation -> {
            ViewCompat.setScaleX(mToggleSettingsPanelButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(mToggleSettingsPanelButton, (float) animation.getAnimatedValue());
        });
        fabScaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mToggleSettingsPanelButton.setVisibility(View.GONE);
            }
        });
        fabScaleDown.setDuration(FAB_ANIMATION_DURATION);
        fabScaleDown.setInterpolator(new AccelerateInterpolator());
        fabScaleDown.start();
        mRunningFabAnimation = fabScaleDown;
    }

    public void setSettingsToolbarTitle(String title) {
        mSettingsToolbar.setTitle(title);
    }

    @Override
    public void onSuccess() {
        Runnable runnable = () -> {
            showSnackbar(R.string.mytba_settings_updated_successfully);

            Integer colorFrom = getResources().getColor(R.color.accent);
            Integer colorTo = getResources().getColor(R.color.green);

            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(animator -> mToggleSettingsPanelButton.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue())));
            colorAnimation.setDuration(FAB_COLOR_ANIMATION_DURATION);

            ValueAnimator reverseColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorFrom);
            reverseColorAnimation.addUpdateListener(animator -> mToggleSettingsPanelButton.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue())));
            reverseColorAnimation.setDuration(FAB_COLOR_ANIMATION_DURATION);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(colorAnimation);
            animatorSet.play(reverseColorAnimation).after(2000);
            animatorSet.start();
        };
        runAfterSettingsPanelIsClosed(runnable);

        // Tell the settings fragment to reload the now-updated settings
        mSettingsFragment.refreshSettingsFromDatabase();

        // Save finished
        mSaveInProgress = false;
    }

    @Override
    public void onNoOp() {
        runAfterSettingsPanelIsClosed(() -> showSnackbar(R.string.mytba_settings_not_changed));

        mSaveInProgress = false;
    }

    @Override
    public void onError() {
        Runnable runnable = () -> {
            showSnackbar(R.string.mytba_settings_error);
            // Something went wrong, restore the initial state
            mSettingsFragment.restoreInitialState();

            Integer colorFrom = getResources().getColor(R.color.accent);
            Integer colorTo = getResources().getColor(R.color.red);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(animator -> mToggleSettingsPanelButton.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue())));
            colorAnimation.setDuration(FAB_COLOR_ANIMATION_DURATION);

            ValueAnimator reverseColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorFrom);
            reverseColorAnimation.addUpdateListener(animator -> mToggleSettingsPanelButton.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue())));
            reverseColorAnimation.setDuration(FAB_COLOR_ANIMATION_DURATION);

            mFabColorAnimator = new AnimatorSet();
            mFabColorAnimator.play(colorAnimation);
            mFabColorAnimator.play(reverseColorAnimation).after(2000);
            mFabColorAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    // Reset the FAB to the default color
                    mToggleSettingsPanelButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent)));
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mFabColorAnimator = null;
                }
            });
            mFabColorAnimator.start();
        };
        runAfterSettingsPanelIsClosed(runnable);

        mSaveInProgress = false;
    }

    @Override
    public void onBackPressed() {
        if (mIsSettingsPanelOpen) {
            closeSettingsPanel();
            return;
        }
        super.onBackPressed();
    }

    public void onSettingsLoaded() {
        // Re-enable the submit button
        mIsSaveEnalbed = true;
    }

    private void showSnackbar(@StringRes int messageResId) {
        showSnackbar(getString(messageResId));
    }

    @SuppressWarnings("WrongConstant")
    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, 2000);
        TextView text = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        if (text != null) {
            text.setTextColor(getResources().getColor(R.color.white));
        }
        snackbar.show();
    }

    /**
     * Used to defer an operation until after the settings panel has finished animating closed.
     * <p>
     * If the panel has already finished animating when this is called, the Runnable will be run
     * immediately.
     */
    private void runAfterSettingsPanelIsClosed(Runnable runnable) {
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
