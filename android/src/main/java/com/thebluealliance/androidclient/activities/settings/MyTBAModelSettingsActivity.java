package com.thebluealliance.androidclient.activities.settings;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.BaseActivity;
import com.thebluealliance.androidclient.fragments.mytba.NotificationSettingsFragment;
import com.thebluealliance.androidclient.fragments.tasks.UpdateUserModelSettingsTaskFragment;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.interfaces.LoadModelSettingsCallback;
import com.thebluealliance.androidclient.interfaces.ModelSettingsCallbacks;

/**
 * Created by Nathan on 12/22/2014.
 */
public class MyTBAModelSettingsActivity extends BaseActivity implements View.OnClickListener, ModelSettingsCallbacks, LoadModelSettingsCallback {

    private static final String SAVE_SETTINGS_TASK_FRAGMENT_TAG = "task_fragment_tag";

    private static final String EXTRA_MODEL_KEY = "model_key";
    private static final String EXTRA_MODEL_TYPE = "model_type";

    Toolbar toolbar;

    private Bundle savedPreferenceState;
    private FloatingActionButton saveModelPreferencesFab;

    private String modelKey;
    private ModelHelper.MODELS modelType;

    private NotificationSettingsFragment settings;
    private UpdateUserModelSettingsTaskFragment saveSettingsTaskFragment;

    private Handler handler = new Handler();

    private TransitionDrawable fabDrawable;

    private View settingsListContainer;
    private View greenContainer;

    public static Intent newInstance(Context context, String modelKey, ModelHelper.MODELS modelType) {
        Intent intent = new Intent(context, MyTBAModelSettingsActivity.class);
        intent.putExtra(EXTRA_MODEL_KEY, modelKey);
        intent.putExtra(EXTRA_MODEL_TYPE, modelType.getEnum());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mytba_model_settings);

        settingsListContainer = findViewById(R.id.settings_list);
        greenContainer = findViewById(R.id.green_container);

        setSearchEnabled(false);

