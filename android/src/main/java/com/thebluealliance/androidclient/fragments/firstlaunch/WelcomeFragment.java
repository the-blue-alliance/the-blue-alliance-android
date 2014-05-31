package com.thebluealliance.androidclient.fragments.firstlaunch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.FirstLaunchActivity;

/**
 * Created by Nathan on 5/25/2014.
 */
public class WelcomeFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);
        ((Button) v.findViewById(R.id.next_page)).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.next_page) {
            ((FirstLaunchActivity) getActivity()).advanceToNextPage();
        }
    }
}
