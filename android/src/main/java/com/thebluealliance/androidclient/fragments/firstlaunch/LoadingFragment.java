package com.thebluealliance.androidclient.fragments.firstlaunch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.FirstLaunchActivity;
import com.thebluealliance.androidclient.background.firstlaunch.LoadAllData;

/**
 * Created by Nathan on 5/26/2014.
 */
public class LoadingFragment extends Fragment {

    TextView status;
    TextView message;

    private LoadAllData task;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_loading, container, false);
        status = (TextView) v.findViewById(R.id.status);
        message = (TextView) v.findViewById(R.id.message);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        task = new LoadAllData(this);
        task.execute();
    }

    public void onLoadingProgressUpdate(LoadAllData.LoadProgressInfo info) {
        if (info.state == LoadAllData.LoadProgressInfo.STATE_NO_CONNECTION) {
            getActivity().finish();
        } else if (info.state == LoadAllData.LoadProgressInfo.STATE_LOADING) {
            message.setText(info.message);
        } else if (info.state == LoadAllData.LoadProgressInfo.STATE_FINISHED) {
            ((FirstLaunchActivity) getActivity()).advanceToNextPage();
        }
    }
}
