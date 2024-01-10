package com.thebluealliance.androidclient.fragments.team;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.binders.ExpandableListViewBinder;
import com.thebluealliance.androidclient.databinding.ExpandableListViewWithSpinnerBinding;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.eventbus.YearChangedEvent;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.interfaces.HasEventParam;
import com.thebluealliance.androidclient.interfaces.HasYearParam;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.MediaListSubscriber;
import com.thebluealliance.androidclient.views.NoDataView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import rx.Observable;

@AndroidEntryPoint
public class TeamMediaFragment extends DatafeedFragment<
        List<Media>,
        List<ListGroup>,
        ExpandableListViewWithSpinnerBinding,
        MediaListSubscriber,
        ExpandableListViewBinder>
        implements HasYearParam, HasEventParam {

    public static final String TEAM_KEY = "team", YEAR = "year";

    private String mTeamKey;
    private int mYear;

    public static TeamMediaFragment newInstance(String teamKey, int year) {
        Bundle args = new Bundle();
        args.putString(TEAM_KEY, teamKey);
        args.putInt(YEAR, year);

        TeamMediaFragment f = new TeamMediaFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null || !args.containsKey(TEAM_KEY) || !args.containsKey(YEAR)) {
            throw new IllegalArgumentException("TeamMediaFragment must be constructed with a team key and year");
        }
        mTeamKey = args.getString(TEAM_KEY);
        mYear = args.getInt(YEAR, -1);
        if (mYear == -1) {
            mYear = Utilities.getCurrentYear();
        }
        super.onCreate(savedInstanceState);

        mBinder.setExpandMode(ExpandableListViewBinder.MODE_EXPAND_ALL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.expandable_list_view_with_spinner, container, false);
        mBinder.setRootView(v);
        mBinder.setNoDataView((NoDataView) v.findViewById(R.id.no_data));
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onYearChanged(YearChangedEvent event) {
        mYear = event.getYear();
        onRefreshStart(RefreshController.NOT_REQUESTED_BY_USER);
    }

    @Override
    public String getEventKey() {
        return "";
    }

    @Override
    public int getYear() {
        return mYear;
    }

    @Override
    protected Observable<List<Media>> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchTeamMediaInYear(mTeamKey, mYear, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("teamMedia_%1$s_%2$d", mTeamKey, mYear);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_photo_camera_black_48dp, R.string.no_media_data);
    }
}