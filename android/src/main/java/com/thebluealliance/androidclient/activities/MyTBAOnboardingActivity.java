package com.thebluealliance.androidclient.activities;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.auth.AuthProvider;
import com.thebluealliance.androidclient.di.components.AuthComponent;
import com.thebluealliance.androidclient.di.components.DaggerAuthComponent;
import com.thebluealliance.androidclient.views.MyTBAOnboardingViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MyTBAOnboardingActivity extends AppCompatActivity
        implements MyTBAOnboardingViewPager.Callbacks{

    private static final String MYTBA_LOGIN_COMPLETE = "mytba_login_complete";
    private static final int SIGNIN_CODE = 254;

    @Bind(R.id.mytba_view_pager)
    MyTBAOnboardingViewPager mMyTBAOnboardingViewPager;

    @Bind(R.id.continue_button_label)
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
                            Log.d(Constants.LOG_TAG, "User logged in: " + user.getEmail());
                            mMyTBAOnboardingViewPager.setUpForLoginSuccess();
                            isMyTBALoginComplete = true;
                            mAccountController.setMyTbaEnabled(true);
                        }, throwable -> {
                            Log.e(Constants.LOG_TAG, "Error logging in");
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
        startActivityForResult(signInIntent, SIGNIN_CODE);
    }

    private AuthComponent getComponent() {
        TBAAndroid application = (TBAAndroid) getApplication();
        return DaggerAuthComponent.builder()
                                  .authModule(application.getAuthModule())
                                  .accountModule(application.getAccountModule())
                                  .build();
    }
}
