package com.thebluealliance.androidclient.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.SignInButton;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.MyTBAOnboardingPagerAdapter;
import com.thebluealliance.androidclient.databinding.MytbaOnboardingViewPagerBinding;

public class MyTBAOnboardingViewPager extends RelativeLayout implements View.OnClickListener {
    final private MytbaOnboardingViewPagerBinding mBinding;
    final private MyTBAOnboardingPagerAdapter mAdapter;

    private Callbacks mCallbacks;

    public MyTBAOnboardingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBinding = MytbaOnboardingViewPagerBinding.inflate(LayoutInflater.from(context), this);
        mAdapter = new MyTBAOnboardingPagerAdapter(mBinding.viewPager);
        mBinding.viewPager.setAdapter(mAdapter);
        mBinding.viewPager.setOffscreenPageLimit(10);
        mBinding.mytbaPagerIndicator.setViewPager(mBinding.viewPager);
        mBinding.googleSignInButton.setSize(SignInButton.SIZE_WIDE);
        mBinding.googleSignInButton.setOnClickListener(this);
        mBinding.enableNotificationsButton.setOnClickListener(this);
    }

    public void setCallbacks(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.google_sign_in_button && mCallbacks != null) {
            mCallbacks.onSignInButtonClicked();
        } else if (id == R.id.enable_notifications_button && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mCallbacks.onEnableNotificationsButtonClicked();
        }
    }

    public void scrollToLoginPage() {
        // Login page should always be the last page
        mBinding.viewPager.setCurrentItem(mAdapter.getLoginPageId());
    }

    public boolean isBeforeLoginPage() {
        return mBinding.viewPager.getCurrentItem() < mAdapter.getLoginPageId();
    }

    public boolean isOnLoginPage() {
        return mBinding.viewPager.getCurrentItem() == mAdapter.getLoginPageId();
    }

    public boolean isOnNotificationPermissionPage() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && mBinding.viewPager.getCurrentItem() == mAdapter.getNotificationPermissionPageId();
    }

    public boolean isDone() {
        return mBinding.viewPager.getCurrentItem() == mAdapter.getCount() - 1;
    }

    public void advance() {
        mBinding.viewPager.setCurrentItem(mBinding.viewPager.getCurrentItem() + 1);
    }

    public ViewPager getViewPager() {
        return mBinding.viewPager;
    }

    public void setTitleText(@StringRes int resId) {
        mBinding.mytbaTitle.setText(resId);
    }

    public void setUpForLoginPrompt() {
        mBinding.mytbaTitle.setVisibility(View.VISIBLE);
        mBinding.mytbaTitle.setText(R.string.mytba_get_started_title);

        mBinding.mytbaSubtitle.setVisibility(View.VISIBLE);
        mBinding.mytbaSubtitle.setText(R.string.mytba_login_prompt);
    }

    public void setUpForLoginSuccess() {
        mBinding.mytbaTitle.setVisibility(View.VISIBLE);
        mBinding.mytbaTitle.setText(R.string.mytba_login_success);

        mBinding.mytbaSubtitle.setVisibility(View.VISIBLE);
        mBinding.mytbaSubtitle.setText(R.string.mytba_login_success_subtitle);

        mBinding.googleSignInButton.setVisibility(View.GONE);
    }

    public void setUpForPermissionResult(boolean permissionGranted) {
        if (permissionGranted) {
            mBinding.enableNotificationsButton.setVisibility(GONE);
        } else {
            mBinding.enableNotificationsClarification.setVisibility(VISIBLE);
        }
    }

    public interface Callbacks {
        void onSignInButtonClicked();
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        void onEnableNotificationsButtonClicked();
    }
}
