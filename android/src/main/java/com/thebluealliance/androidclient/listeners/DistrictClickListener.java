package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.view.View;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewDistrictActivity;
import com.thebluealliance.androidclient.helpers.DistrictHelper;

/**
 * Created by phil on 7/24/14.
 */
public class DistrictClickListener implements View.OnClickListener {

    private Context context;
    private String key;

    public DistrictClickListener(Context context, String key){
        this.context = context;
        this.key = key;
    }

    @Override
    public void onClick(View v) {
        String districtKey = v.findViewById(R.id.title).getTag().toString();
        if(DistrictHelper.validateDistrictKey(districtKey)){
            context.startActivity(ViewDistrictActivity.newInstance(context, key));
        }
    }
}
