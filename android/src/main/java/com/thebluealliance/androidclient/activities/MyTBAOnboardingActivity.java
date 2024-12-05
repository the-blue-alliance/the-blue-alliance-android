package com.thebluealliance.androidclient.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.databinding.ActivityMytbaOnboardingBinding;
import com.thebluealliance.androidclient.mytba.MyTbaOnboardingController;
import com.thebluealliance.androidclient.views.MyTBAOnboardingViewPager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class MyTBAOnboardingActivity extends AppCompatActivity
        implements MyTBAOnboardingViewPager.Callbacks,
        MyTbaOnboardingController.MyTbaOnboardingCallbacks {

    private static final String MYTBA_LOGIN_COMPLETE = "mytba_login_complete";

    private ActivityMytbaOnboardingBinding mBinding;

    private boolean isMyTBALoginComplete = false;

    @Inject
    MyTbaOnboardingController mMyTbaOnboardingController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.configureActivityForEdgeToEdge(this);
        mBinding = ActivityMytbaOnboardingBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.mytbaViewPager.setCallbacks(this);
        mBinding.mytbaViewPager.setTitleText(R.string.what_is_mytba);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MYTBA_LOGIN_COMPLETE)) {
                isMyTBALoginComplete = savedInstanceState.getBoolean(MYTBA_LOGIN_COMPLETE);
            }
        }

        if (isMyTBALoginComplete) {
            mBinding.mytbaViewPager.setUpForLoginSuccess();
        } else {
            mBinding.mytbaViewPager.setUpForLoginPrompt();
        }

        mBinding.mytbaViewPager.getViewPager().addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateContinueButtonText();
            }
        });

        updateContinueButtonText();

        mBinding.cancelButton.setOnClickListener((View view) -> finish());
        mBinding.continueButton.setOnClickListener(this::onContinueClick);

        mMyTbaOnboardingController.registerActivityCallbacks(this, this);
    }

    private void updateContinueButtonText() {
        if (mBinding.mytbaViewPager.isDone()) {
            mBinding.continueButtonLabel.setText(R.string.finish_caps);
        } else if (mBinding.mytbaViewPager.isOnLoginPage()) {
            mBinding.continueButtonLabel.setText(R.string.continue_caps);
        } else {
            mBinding.continueButtonLabel.setText(R.string.skip_intro_caps);
        }
    }

    @Override
    public void onLoginSuccess() {
        mBinding.mytbaViewPager.setUpForLoginSuccess();
        isMyTBALoginComplete = true;

        if (!mBinding.mytbaViewPager.isDone()) {
            mBinding.mytbaViewPager.advance();
        }
    }

    @Override
    public void onLoginFailed() {
        mBinding.mytbaViewPager.setUpForLoginPrompt();
    }

    @Override
    public void onPermissionResult(boolean isGranted) {
        mBinding.mytbaViewPager.setUpForPermissionResult(isGranted);

        if (isGranted) {
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(MYTBA_LOGIN_COMPLETE, isMyTBALoginComplete);
    }

    private void onContinueClick(View view) {
        if (mBinding.mytbaViewPager.isDone()) {
            // On the last page, the "continue" button turns into a "finish" button
            finish();
        } else if (mBinding.mytbaViewPager.isOnLoginPage()) {
            // If there is an additional page after login, go there
            mBinding.mytbaViewPager.advance();
        } else {
            // On other pages, the "continue" button becomes a "skip intro" button
            mBinding.mytbaViewPager.scrollToLoginPage();
        }
    }

    @Override
    public void onSignInButtonClicked() {
        mMyTbaOnboardingController.launchSignIn(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onEnableNotificationsButtonClicked() {
        mMyTbaOnboardingController.launchNotificationPermissionRequest(this);
    }
}
