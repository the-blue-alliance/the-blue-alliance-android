package com.thebluealliance.androidclient.fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.EventsByWeekFragmentAdapter;
import com.thebluealliance.androidclient.adapters.ViewTeamFragmentAdapter;
import com.thebluealliance.androidclient.interfaces.ActionBarSpinnerListener;

public class EventsByWeekFragment extends Fragment implements ActionBarSpinnerListener{

    private int mYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events_by_week, container, false);
    }

    @Override
    public void actionBarSpinnerSelected(AdapterView<?> adapterView, int position) {
        View view = getView();
        ViewPager pager = (ViewPager) view.findViewById(R.id.view_pager);
        mYear = Integer.parseInt(adapterView.getAdapter().getItem(position).toString());
        pager.setAdapter(new EventsByWeekFragmentAdapter(getFragmentManager(), mYear));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        Log.d("EventsByWeekFragment", "action bar selected: position " + position);
    }
}
