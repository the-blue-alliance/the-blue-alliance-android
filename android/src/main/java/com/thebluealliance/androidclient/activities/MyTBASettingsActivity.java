package com.thebluealliance.androidclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.accounts.UpdateUserModelSettingsWorker;
import com.thebluealliance.androidclient.fragments.mytba.MyTBASettingsFragment;
import com.thebluealliance.androidclient.interfaces.LoadModelSettingsCallback;
import com.thebluealliance.androidclient.interfaces.ModelSettingsCallbacks;
import com.thebluealliance.androidclient.types.ModelType;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity which hosts a FAB that opens a myTBA model settings panel.
 */
@AndroidEntryPoint
public abstract class MyTBASettingsActivity extends DatafeedActivity implements View.OnClickListener, ModelSettingsCallbacks, LoadModelSettingsCallback {

    private static final String SETTINGS_PANEL_OPEN = "settings_panel_open";

    @ColorRes private static final int FAB_COLOR = R.color.accent;
    @ColorRes private static final int FAB_COLOR_SUCCESS = R.color.green;
    @ColorRes private static final int FAB_COLOR_ERROR = R.color.red;

    FrameLayout mRootView;

    CoordinatorLayout mCoordinatorLayout;
    RelativeLayout mSettingsContainer;
    FloatingActionButton mToggleSettingsPanelButton;
    View mForegroundDim;
    Toolbar mSettingsToolbar;

    private final Handler mFabHandler = new Handler();

    private MyTBASettingsFragment mSettingsFragment;

    private boolean mIsMyTBAEnabled;
    private boolean mIsSettingsPanelOpen = false;
    private boolean mIsSaveEnalbed = false;
    private boolean mSaveInProgress = false;
    private boolean mFabVisible = false;

    // Track animations so we can cancel them if needed
    private AnimatorSet mFabColorAnimator;
    private ValueAnimator mRunningFabAnimation;
    private AnimatorSet mRunningPanelAnimation;

    // In milliseconds
    private static final int ANIMATION_DURATION = 350;
    private static final int FAB_ANIMATION_DURATION = 250;
    private static final int FAB_COLOR_ANIMATION_DURATION = 250;

    private static final float UNDIMMED_ALPHA = 0.0f;
    private static final float DIMMED_ALPHA = 0.7f;

    private Bundle savedPreferenceState;

