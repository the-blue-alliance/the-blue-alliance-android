package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.thebluealliance.androidclient.helpers.ModelHelper;

/**
 * File created by phil on 8/13/14.
 */
public class ModelClickListener implements View.OnClickListener{

    private Context context;
    private String key;

    public ModelClickListener(Context context, String key){
        this.key = key;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent = ModelHelper.getIntentFromKey(context, key);
        context.startActivity(intent);
    }
}
