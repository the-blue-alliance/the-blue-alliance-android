package com.thebluealliance.androidclient.fragments.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.DistrictPointsListSubscriber;

import rx.Observable;

public class EventDistrictPointsFragment
        extends ListViewFragment<JsonObject, DistrictPointsListSubscriber> {
    private static final String KEY = "event_key";

    private String mEventKey;

    //TODO implement way to show "not part of district" warning
    private boolean isDistrict;

    public static EventDistrictPointsFragment newInstance(String eventKey) {
        EventDistrictPointsFragment f = new EventDistrictPointsFragment();
        Bundle data = new Bundle();
        data.putString(KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mEventKey = getArguments().getString(KEY, "");
        }
        super.onCreate(savedInstanceState);
        isDistrict = true;
        setHasOptionsMenu(true);
        mSubscriber.setEventKey(mEventKey);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.district_point_math, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        //disable touch feedback (you can't click the elements here...)
        mListView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        mListView.setSelector(R.drawable.transparent);
        return view;
    }

    public void updateDistrict(boolean isDistrict) {
        this.isDistrict = isDistrict;
        if (getView() == null) {
            return;
        }

        View moreInfoContainer = getView().findViewById(R.id.more_info_container);
        if (isDistrict) {
            moreInfoContainer.setVisibility(View.GONE);
        } else {
            moreInfoContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<JsonObject> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchEventDistrictPoints(mEventKey, tbaCacheHeader);
    }

    @Override
    protected NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_recent_actors_black_48dp, R.string.no_district_points);
    }
}
