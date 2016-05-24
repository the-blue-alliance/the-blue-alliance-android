package com.thebluealliance.androidclient.views;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.MyTBAOnboardingPagerAdapter;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.relex.circleindicator.CircleIndicator;

public class MyTBAOnboardingViewPager extends RelativeLayout implements View.OnClickListener {

    private final ViewPager mViewPager;
    private final View mSignInButton;
    private final TextView myTBATitle;
    private final TextView myTBASubtitle;

    private Callbacks mCallbacks;

    public MyTBAOnboardingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.mytba_onboarding_view_pager, this, true);

        myTBATitle = (TextView) findViewById(R.id.mytba_title);
        myTBASubtitle = (TextView) findViewById(R.id.mytba_subtitle);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new MyTBAOnboardingPagerAdapter(mViewPager));
        mViewPager.setOffscreenPageLimit(10);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.mytba_pager_indicator);
        indicator.setViewPager(mViewPager);

        mSignInButton = findViewById(R.id.google_sign_in_button);
        mSignInButton.setOnClickListener(this);
    }

    public void setCallbacks(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.google_sign_in_button:
                if (mCallbacks != null) {
                    mCallbacks.onSignInButtonClicked();
                }
                break;
        }

    }

    public void scrollToLoginPage() {
        // Login page should always be the last page
        mViewPager.setCurrentItem(mViewPager.getAdapter().getCount() - 1);
    }

    public boolean isOnLoginPage() {
        return mViewPager.getCurrentItem() == (mViewPager.getAdapter().getCount() - 1);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public void setTitleText(@StringRes int resId) {
        myTBATitle.setText(resId);
    }

    public void setUpForNoPlayServices() {
        myTBATitle.setVisibility(View.VISIBLE);
        myTBATitle.setText(R.string.mytba_no_play_services);

        myTBASubtitle.setVisibility(View.VISIBLE);
        myTBASubtitle.setText(R.string.mytba_no_play_services_subtitle);
    }

    public void setUpForLoginPrompt() {
        myTBATitle.setVisibility(View.VISIBLE);
        myTBATitle.setText(R.string.mytba_get_started_title);

        myTBASubtitle.setVisibility(View.VISIBLE);
        myTBASubtitle.setText(R.string.mytba_login_prompt);
    }

    public void setUpForLoginSuccess() {
        myTBATitle.setVisibility(View.VISIBLE);
        myTBATitle.setText(R.string.mytba_login_success);

        myTBASubtitle.setVisibility(View.VISIBLE);
        myTBASubtitle.setText(R.string.mytba_login_success_subtitle);

        mSignInButton.setVisibility(View.GONE);
    }

    public interface Callbacks {
        void onSignInButtonClicked();
    }
}
