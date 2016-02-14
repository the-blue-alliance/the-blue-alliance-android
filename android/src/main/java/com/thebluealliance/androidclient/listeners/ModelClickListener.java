package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.types.ModelType;

public class ModelClickListener implements View.OnClickListener {

    private Context context;
    private String key;
    private ModelType type;

    public ModelClickListener(Context context, String key, ModelType type) {
        this.key = key;
        this.context = context;
        this.type = type;
    }

    @Override
    public void onClick(View v) {
        Intent intent = ModelHelper.getIntentFromKey(context, key, type);
        AnalyticsHelper.sendClickUpdate(context, "model_click", key, "");
        context.startActivity(intent);
    }
}