    @Inject AccountController mAccountController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_mytba_settings);

        mRootView = findViewById(R.id.activity_content);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        mSettingsContainer = (RelativeLayout) findViewById(R.id.settings);
        mToggleSettingsPanelButton = (FloatingActionButton) findViewById(R.id.toggle_settings_button);
        mForegroundDim = findViewById(R.id.activity_foreground_dim);
        mSettingsToolbar = (Toolbar) findViewById(R.id.settings_toolbar);

        mToggleSettingsPanelButton.setOnClickListener(this);

        mSettingsToolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        mSettingsToolbar.setTitle("Team Settings");
        mSettingsToolbar.setNavigationOnClickListener(v -> onSettingsCloseButtonClick());
        mSettingsToolbar.setNavigationContentDescription(R.string.close);
        ViewCompat.setElevation(mSettingsToolbar, getResources().getDimension(R.dimen.toolbar_elevation));

        // We check this so that we can hide the fab and prevent it from being subsequently shown
        // if myTBA is not enabled
        mIsMyTBAEnabled = mAccountController.isMyTbaEnabled();

        mFabVisible = (mToggleSettingsPanelButton.getVisibility() == View.VISIBLE);
        syncFabVisibilityWithMyTbaEnabled(false);

        setupFabIconForSettingsPanelOpen(false);

        if (savedInstanceState != null) {
            mIsSettingsPanelOpen = savedInstanceState.getBoolean(SETTINGS_PANEL_OPEN);
            if (mIsSettingsPanelOpen) {
                openSettingsPanel(false);
            } else {
                closeSettingsPanel(false);
            }
            savedPreferenceState = savedInstanceState.getBundle(MyTBASettingsFragment.SAVED_STATE_BUNDLE);
        }
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
        getSupportFragmentManager().beginTransaction().replace(R.id.settings_list, mSettingsFragment).commit();

        // Disable the submit settings button so we can't hit it before the content is loaded
        // This prevents accidentally wiping settings (see #317)
        mIsSaveEnalbed = false;
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater inflater = getLayoutInflater();
        inflater.inflate(layoutResID, mRootView);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toggle_settings_button) {
            if (onFabClick()) {
                // A subclass handled this click for us, no need to do anything
                return;
            }
            if (!mIsSettingsPanelOpen) {
                if (!mSaveInProgress) {
                    openSettingsPanel(true);
                }
            } else {
                // The user wants to save the preferences
                // Always close the panel
                mFabHandler.postDelayed(() -> closeSettingsPanel(true), 1);

                // If saving is disabled, don't attempt to save
                if (!mIsSaveEnalbed) {
                    return;
                }

                UpdateUserModelSettingsWorker.runWithCallbacks(this, mSettingsFragment.getSettings(), this);
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
        closeSettingsPanel(true);
        // Cancel any changes made by the user
        mSettingsFragment.restoreInitialState();
    }

    protected void openSettingsPanel(boolean animate) {
        mIsSettingsPanelOpen = true;

        if (mSettingsFragment != null) {
            mSettingsFragment.restoreInitialState();
        }

        // Reset the color of the button
        if (mFabColorAnimator != null) {
            mFabColorAnimator.cancel();
        }
        setFabColor(FAB_COLOR);

        if (!animate) {
            // Show the panel
            mSettingsContainer.setVisibility(View.VISIBLE);

            // Set up the status bar
            getDrawerLayout().setStatusBarBackgroundColor(getResources().getColor(R.color
                    .accent_dark));
            getDrawerLayout().invalidate();
            Utilities.setLightStatusBar(getWindow(), true);

            // Dim the activity foreground
            mForegroundDim.setAlpha(DIMMED_ALPHA);

            // Configure the fab
            setupFabIconForSettingsPanelOpen(true);

            return;
        }

        // Hide the button immediately
        mToggleSettingsPanelButton.setVisibility(View.GONE);

        // this is the center of the button in relation to the main view. This provides the center of the clipping circle for the settings view.
        int centerOfButtonOutsideX = (mToggleSettingsPanelButton.getLeft() + mToggleSettingsPanelButton.getRight()) / 2;
        int centerOfButtonOutsideY = (mToggleSettingsPanelButton.getTop() + mToggleSettingsPanelButton.getBottom()) / 2;

        float finalRadius = (float) Math.sqrt(Math.pow(centerOfButtonOutsideX - mSettingsContainer.getLeft(), 2) + Math.pow(centerOfButtonOutsideY - mSettingsContainer.getTop(), 2));

        Animator settingsPanelAnimator;
        mSettingsContainer.setVisibility(View.VISIBLE);
        settingsPanelAnimator = ViewAnimationUtils.createCircularReveal(mSettingsContainer, centerOfButtonOutsideX, centerOfButtonOutsideY, 0, finalRadius);
        settingsPanelAnimator.setDuration(ANIMATION_DURATION);
        settingsPanelAnimator.setInterpolator(new DecelerateInterpolator());

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
    }

    protected void closeSettingsPanel(boolean animate) {
        mIsSettingsPanelOpen = false;

        if (!animate) {
            // Hide the panel
            mSettingsContainer.setVisibility(View.GONE);

            // Set up the status bar
            getDrawerLayout().setStatusBarBackgroundColor(getResources().getColor(R.color
                    .primary_dark));
            getDrawerLayout().invalidate();
            Utilities.setLightStatusBar(getWindow(), false);

            // Undim the activity foreground
            mForegroundDim.setAlpha(UNDIMMED_ALPHA);

            // Configure the fab
            setupFabIconForSettingsPanelOpen(false);

            return;
        }

        int centerOfButtonOutsideX = (mToggleSettingsPanelButton.getLeft() + mToggleSettingsPanelButton.getRight()) / 2;
        int centerOfButtonOutsideY = (mToggleSettingsPanelButton.getTop() + mToggleSettingsPanelButton.getBottom()) / 2;

        float finalRadius = (float) Math.sqrt(Math.pow(centerOfButtonOutsideX - mSettingsContainer.getLeft(), 2) + Math.pow(centerOfButtonOutsideY - mSettingsContainer.getTop(), 2));

        Animator settingsPanelAnimator;
        settingsPanelAnimator = ViewAnimationUtils.createCircularReveal(mSettingsContainer, centerOfButtonOutsideX, centerOfButtonOutsideY, finalRadius, 0);
        settingsPanelAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                mSettingsContainer.setVisibility(View.INVISIBLE);
            }
        });
        settingsPanelAnimator.setDuration(ANIMATION_DURATION);
        settingsPanelAnimator.setInterpolator(new AccelerateInterpolator());

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
    }

    protected void syncFabVisibilityWithMyTbaEnabled(boolean animate) {
        if (mIsMyTBAEnabled) {
            showFab(animate);
        } else {
            hideFab(animate);
        }
        showFab(animate);
    }

    protected void showFab(boolean animate) {
        if (mFabVisible) {
            return;
        }
        mFabVisible = true;
        if (mRunningFabAnimation != null) {
            mRunningFabAnimation.cancel();
        }
        if (!animate) {
            mToggleSettingsPanelButton.setVisibility(View.VISIBLE);
            mToggleSettingsPanelButton.setScaleX(1.0f);
            mToggleSettingsPanelButton.setScaleY(1.0f);
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

    protected void hideFab(boolean animate) {
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

            @Override
            public void onAnimationEnd(Animator animation) {
                mToggleSettingsPanelButton.setVisibility(View.GONE);
            }
        });
        fabScaleDown.addUpdateListener(animation -> {
            ViewCompat.setScaleX(mToggleSettingsPanelButton, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(mToggleSettingsPanelButton, (float) animation.getAnimatedValue());
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

            Integer colorFrom = getResources().getColor(FAB_COLOR);
            Integer colorTo = getResources().getColor(FAB_COLOR_SUCCESS);

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
                    setFabColor(FAB_COLOR);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mFabColorAnimator = null;
                }
            });
            mFabColorAnimator.start();
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

            Integer colorFrom = getResources().getColor(FAB_COLOR);
            Integer colorTo = getResources().getColor(FAB_COLOR_ERROR);
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
                    setFabColor(FAB_COLOR);
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
            closeSettingsPanel(true);
            return;
        }
        super.onBackPressed();
    }

    public void onSettingsLoaded() {
        // Re-enable the submit button
        mIsSaveEnalbed = true;
    }

    protected void showSnackbar(@StringRes int messageResId) {
        showSnackbar(getString(messageResId));
    }

    @SuppressWarnings("WrongConstant")
    protected void showSnackbar(CharSequence message) {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, 2000);
        TextView text = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        if (text != null) {
            text.setTextColor(getResources().getColor(R.color.white));
        }
        snackbar.show();
    }

    protected Snackbar createSnackbar(CharSequence message, @Snackbar.Duration int duration) {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message == null ? "" : message, duration);
        TextView text = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        if (text != null) {
            text.setTextColor(getResources().getColor(R.color.white));
        }
        snackbar.setBackgroundTint(R.color.accent);
        return snackbar;
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

    /**
     * Subclasses should override this if they care about FAB click events. If they handle the
     * event, return true; otherwise, return false so the default behavior (opening and closing
     * the settings panel) will occur.
     *
     * @return true if the click was handled by this method
     */
    protected boolean onFabClick() {
        return false;
    }

    protected void setFabColor(@ColorRes int color) {
        mToggleSettingsPanelButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color)));
    }

    protected void setFabDrawable(@DrawableRes int drawable) {
        mToggleSettingsPanelButton.setImageResource(drawable);
    }

    protected void setupFabForMyTbaSettingsTab() {
        setupFabIconForSettingsPanelOpen(mIsSettingsPanelOpen);
        setFabColor(FAB_COLOR);
    }
}
