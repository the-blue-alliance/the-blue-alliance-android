package com.thebluealliance.androidclient.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaAndroid;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.auth.AuthProvider;
import com.thebluealliance.androidclient.auth.apple.AppleAuthProvider;
import com.thebluealliance.androidclient.di.components.AuthComponent;
import com.thebluealliance.androidclient.di.components.DaggerAuthComponent;
import com.thebluealliance.androidclient.views.MyTBAOnboardingViewPager;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MyTBAOnboardingActivity extends AppCompatActivity
        implements MyTBAOnboardingViewPager.Callbacks{

    private static final String MYTBA_LOGIN_COMPLETE = "mytba_login_complete";
    private static final int SIGNIN_CODE = 254;

    @BindView(R.id.mytba_view_pager)
    MyTBAOnboardingViewPager mMyTBAOnboardingViewPager;

    @BindView(R.id.continue_button_label)
    TextView mContinueButtonText;

    private boolean isMyTBALoginComplete = false;

    @Inject @Named("firebase_auth") AuthProvider mAuthProvider;
    @Inject AccountController mAccountController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytba_onboarding);
        ButterKnife.bind(this);
        getComponent().inject(this);

        mMyTBAOnboardingViewPager.setCallbacks(this);
        mMyTBAOnboardingViewPager.setTitleText(R.string.what_is_mytba);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MYTBA_LOGIN_COMPLETE)) {
                isMyTBALoginComplete = savedInstanceState.getBoolean(MYTBA_LOGIN_COMPLETE);
            }
        }

        if (isMyTBALoginComplete) {
            mMyTBAOnboardingViewPager.setUpForLoginSuccess();
        } else {
            mMyTBAOnboardingViewPager.setUpForLoginPrompt();
        }

        mMyTBAOnboardingViewPager.getViewPager().addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateContinueButtonText();
            }
        });

        updateContinueButtonText();
    }

    private void updateContinueButtonText() {
        if (mMyTBAOnboardingViewPager.isOnLoginPage()) {
            mContinueButtonText.setText(R.string.finish_caps);
        } else {
            mContinueButtonText.setText(R.string.skip_intro_caps);
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
                            mMyTBAOnboardingViewPager.setUpForLoginSuccess();
                            isMyTBALoginComplete = true;
                            mAccountController.onAccountConnect(MyTBAOnboardingActivity.this, user);
                        }, throwable -> {
                            TbaLogger.e("Error logging in");
                            throwable.printStackTrace();
                            mAccountController.setMyTbaEnabled(false);
                        });
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_LONG).show();
                mMyTBAOnboardingViewPager.setUpForLoginPrompt();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(MYTBA_LOGIN_COMPLETE, isMyTBALoginComplete);
    }

    @OnClick(R.id.cancel_button)
    void onCancelClick(View view) {
        finish();
    }

    @OnClick(R.id.continue_button)
    void onContinueClick(View view) {
        if (mMyTBAOnboardingViewPager.isOnLoginPage()) {
            // On the last page, the "continue" button turns into a "finish" button
            finish();
        } else {
            // On other pages, the "continue" button becomes a "skip intro" button
            mMyTBAOnboardingViewPager.scrollToLoginPage();
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

    @Override
    public void onAppleButtonClicked() {
//            if (AppleAuthProvider.getCurrentUser() == null){
//                AppleAuthProvider.startSignUpWithApple(this);
//            } else if (AppleAuthProvider.getCurrentUser() != null){
//                AppleAuthProvider.getPendingAuthResult();
//            } else {
//                Toast.makeText(this, R.string.mytba_no_signin_intent, Toast.LENGTH_SHORT).show();
//                TbaLogger.e("Unable to get login Intent");
//            }
            AppleAuthProvider.startSignUpWithApple(this);
            Toast.makeText(this, R.string.mytba_no_signin_intent, Toast.LENGTH_SHORT).show();
    }

    private AuthComponent getComponent() {
        TbaAndroid application = (TbaAndroid) getApplication();
        return DaggerAuthComponent.builder()
                                  .authModule(application.getAuthModule())
                                  .accountModule(application.getAccountModule())
                                  .build();
    }
}
