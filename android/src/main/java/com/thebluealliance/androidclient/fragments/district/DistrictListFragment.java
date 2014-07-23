package com.thebluealliance.androidclient.fragments.district;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by phil on 7/23/14.
 */
public class DistrictListFragment extends Fragment {

    public static final String YEAR = "year";

    public static DistrictListFragment newInstance(int year){
        DistrictListFragment f = new DistrictListFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        f.setArguments(args);
        return f;
    }

}
