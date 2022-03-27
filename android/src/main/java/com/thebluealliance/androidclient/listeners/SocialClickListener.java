package com.thebluealliance.androidclient.listeners;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.thebluealliance.androidclient.helpers.AnalyticsHelper;

import javax.inject.Inject;

public class SocialClickListener implements View.OnClickListener {

    private Context mContext;
    private String mModelKey;

    @Inject
    public SocialClickListener(Context context) {
        mContext = context;
    }

    public void setModelKey(String modelKey) {
        mModelKey = modelKey;
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() != null) {
            String uri = view.getTag().toString();

            //social button was clicked. Track the call
            AnalyticsHelper.sendSocialUpdate(mContext, uri, mModelKey);

            Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            try {
                mContext.startActivity(i);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, "No app can handle that request", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
