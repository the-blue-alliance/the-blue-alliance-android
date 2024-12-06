package com.thebluealliance.androidclient.activities.settings;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.UpdateUserModelSettingsWorker;
import com.thebluealliance.androidclient.activities.BaseActivity;
import com.thebluealliance.androidclient.fragments.mytba.MyTBASettingsFragment;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.interfaces.LoadModelSettingsCallback;
import com.thebluealliance.androidclient.interfaces.ModelSettingsCallbacks;
import com.thebluealliance.androidclient.types.ModelType;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyTBAModelSettingsActivity extends BaseActivity implements View.OnClickListener, ModelSettingsCallbacks, LoadModelSettingsCallback {

    private static final String EXTRA_MODEL_KEY = "model_key";
    private static final String EXTRA_MODEL_TYPE = "model_type";

    Toolbar toolbar;

    private Bundle savedPreferenceState;
    private FloatingActionButton saveModelPreferencesFab;

    private MyTBASettingsFragment settings;

    private final Handler handler = new Handler();

    private TransitionDrawable fabDrawable;

    public static Intent newInstance(Context context, String modelKey, ModelType modelType) {
        Intent intent = new Intent(context, MyTBAModelSettingsActivity.class);
        intent.putExtra(EXTRA_MODEL_KEY, modelKey);
        intent.putExtra(EXTRA_MODEL_TYPE, modelType.getEnum());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytba_model_settings);

        setSearchEnabled(false);

        String modelKey = getIntent().getExtras().getString(EXTRA_MODEL_KEY);
        int modelType = getIntent().getExtras().getInt(EXTRA_MODEL_TYPE, -1);
        ModelType modelType1;
        String modelKey1;
        if (modelKey != null && modelType != -1) {
            modelKey1 = modelKey;
            modelType1 = ModelHelper.getModelFromEnum(modelType);
        } else {
            throw new IllegalArgumentException("MyTBAModelSettingsActivity must be created with a model key!");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(modelType1.getSingularTitle() + " Settings");

        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNotificationSettingsCloseButtonClick();
            }
        });
        toolbar.setNavigationContentDescription(R.string.close);

        getWindow().setStatusBarColor(getResources().getColor(R.color.accent_dark));

        saveModelPreferencesFab = (FloatingActionButton) findViewById(R.id.close_settings_button);
        saveModelPreferencesFab.setOnClickListener(this);

        if (savedInstanceState != null) {
            savedPreferenceState = savedInstanceState.getBundle(MyTBASettingsFragment.SAVED_STATE_BUNDLE);
        }

        // Create the settings fragment
        saveModelPreferencesFab.setEnabled(false);
        settings = MyTBASettingsFragment.newInstance(modelKey1, modelType1, savedPreferenceState);
        getSupportFragmentManager().beginTransaction().replace(R.id.settings_list, settings).commit();

        // Create drawable for the FAB
        Resources res = getResources();
        Drawable[] backgrounds = new Drawable[]{res.getDrawable(R.drawable.ic_check_white_24dp), res.getDrawable(R.drawable.ic_error_white_24dp)};
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
            outState.putBundle(MyTBASettingsFragment.SAVED_STATE_BUNDLE, b);
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
    public boolean shouldShowWarningMessages() {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close_settings_button) {
            UpdateUserModelSettingsWorker.runWithCallbacks(this, settings.getSettings(), this);
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