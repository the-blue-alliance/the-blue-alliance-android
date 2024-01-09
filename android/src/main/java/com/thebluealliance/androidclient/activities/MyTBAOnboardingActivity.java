package com.thebluealliance.androidclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.auth.AuthProvider;
import com.thebluealliance.androidclient.databinding.ActivityMytbaOnboardingBinding;
import com.thebluealliance.androidclient.views.MyTBAOnboardingViewPager;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class MyTBAOnboardingActivity extends AppCompatActivity
        implements MyTBAOnboardingViewPager.Callbacks{

    private static final String MYTBA_LOGIN_COMPLETE = "mytba_login_complete";
    private static final int SIGNIN_CODE = 254;

    private ActivityMytbaOnboardingBinding mBinding;

    private boolean isMyTBALoginComplete = false;

    @Inject @Named("firebase_auth") AuthProvider mAuthProvider;
    @Inject AccountController mAccountController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private void updateContinueButtonText() {
        if (mBinding.mytbaViewPager.isOnLoginPage()) {
            mBinding.continueButtonLabel.setText(R.string.finish_caps);
        } else {
            mBinding.continueButtonLabel.setText(R.string.skip_intro_caps);
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
                            mAccountController.onAccountConnect(MyTBAOnboardingActivity.this, user);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(MYTBA_LOGIN_COMPLETE, isMyTBALoginComplete);
    }

    private void onContinueClick(View view) {
        if (mBinding.mytbaViewPager.isOnLoginPage()) {
            // On the last page, the "continue" button turns into a "finish" button
            finish();
        } else {
            // On other pages, the "continue" button becomes a "skip intro" button
            mBinding.mytbaViewPager.scrollToLoginPage();
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
