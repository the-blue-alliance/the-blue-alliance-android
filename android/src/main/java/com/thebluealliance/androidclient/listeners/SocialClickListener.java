package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.thebluealliance.androidclient.helpers.AnalyticsHelper;

import java.util.List;

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
        PackageManager manager = mContext.getPackageManager();
        if (view.getTag() != null) {

            String uri = view.getTag().toString();

            //social button was clicked. Track the call
            AnalyticsHelper.sendSocialUpdate(mContext, uri, mModelKey);

            Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            List<ResolveInfo> handlers = manager.queryIntentActivities(i, 0);
            if (!handlers.isEmpty()) {
                // There is an application to handle this intent intent
                mContext.startActivity(i);
            } else {
                // No application can handle this intent
                Toast.makeText(mContext, "No app can handle that request", Toast.LENGTH_SHORT)
                  .show();
            }
        }
    }
}
