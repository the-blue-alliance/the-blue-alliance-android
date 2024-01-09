package com.thebluealliance.androidclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.adapters.FirstLaunchPagerAdapter;
import com.thebluealliance.androidclient.auth.AuthProvider;
import com.thebluealliance.androidclient.background.firstlaunch.LoadTBADataWorker;
import com.thebluealliance.androidclient.databinding.ActivityOnboardingBinding;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.views.MyTBAOnboardingViewPager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OnboardingActivity extends AppCompatActivity
  implements LoadTBADataWorker.LoadTBADataCallbacks,
  MyTBAOnboardingViewPager.Callbacks {

    private static final String CURRENT_LOADING_MESSAGE_KEY = "current_loading_message";
    private static final String LOADING_COMPLETE = "loading_complete";
    private static final String MYTBA_LOGIN_COMPLETE = "mytba_login_complete";
    private static final String WELCOME_PAGER_STATE = "welcome_pager_state";
    private static final String MYTBA_PAGER_STATE = "mytba_pager_state";
    private static final String LOAD_TASK_UUID = "load_task_uuid";
    private static final int SIGNIN_CODE = 254;

    private ActivityOnboardingBinding mBinding;

    private String currentLoadingMessage = "";
    private boolean isDataFinishedLoading = false;
    private boolean isMyTBALoginComplete = false;
    private @Nullable UUID dataLoadTask = null;

    @Inject @Named("firebase_auth") AuthProvider mAuthProvider;
    @Inject AccountController mAccountController;
    @Inject SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.viewPager.setSwipeEnabled(false);
        mBinding.viewPager.setOffscreenPageLimit(10);
        mBinding.viewPager.setAdapter(new FirstLaunchPagerAdapter(this));

        mBinding.mytbaViewPager.setCallbacks(this);

        // If the activity is being recreated after a config change, restore the message that was
        // being shown when the last activity was destroyed
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CURRENT_LOADING_MESSAGE_KEY)) {
                currentLoadingMessage = savedInstanceState.getString(CURRENT_LOADING_MESSAGE_KEY);
                mBinding.loadingMessage.setText(currentLoadingMessage);
            }

            if (savedInstanceState.containsKey(LOADING_COMPLETE)) {
                isDataFinishedLoading = savedInstanceState.getBoolean(LOADING_COMPLETE);
            }

            if (savedInstanceState.containsKey(MYTBA_LOGIN_COMPLETE)) {
                isMyTBALoginComplete = savedInstanceState.getBoolean(MYTBA_LOGIN_COMPLETE);
            }

            if (savedInstanceState.containsKey(WELCOME_PAGER_STATE)) {
                mBinding.viewPager.onRestoreInstanceState(savedInstanceState.getParcelable(WELCOME_PAGER_STATE));
            }

            if (savedInstanceState.containsKey(MYTBA_PAGER_STATE)) {
                mBinding.mytbaViewPager.getViewPager().onRestoreInstanceState(savedInstanceState.getParcelable(MYTBA_PAGER_STATE));
            }

            if (savedInstanceState.containsKey(LOAD_TASK_UUID)) {
                dataLoadTask = UUID.fromString(savedInstanceState.getString(LOAD_TASK_UUID));
                LoadTBADataWorker.subscribeToJob(this, dataLoadTask, this);
            }
        }

        /*
        If the data has already finished loading and we're being restored after an orientation
        change, hide the loading indicators and show the continue button
        */

        if (isDataFinishedLoading) {
            mBinding.loadingProgressBar.setVisibility(View.GONE);
            mBinding.loadingMessage.setVisibility(View.GONE);
            mBinding.continueToEnd.setVisibility(View.VISIBLE);
            mBinding.continueToEnd.setAlpha(1.0f);
        }

        if (isMyTBALoginComplete) {
            mBinding.mytbaViewPager.setUpForLoginSuccess();
        } else {
            mBinding.mytbaViewPager.setUpForLoginPrompt();
        }

        mBinding.continueToEnd.setOnClickListener(this::onContinueToEndClient);
        mBinding.welcomeNextPage.setOnClickListener((View view) -> beginLoadingIfConnected());
        mBinding.finish.setOnClickListener((View view) -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_LOADING_MESSAGE_KEY, currentLoadingMessage);
        outState.putBoolean(LOADING_COMPLETE, isDataFinishedLoading);
        outState.putBoolean(MYTBA_LOGIN_COMPLETE, isMyTBALoginComplete);
        outState.putParcelable(WELCOME_PAGER_STATE, mBinding.viewPager.onSaveInstanceState());
        outState.putParcelable(MYTBA_PAGER_STATE, mBinding.mytbaViewPager.getViewPager().onSaveInstanceState());
        if (dataLoadTask != null) {
            outState.putString(LOAD_TASK_UUID, dataLoadTask.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGNIN_CODE) {
            if (resultCode == RESULT_OK) {
                mAuthProvider.userFromSignInResult(requestCode, resultCode, data)
                        .subscribe(user -> {
                            TbaLogger.d("User logged in: " + user.getEmail());
                            mBinding.mytbaViewPager.setUpForLoginSuccess();
                            isMyTBALoginComplete = true;
                            mAccountController.onAccountConnect(OnboardingActivity.this, user);
                        }, throwable -> {
                            TbaLogger.e("Error logging in");
                            throwable.printStackTrace();
                            mAccountController.setMyTbaEnabled(false);
                        });
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_LONG).show();
                mBinding.mytbaViewPager.setUpForLoginPrompt();
            }
        }
    }

    private void onContinueToEndClient(View view) {
        // If myTBA hasn't been activated yet, prompt the user one last time to sign in
        if (!mBinding.mytbaViewPager.isOnLoginPage()) {
            mBinding.mytbaViewPager.scrollToLoginPage();
        } else if (!isMyTBALoginComplete) {
            // Only show this dialog if play services are actually available
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.mytba_prompt_title))
                    .setMessage(getString(R.string.mytba_prompt_message))
                    .setCancelable(false)
                    .setPositiveButton(R.string.mytba_prompt_yes, (dialog, dialogId) -> {
                        // Do nothing; allow user to enable myTBA
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.mytba_prompt_cancel, (dialog, dialogId) -> {
                        // Scroll to the last page
                        mBinding.viewPager.setCurrentItem(2);
                        dialog.dismiss();
                    }).create().show();
        } else {
            mBinding.viewPager.setCurrentItem(2);
        }
    }

    private void beginLoadingIfConnected() {
        if (ConnectionDetector.isConnectedToInternet(this)) {
            mBinding.viewPager.setCurrentItem(1);
            beginLoading();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle(R.string.check_connection_title);

            alertDialogBuilder
                    .setMessage(getString(R.string.warning_no_internet_connection))
                    .setCancelable(false)
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
        dataLoadTask = LoadTBADataWorker.runWithCallbacks(this, new int[0] /* load everything */, this);
    }

    private void onError(final String stacktrace) {
        mPreferences.edit()
                .putBoolean(Constants.ALL_DATA_LOADED_KEY, false)
                .apply();

        // Return to the first page
        mBinding.viewPager.setCurrentItem(0);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(getString(R.string.fatal_error));

        alertDialogBuilder
                .setMessage(getString(R.string.fatal_error_message))
                .setCancelable(false)
                .setPositiveButton(R.string.contact_developer, (dialog, id) -> {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                                                    Uri.fromParts("mailto",
                                                                  "contact@thebluealliance.com",
                                                                  null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FATAL ERROR");
                    emailIntent.putExtra(Intent.EXTRA_TEXT,
                                         "Version: " + BuildConfig.VERSION_NAME
                                         + "\nStacktrace:\n" + stacktrace);
                    startActivity(Intent.createChooser(emailIntent,
                                                       getString(R.string.send_email)));
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    finish();
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        try {
            alertDialog.show();
        } catch (WindowManager.BadTokenException e) {
            // Activity is already gone. Just log the exception
            TbaLogger.e("Error loading data: " + stacktrace);
            e.printStackTrace();
        }
    }

    private void onLoadFinished() {
        mPreferences.edit()
                .putBoolean(Constants.ALL_DATA_LOADED_KEY, true)
                .apply();

        isDataFinishedLoading = true;

        mBinding.loadingMessage.setText("Loading complete");

        // After two seconds, fade out the message and spinner and fade in the "continue" button
        mBinding.loadingMessage.postDelayed(() -> {
            ValueAnimator fadeOutAnimation = ValueAnimator.ofFloat(1.0f, 0.0f);
            fadeOutAnimation.addUpdateListener(animation -> {
                mBinding.loadingMessage.setAlpha((float) animation.getAnimatedValue());
                mBinding.loadingProgressBar.setAlpha((float) animation.getAnimatedValue());
            });
            fadeOutAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBinding.loadingProgressBar.setVisibility(View.GONE);
                    mBinding.loadingMessage.setVisibility(View.GONE);
                }
            });
            fadeOutAnimation.setDuration(250);

            ValueAnimator fadeInAnimation = ValueAnimator.ofFloat(0.0f, 1.0f);
            fadeInAnimation.addUpdateListener(animation -> {
                mBinding.continueToEnd.setAlpha((float) animation.getAnimatedValue());
            });
            fadeInAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mBinding.continueToEnd.setAlpha(0.0f);
                    mBinding.continueToEnd.setVisibility(View.VISIBLE);
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
        mBinding.viewPager.setCurrentItem(0);

        LoadTBADataWorker.cancel(this);

        // Show a warning
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(R.string.connection_lost_title);

        alertDialogBuilder.setMessage(getString(R.string.connection_lost)).setCancelable(false)
                .setPositiveButton(getString(R.string.ok), (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();

        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    private void onLoadingMessageUpdated(String message) {
        if (message == null) {
            return;
        }
        currentLoadingMessage = message;
        mBinding.loadingMessage.setText(message);
    }

    public void onProgressUpdate(LoadTBADataWorker.LoadProgressInfo info) {
        switch (info.state) {
            case LoadTBADataWorker.LoadProgressInfo.STATE_LOADING:
                onLoadingMessageUpdated(info.message);
                break;
            case LoadTBADataWorker.LoadProgressInfo.STATE_FINISHED:
                onLoadFinished();
                break;
            case LoadTBADataWorker.LoadProgressInfo.STATE_NO_CONNECTION:
                onConnectionLost();
                break;
            case LoadTBADataWorker.LoadProgressInfo.STATE_ERROR:
                onError(info.message);
                break;
        }
    }

    @Override
    public void onSignInButtonClicked() {
        Intent signInIntent = mAuthProvider.buildSignInIntent();
        if (signInIntent != null) {
            startActivityForResult(signInIntent, SIGNIN_CODE);
        } else {
            Toast.makeText(this, R.string.mytba_no_signin_intent, Toast.LENGTH_SHORT).show();
            TbaLogger.e("Unable to get login Intent");
        }
    }
}