        String modelKey = getIntent().getExtras().getString(EXTRA_MODEL_KEY);
        int modelType = getIntent().getExtras().getInt(EXTRA_MODEL_TYPE, -1);
        if (modelKey != null && modelType != -1) {
            this.modelKey = modelKey;
            this.modelType = ModelHelper.getModelFromEnum(modelType);
        } else {
            throw new IllegalArgumentException("MyTBAModelSettingsActivity must be created with a model key!");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(this.modelType.getSingularTitle() + " Settings");

        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNotificationSettingsCloseButtonClick();
            }
        });
        toolbar.setNavigationContentDescription(R.string.close);

        if (Utilities.hasLApis()) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.accent_dark));
        }

        saveModelPreferencesFab = (FloatingActionButton) findViewById(R.id.close_notification_settings_button);
        saveModelPreferencesFab.setOnClickListener(this);

        if (savedInstanceState != null) {
            savedPreferenceState = savedInstanceState.getBundle(NotificationSettingsFragment.SAVED_STATE_BUNDLE);
        }

        saveSettingsTaskFragment = (UpdateUserModelSettingsTaskFragment) getSupportFragmentManager().findFragmentByTag(SAVE_SETTINGS_TASK_FRAGMENT_TAG);

        // Create the settings fragment
        saveModelPreferencesFab.setEnabled(false);
        settings = NotificationSettingsFragment.newInstance(this.modelKey, this.modelType, savedPreferenceState);
        getFragmentManager().beginTransaction().replace(R.id.settings_list, settings).commit();

        // Create drawable for the FAB
        Resources res = getResources();
        Drawable backgrounds[] = new Drawable[]{res.getDrawable(R.drawable.ic_check_white_24dp), res.getDrawable(R.drawable.ic_error_white_24dp)};
        fabDrawable = new TransitionDrawable(backgrounds);
        fabDrawable.setCrossFadeEnabled(true);
        saveModelPreferencesFab.setImageDrawable(fabDrawable);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Only save the preference state if they've already been successfully loaded
        // Also, only save them if the settings panel is open. Otherwise, clear them on rotate
        if (settings != null && settings.arePreferencesLoaded()) {
            Bundle b = new Bundle();
            settings.writeStateToBundle(b);
            outState.putBundle(NotificationSettingsFragment.SAVED_STATE_BUNDLE, b);
        }
    }

    private void onNotificationSettingsCloseButtonClick() {
        // Don't save anything, the user doesn't want to
        this.finish();
    }

    @Override
    public void onSettingsLoaded() {
        // Enable the submit button again
        saveModelPreferencesFab.setEnabled(true);
    }

    @Override
    public void showWarningMessage(String message) {
        // Nope.
    }

    @Override
    public void hideWarningMessage() {
        // Also nope.
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close_notification_settings_button) {
            // Save all the things!
            if (saveSettingsTaskFragment == null) {
                saveSettingsTaskFragment = new UpdateUserModelSettingsTaskFragment(settings.getSettings());
                getSupportFragmentManager().beginTransaction().add(saveSettingsTaskFragment, SAVE_SETTINGS_TASK_FRAGMENT_TAG).commit();

                /**
                 * Maybe use these animations in a future release, but they aren't ready for prime time yet.
                 */
                /*
                Path curvedPath = new Path();
                float buttonStartX = fabContainer.getLeft();
                float buttonStartY = fabContainer.getTop();
                curvedPath.moveTo(buttonStartX, buttonStartY);

                float ctrl1x = (float) ((settingsListContainer.getX() + settingsListContainer.getWidth() / 2) + settingsListContainer.getWidth()/2 * 0.24);
                float ctrl1y = (float) ((settingsListContainer.getY() + settingsListContainer.getHeight() / 2) + settingsListContainer.getHeight()/2 * 0.6);
                float ctrl2x = (float) ((settingsListContainer.getX() + settingsListContainer.getWidth() / 2));
                float ctrl2y = (float) ((settingsListContainer.getY() + settingsListContainer.getHeight() / 2) + settingsListContainer.getHeight()/2 * 0.3);
                float endPosX = (settingsListContainer.getX() + settingsListContainer.getWidth() / 2 - fabContainer.getWidth() / 2);
                float endPosY = (settingsListContainer.getY() + settingsListContainer.getHeight() / 2 - fabContainer.getHeight() / 2);
                curvedPath.cubicTo(ctrl1x, ctrl1y, ctrl2x, ctrl2y, endPosX, endPosY);
                ObjectAnimator fabPathAnimator = ObjectAnimator.ofFloat(fabContainer, View.X, View.Y, curvedPath);
                fabPathAnimator.setInterpolator(new AccelerateInterpolator());
                fabPathAnimator.setDuration(300);

                int centerOfButtonOutsideX = (settingsListContainer.getLeft() + settingsListContainer.getRight()) / 2;
                int centerOfButtonOutsideY = (settingsListContainer.getTop() + settingsListContainer.getBottom()) / 2;

                float finalRadius = (float) Math.sqrt(Math.pow(centerOfButtonOutsideX - settingsListContainer.getLeft(), 2) + Math.pow(centerOfButtonOutsideY - settingsListContainer.getTop(), 2));
                Animator circularReveal = ViewAnimationUtils.createCircularReveal(greenContainer, centerOfButtonOutsideX, centerOfButtonOutsideY, 0, finalRadius);
                circularReveal.setInterpolator(new AccelerateInterpolator());
                circularReveal.setDuration(300);
                circularReveal.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        greenContainer.setVisibility(View.VISIBLE);
                    }
                });

                AnimatorSet set = new AnimatorSet();
                set.play(fabPathAnimator);
                set.play(circularReveal).after(250);
                set.start(); */

            }
        }
    }

    @Override
    public void onSuccess() {
        // Save successful, end the activity
        saveModelPreferencesFab.setEnabled(false);

        Integer colorFrom = getResources().getColor(R.color.accent_dark);
        Integer colorTo = getResources().getColor(R.color.green);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(animator -> saveModelPreferencesFab.setBackgroundTintList(ColorStateList.valueOf((Integer)animator.getAnimatedValue())));
        colorAnimation.setDuration(500);
        colorAnimation.start();

        // Close the activity in the future
        handler.postDelayed(() -> MyTBAModelSettingsActivity.this.finish(), 1000);
    }

    @Override
    public void onNoOp() {
        Toast.makeText(this, "No changes made.", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error.", Toast.LENGTH_SHORT).show();

        saveModelPreferencesFab.setEnabled(false);

        Integer colorFrom = getResources().getColor(R.color.accent_dark);
        Integer colorTo = getResources().getColor(R.color.red);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(animator -> {
            fabDrawable.startTransition(500);
            saveModelPreferencesFab.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue()));
        });
        colorAnimation.setDuration(500);

        Integer reverseColorFrom = getResources().getColor(R.color.red);
        Integer reverseColorTo = getResources().getColor(R.color.accent_dark);
        ValueAnimator reverseColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), reverseColorFrom, reverseColorTo);
        reverseColorAnimation.addUpdateListener(animator -> {
            //saveModelPreferencesFab.setColorNormal((Integer) animator.getAnimatedValue());
        });
        reverseColorAnimation.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(colorAnimation);
        animatorSet.play(reverseColorAnimation).after(2500);
        animatorSet.start();

        // Close the activity in the future
        handler.postDelayed(() -> MyTBAModelSettingsActivity.this.finish(), 3000);
    }
}