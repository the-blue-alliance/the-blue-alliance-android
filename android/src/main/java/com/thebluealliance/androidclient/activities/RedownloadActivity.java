package com.thebluealliance.androidclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.FirstLaunchPagerAdapter;
import com.thebluealliance.androidclient.background.firstlaunch.LoadTBADataWorker;
import com.thebluealliance.androidclient.databinding.ActivityRedownloadBinding;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RedownloadActivity extends AppCompatActivity
  implements LoadTBADataWorker.LoadTBADataCallbacks {

    private static final String CURRENT_LOADING_MESSAGE_KEY = "current_loading_message";
    private static final String LOADING_COMPLETE = "loading_complete";
    private static final String LOAD_TASK_UUID = "load_task_uuid";

    private ActivityRedownloadBinding mBinding;

    private String currentLoadingMessage = "";
    private @Nullable UUID dataLoadTask = null;
    private boolean isDataFinishedLoading = false;
    private short[] mDataToLoad;

    @Inject SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.configureActivityForEdgeToEdge(this);
        mBinding = ActivityRedownloadBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Extract relevant data from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(LoadTBADataWorker.DATA_TO_LOAD)) {
            mDataToLoad = extras.getShortArray(LoadTBADataWorker.DATA_TO_LOAD);
        }

        mBinding.viewPager.setSwipeEnabled(false);
        mBinding.viewPager.setOffscreenPageLimit(10);
        mBinding.viewPager.setAdapter(new FirstLaunchPagerAdapter(this));

        mBinding.welcomeNextPage.setOnClickListener((View view) -> beginLoadingIfConnected());
        mBinding.continueToEnd.setOnClickListener((View view) -> mBinding.viewPager.setCurrentItem((2)));
        mBinding.finish.setOnClickListener((View view) -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        // Setup the changelog
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.changelog)));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.getProperty("line.separator"));
                    line = br.readLine();
                }
                String everything = sb.toString();
                mBinding.changelog.setText(Html.fromHtml(everything));
            } finally {
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mBinding.changelog.setText("Error reading changelog file.");
        }

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
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_LOADING_MESSAGE_KEY, currentLoadingMessage);
        outState.putBoolean(LOADING_COMPLETE, isDataFinishedLoading);
        if (dataLoadTask != null) {
            outState.putString(LOAD_TASK_UUID, dataLoadTask.toString());
        }
    }

    private void beginLoadingIfConnected() {
        if (ConnectionDetector.isConnectedToInternet(this)) {
            mBinding.viewPager.setCurrentItem(1);
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
        int[] intData = new int[mDataToLoad.length];
        for (int i = 0; i < mDataToLoad.length; i++) {
            intData[i] = mDataToLoad[i];
        }
        dataLoadTask = LoadTBADataWorker.runWithCallbacks(this, intData, this);
    }

    private void onError(final String stacktrace) {
        mPreferences.edit()
                .putBoolean(Constants.ALL_DATA_LOADED_KEY, false)
                .apply();

        // Return to the first page
        mBinding.viewPager.setCurrentItem(0);

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
            TbaLogger.e("Error loading data: " + stacktrace);
            e.printStackTrace();
        }
    }

    private void onLoadFinished() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(Constants.ALL_DATA_LOADED_KEY, true);
        editor.putInt(Constants.APP_VERSION_KEY, BuildConfig.VERSION_CODE).apply();

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
                    mBinding.loadingMessage.setVisibility(View.GONE);
                    mBinding.loadingProgressBar.setVisibility(View.GONE);
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

        // Cancel task
        LoadTBADataWorker.cancel(this);

        // Show a warning
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(R.string.connection_lost_title);

        alertDialogBuilder.setMessage(getString(R.string.connection_lost)).setCancelable(false)
                .setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                    dialog.dismiss();
                });

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
}
