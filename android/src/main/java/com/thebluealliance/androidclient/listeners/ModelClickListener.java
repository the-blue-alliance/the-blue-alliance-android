package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.helpers.ModelHelper;

/**
 * File created by phil on 8/13/14.
 */
public class ModelClickListener implements View.OnClickListener{

    private Context context;
    private String key;
    private ModelHelper.MODELS type;

    public ModelClickListener(Context context, String key, ModelHelper.MODELS type){
        this.key = key;
        this.context = context;
        this.type = type;
    }

    @Override
    public void onClick(View v) {
        Intent intent = ModelHelper.getIntentFromKey(context, key, type);
        Log.d(Constants.LOG_TAG, "Conext: "+context);
        context.startActivity(intent);
    }
}
