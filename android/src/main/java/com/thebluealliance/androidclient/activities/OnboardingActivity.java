package com.thebluealliance.androidclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.PlusManager;
import com.thebluealliance.androidclient.accounts.PlusManagerCallbacks;
import com.thebluealliance.androidclient.adapters.FirstLaunchPagerAdapter;
import com.thebluealliance.androidclient.background.LoadTBADataTaskFragment;
import com.thebluealliance.androidclient.background.firstlaunch.LoadTBAData;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.views.DisableSwipeViewPager;
import com.thebluealliance.androidclient.views.MyTBAOnboardingViewPager;

/**
 * Created by Nathan on 8/13/2015.
 */
public class OnboardingActivity extends AppCompatActivity implements View.OnClickListener, LoadTBAData.LoadTBADataCallbacks, PlusManagerCallbacks, MyTBAOnboardingViewPager.MyTBAOnboardingFragmentCallbacks {

    private static final String CURRENT_LOADING_MESSAGE_KEY = "current_loading_message";
    private static final String LOADING_COMPLETE = "loading_complete";
    private static final String MYTBA_LOGIN_COMPLETE = "mytba_login_complete";
    private static final String LOAD_FRAGMENT_TAG = "loadFragment";

    private DisableSwipeViewPager viewPager;
    private MyTBAOnboardingViewPager mMyTBAOnboardingViewPager;
    private TextView loadingMessage;
    private ProgressBar loadingProgressBar;
    private View continueToEndButton;

    private String currentLoadingMessage = "";

    private LoadTBADataTaskFragment loadFragment;

    private boolean isDataFinishedLoading = false;
    private boolean isMyTBALoginComplete = false;

    private PlusManager mPlusManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPlusManager = new PlusManager(this, this);

        setContentView(R.layout.activity_onboarding);

        viewPager = (DisableSwipeViewPager) findViewById(R.id.view_pager);
        viewPager.setSwipeEnabled(false);
        viewPager.setOffscreenPageLimit(10);
        viewPager.setAdapter(new FirstLaunchPagerAdapter(this));

        mMyTBAOnboardingViewPager = (MyTBAOnboardingViewPager) findViewById(R.id.mytba_view_pager);
        mMyTBAOnboardingViewPager.setCallbacks(this);

