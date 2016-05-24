package com.thebluealliance.androidclient.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.PlusManager;
import com.thebluealliance.androidclient.views.MyTBAOnboardingViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MyTBAOnboardingActivity extends AppCompatActivity implements View.OnClickListener, PlusManager.Callbacks, MyTBAOnboardingViewPager.Callbacks {

    private static final String MYTBA_LOGIN_COMPLETE = "mytba_login_complete";

    private MyTBAOnboardingViewPager mMyTBAOnboardingViewPager;
    private TextView mContinueButtonText;

    private boolean isMyTBALoginComplete = false;

    private PlusManager mPlusManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPlusManager = new PlusManager(this, this);

        setContentView(R.layout.activity_mytba_onboarding);

        mMyTBAOnboardingViewPager = (MyTBAOnboardingViewPager) findViewById(R.id.mytba_view_pager);
        mMyTBAOnboardingViewPager.setCallbacks(this);
        mMyTBAOnboardingViewPager.setTitleText(R.string.what_is_mytba);

        mContinueButtonText = (TextView) findViewById(R.id.continue_button_label);

        findViewById(R.id.cancel_button).setOnClickListener(this);
        findViewById(R.id.continue_button).setOnClickListener(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MYTBA_LOGIN_COMPLETE)) {
                isMyTBALoginComplete = savedInstanceState.getBoolean(MYTBA_LOGIN_COMPLETE);
            }
        }

        if (isMyTBALoginComplete) {
            mMyTBAOnboardingViewPager.setUpForLoginSuccess();
        } else if (!supportsGooglePlayServices()) {
            mMyTBAOnboardingViewPager.setUpForNoPlayServices();
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
        mPlusManager.onActivityResult(requestCode, resultCode);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(MYTBA_LOGIN_COMPLETE, isMyTBALoginComplete);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.cancel_button:
                finish();
                break;
            case R.id.continue_button:
                if (mMyTBAOnboardingViewPager.isOnLoginPage()) {
                    // On the last page, the "continue" button turns into a "finish" button
                    finish();
                } else {
                    // On other pages, the "continue" button becomes a "skip intro" button
                    mMyTBAOnboardingViewPager.scrollToLoginPage();
                }
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
