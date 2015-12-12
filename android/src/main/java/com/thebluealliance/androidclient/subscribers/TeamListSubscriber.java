package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.comparators.TeamSortByNumberComparator;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.renderers.TeamRenderer;
import com.thebluealliance.androidclient.renderers.TeamRenderer.RenderType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamListSubscriber extends BaseAPISubscriber<List<Team>, List<ListItem>> {

    private TeamSortByNumberComparator mComparator;
    private TeamRenderer mRenderer;
    private
    @RenderType
    int mRenderMode;

    public TeamListSubscriber(TeamRenderer renderer) {
        super();
        mRenderer = renderer;
        mDataToBind = new ArrayList<>();
        mComparator = new TeamSortByNumberComparator();
        mRenderMode = TeamRenderer.RENDER_BASIC;
    }

    public void setRenderMode(@RenderType int mode) {
        mRenderMode = mode;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData == null) {
            return;
        }
        Collections.sort(mAPIData, mComparator);
        for (int i = 0; i < mAPIData.size(); i++) {
            Team team = mAPIData.get(i);
            if (team == null) {
                continue;
            }
            ListItem item = mRenderer.renderFromModel(team, mRenderMode);
            if (item == null) {
                continue;
            }
            mDataToBind.add(item);
        }
    }
}
