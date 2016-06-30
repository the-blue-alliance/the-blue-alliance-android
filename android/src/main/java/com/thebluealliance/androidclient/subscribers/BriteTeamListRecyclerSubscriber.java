package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.comparators.TeamSortByNumberComparator;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.viewmodels.TeamViewModel;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class BriteTeamListRecyclerSubscriber extends BriteBaseAPISubscriber<List<Team>, List<Object>> {

    private Context mContext;
    private TeamSortByNumberComparator mComparator;
    private
    @Team.RenderType
    int mRenderMode;

    @Inject
    public BriteTeamListRecyclerSubscriber(Context context) {
        super();
        mContext = context;
        mDataToBind = new ArrayList<>();
        mComparator = new TeamSortByNumberComparator();
        mRenderMode = Team.RENDER_BASIC;
    }

    public void setRenderMode(@Team.RenderType int mode) {
        mRenderMode = mode;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        Log.d(Constants.LOG_TAG, "parsing " + mAPIData.size() + " teams");
        Collections.sort(mAPIData, mComparator);
        for (int i = 0; i < mAPIData.size(); i++) {
            Team team = mAPIData.get(i);
            if (team == null) {
                continue;
            }
            TeamViewModel item = team.renderToViewModel(mContext, mRenderMode);
            if (item == null) {
                continue;
            }
            mDataToBind.add(item);
        }
    }
}
