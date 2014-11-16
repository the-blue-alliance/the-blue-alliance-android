package com.thebluealliance.androidclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;

/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class AuthenticatorActivity extends PlusBaseActivity {

    // UI references.
    private View mProgressView;
    private SignInButton mPlusSignInButton;
    private Button mPlusNotNowButton;
    private Button mPlusSignOutButton;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        // Find the Google+ sign in button.
        mPlusSignInButton = (SignInButton) findViewById(R.id.plus_sign_in_button);
        mPlusSignOutButton = (Button) findViewById(R.id.plus_sign_out_button);
        mPlusNotNowButton = (Button) findViewById(R.id.mytba_opt_out_button);
        if (supportsGooglePlayServices()) {
            // Set a listener to connect the user when the G+ button is clicked.
            mPlusSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });
            mPlusSignOutButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play
            // Services.
            mPlusSignInButton.setVisibility(View.GONE);
            mPlusSignOutButton.setVisibility(View.GONE);
            TextView desc = (TextView)findViewById(R.id.mytba_message);
            desc.setText(getString(R.string.gms_not_supported));
            return;
        }

        final Intent homeIntent = new Intent(this, HomeActivity.class);
        mPlusNotNowButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(homeIntent);
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onPlusClientSignIn() {
        AccountHelper.enableMyTBA(this, true);
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onPlusClientBlockingUI(boolean show) {
        showProgress(show);
    }

    @Override
    protected void updateConnectButtonState() {
        boolean connected = getPlusClient().isConnected();

        mPlusSignInButton.setVisibility(connected ? View.GONE : View.VISIBLE);
        mPlusNotNowButton.setVisibility(connected ? View.GONE : View.VISIBLE);
        mPlusSignOutButton.setVisibility(connected ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onPlusClientRevokeAccess() {
        // TODO: Access to the user's G+ account has been revoked.  Per the developer terms, delete
        // any stored user data here.
    }

    @Override
    protected void onPlusClientSignOut() {
        AccountHelper.enableMyTBA(this, false);
    }

    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS;
    }
}



