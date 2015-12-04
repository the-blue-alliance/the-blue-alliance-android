package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.view.View;

import com.thebluealliance.androidclient.activities.settings.MyTBAModelSettingsActivity;
import com.thebluealliance.androidclient.helpers.ModelType;

public class ModelSettingsClickListener implements View.OnClickListener {

    private final Context mContext;
    private final String mModelKey;
    private final ModelType mModelType;

    public ModelSettingsClickListener(Context context, String modelKey, ModelType modelType) {
        mContext = context;
        mModelKey = modelKey;
        mModelType = modelType;
    }

    @Override
    public void onClick(View v) {
        mContext.startActivity(
          MyTBAModelSettingsActivity.newInstance(mContext, mModelKey, mModelType));
    }
}
