package com.thebluealliance.androidclient.fragments.district;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.thebluealliance.androidclient.interfaces.RefreshListener;

/**
 * Created by phil on 7/24/14.
 */
public class DistrictRankingsFragment extends Fragment implements RefreshListener {

    public static final String KEY = "districtKey";

    public static DistrictRankingsFragment newInstance(String key){
        DistrictRankingsFragment f = new DistrictRankingsFragment();
        Bundle args = new Bundle();
        args.putString(KEY, key);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onRefreshStart() {

    }

    @Override
    public void onRefreshStop() {

    }
}
