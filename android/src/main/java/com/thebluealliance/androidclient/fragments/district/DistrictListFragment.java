package com.thebluealliance.androidclient.fragments.district;

import android.os.Bundle;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.DistrictListSubscriber;

import java.util.List;

import rx.Observable;

public class DistrictListFragment
  extends ListViewFragment<List<District>, DistrictListSubscriber> {

    public static final String YEAR = "year";

    private int mYear;

    public static DistrictListFragment newInstance(int year) {
        DistrictListFragment f = new DistrictListFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mYear = getArguments().getInt(YEAR, Utilities.getCurrentYear());
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<District>> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchDistrictList(mYear, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("districtList_$1%d", mYear);
    }

    @Override
    protected NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_assignment_black_48dp, R.string.no_district_list);
    }
}
