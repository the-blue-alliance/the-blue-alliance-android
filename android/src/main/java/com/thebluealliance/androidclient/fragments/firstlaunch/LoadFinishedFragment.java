package com.thebluealliance.androidclient.fragments.firstlaunch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.StartActivity;

/**
 * Created by Nathan on 5/25/2014.
 */
public class LoadFinishedFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_load_finished, container, false);
        ((Button) v.findViewById(R.id.finish)).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.finish) {
            getActivity().startActivity(new Intent(getActivity(), StartActivity.class));
            getActivity().finish();
        }
    }
}