        loadingMessage = (TextView) findViewById(R.id.loading_message);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loading_progress_bar);
        continueToEndButton = findViewById(R.id.continue_to_end);
        continueToEndButton.setOnClickListener(this);

        // If the activity is being recreated after a config change, restore the message that was
        // being shown when the last activity was destroyed
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CURRENT_LOADING_MESSAGE_KEY)) {
                currentLoadingMessage = savedInstanceState.getString(CURRENT_LOADING_MESSAGE_KEY);
                loadingMessage.setText(currentLoadingMessage);
            }

            if (savedInstanceState.containsKey(LOADING_COMPLETE)) {
                isDataFinishedLoading = savedInstanceState.getBoolean(LOADING_COMPLETE);
            }

            if (savedInstanceState.containsKey(MYTBA_LOGIN_COMPLETE)) {
                isMyTBALoginComplete = savedInstanceState.getBoolean(MYTBA_LOGIN_COMPLETE);
            }
        }

        findViewById(R.id.welcome_next_page).setOnClickListener(this);
        findViewById(R.id.finish).setOnClickListener(this);

        loadFragment = (LoadTBADataTaskFragment) getSupportFragmentManager().findFragmentByTag(LOAD_FRAGMENT_TAG);

        if (loadFragment != null) {
            viewPager.setCurrentItem(1, false);

            LoadTBAData.LoadProgressInfo info = loadFragment.getLastProgressUpdate();
            if (info != null) {
                if (!loadFragment.wasLastUpdateDelivered() && info.state != LoadTBAData.LoadProgressInfo.STATE_FINISHED) {
                    onProgressUpdate(info);
                }
            }
        }

        /*
        If the data has already finished loading and we're being restored after an orientation
        change, hide the loading indicators and show the continue button
        */

        if (isDataFinishedLoading) {
            loadingProgressBar.setVisibility(View.GONE);
            loadingMessage.setVisibility(View.GONE);
            continueToEndButton.setVisibility(View.VISIBLE);
            continueToEndButton.setAlpha(1.0f);
        }

        if (isMyTBALoginComplete) {
            mMyTBAOnboardingViewPager.setUpForLoginSuccess();
        } else if (!supportsGooglePlayServices()) {
            mMyTBAOnboardingViewPager.setUpForNoPlayServices();
        } else {
            mMyTBAOnboardingViewPager.setUpForLoginPrompt();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPlusManager.onActivityResult(requestCode, resultCode);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_LOADING_MESSAGE_KEY, currentLoadingMessage);
        outState.putBoolean(LOADING_COMPLETE, isDataFinishedLoading);
        outState.putBoolean(MYTBA_LOGIN_COMPLETE, isMyTBALoginComplete);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.welcome_next_page:
                beginLoadingIfConnected();
                break;
            case R.id.continue_to_end:
                // If myTBA hasn't been activated yet, prompt the user one last time to sign in
                if (!mMyTBAOnboardingViewPager.isOnLoginPage()) {
                    mMyTBAOnboardingViewPager.scrollToLoginPage();
                } else if (!isMyTBALoginComplete) {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.mytba_prompt_title))
                            .setMessage(getString(R.string.mytba_prompt_message))
                            .setCancelable(false)
                            .setPositiveButton(R.string.mytba_prompt_yes, (dialog, dialogId) -> {
                                // Scroll to the last page
                                viewPager.setCurrentItem(2);
                                dialog.dismiss();
                            })
                            .setNegativeButton(R.string.mytba_prompt_cancel, (dialog, dialogId) -> {
                                // Do nothing; allow user to enable myTBA
                                dialog.dismiss();
                            }).create().show();
                } else if (isMyTBALoginComplete) {
                    viewPager.setCurrentItem(2);
                }
                break;
            case R.id.finish:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
        }
    }

    private void beginLoadingIfConnected() {
        if (ConnectionDetector.isConnectedToInternet(this)) {
            viewPager.setCurrentItem(1);
            beginLoading();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle(R.string.check_connection_title);

            alertDialogBuilder.setMessage(getString(R.string.warning_no_internet_connection)).setCancelable(false)
                    .setPositiveButton(getString(R.string.retry), (dialog, id) -> {
                        beginLoadingIfConnected();
                        dialog.dismiss();
                    }).setNegativeButton(getString(R.string.cancel), (dialog, id) -> {
                finish();
            });

            // Create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.show();
        }
    }

    private void beginLoading() {
        Fragment f = new LoadTBADataTaskFragment();
        f.setRetainInstance(true);
        getSupportFragmentManager().beginTransaction().add(f, LOAD_FRAGMENT_TAG).commit();
    }

    private void onError(final String stacktrace) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(Constants.ALL_DATA_LOADED_KEY, false).commit();

        // Return to the first page
        viewPager.setCurrentItem(0);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(getString(R.string.fatal_error));

        alertDialogBuilder.setMessage(getString(R.string.fatal_error_message)).setCancelable(false).setPositiveButton(R.string.contact_developer, (dialog, id) -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "contact@thebluealliance.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FATAL ERROR");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Version: " + BuildConfig.VERSION_NAME + "\nStacktrace:\n" + stacktrace);
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
            finish();
        }).setNegativeButton(R.string.cancel, (dialog, id) -> {
            finish();
        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        try {
            alertDialog.show();
        } catch (WindowManager.BadTokenException e) {
            // Activity is already gone. Just log the exception
            Log.e(Constants.LOG_TAG, "Error loading data: " + stacktrace);
            e.printStackTrace();
        }
    }

    private void onLoadFinished() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(Constants.ALL_DATA_LOADED_KEY, true).commit();

        isDataFinishedLoading = true;

        loadingMessage.setText("Loading complete");

        // After two seconds, fade out the message and spinner and fade in the "continue" button
        loadingMessage.postDelayed(() -> {
            ValueAnimator fadeOutAnimation = ValueAnimator.ofFloat(1.0f, 0.0f);
            fadeOutAnimation.addUpdateListener(animation -> {
                loadingMessage.setAlpha((float) animation.getAnimatedValue());
                loadingProgressBar.setAlpha((float) animation.getAnimatedValue());
            });
            fadeOutAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loadingMessage.setVisibility(View.GONE);
                    loadingProgressBar.setVisibility(View.GONE);
                }
            });
            fadeOutAnimation.setDuration(250);

            ValueAnimator fadeInAnimation = ValueAnimator.ofFloat(0.0f, 1.0f);
            fadeInAnimation.addUpdateListener(animation -> {
                continueToEndButton.setAlpha((float) animation.getAnimatedValue());
            });
            fadeInAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    continueToEndButton.setAlpha(0.0f);
                    continueToEndButton.setVisibility(View.VISIBLE);
                }
            });
            fadeInAnimation.setDuration(250);

            AnimatorSet animationSet = new AnimatorSet();
            animationSet.play(fadeOutAnimation);
            animationSet.play(fadeInAnimation).after(fadeOutAnimation);
            animationSet.start();
        }, 2000);
    }

    private void onConnectionLost() {
        // Scroll to first page
        viewPager.setCurrentItem(0);

        // Cancel task
        if (loadFragment != null) {
            loadFragment.cancelTask();
        }

        // Show a warning
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(R.string.connection_lost_title);

        alertDialogBuilder.setMessage(getString(R.string.connection_lost)).setCancelable(false)
                .setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void onLoadingMessageUpdated(String message) {
        if (message == null) {
            return;
        }
        currentLoadingMessage = message;
        loadingMessage.setText(message);
    }

    public void onProgressUpdate(LoadTBAData.LoadProgressInfo info) {
        switch (info.state) {
            case LoadTBAData.LoadProgressInfo.STATE_LOADING:
                onLoadingMessageUpdated(info.message);
                break;
            case LoadTBAData.LoadProgressInfo.STATE_FINISHED:
                onLoadFinished();
                break;
            case LoadTBAData.LoadProgressInfo.STATE_NO_CONNECTION:
                onConnectionLost();
                break;
            case LoadTBAData.LoadProgressInfo.STATE_ERROR:
                onError(info.message);
                break;
        }
    }

    @Override
    public void onPlusClientSignIn() {
        mMyTBAOnboardingViewPager.setUpForLoginSuccess();
        isMyTBALoginComplete = true;
    }

    @Override
    public void onPlusClientBlockingUI(boolean show) {

    }

    @Override
    public void updateConnectButtonState() {

    }

    /**
     * Check if the device supports Google Play Services.  It's best practice to check first rather
     * than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
    }

    @Override
    public void onSignInButtonClicked() {
        mPlusManager.signIn();
    }
}
