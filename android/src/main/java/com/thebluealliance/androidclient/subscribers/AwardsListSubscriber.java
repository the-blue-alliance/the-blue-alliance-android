package com.thebluealliance.androidclient.subscribers;

import android.util.ArrayMap;

import com.thebluealliance.androidclient.comparators.AwardSortComparator;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.eventbus.EventAwardsEvent;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.renderers.AwardRenderer;
import com.thebluealliance.androidclient.renderers.ModelRenderer;
import com.thebluealliance.api.model.IAwardRecipient;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AwardsListSubscriber extends BaseAPISubscriber<List<Award>, List<ListItem>> {

    private static final AwardSortComparator AWARD_COMPARATOR = new AwardSortComparator();

    private String mTeamKey;
    private Database mDb;
    private ModelRenderer<Award, AwardRenderer.RenderArgs> mRenderer;

    public AwardsListSubscriber(Database db, ModelRenderer<Award, AwardRenderer.RenderArgs> renderer) {
        super();
        mDataToBind = new ArrayList<>();
        mRenderer = renderer;
        mDb = db;
    }

    public void setTeamKey(String teamKey) {
        mTeamKey = teamKey;
    }

    @Override
    public void parseData()  {
        mDataToBind.clear();
        Map<String, Team> teams = new ArrayMap<String, Team>();
        ArrayList<Award> sortedAwards = new ArrayList<>(mAPIData);
        Collections.sort(sortedAwards, AWARD_COMPARATOR);
        for (int i = 0; i < sortedAwards.size(); i++) {
            Award award = sortedAwards.get(i);
            if (award.getRecipientList() == null) continue;
            for (IAwardRecipient winner : award.getRecipientList()) {
                if (winner != null && winner.getTeamKey() != null){
                    String teamKey = winner.getTeamKey();
                    Team team = mDb.getTeamsTable().get(teamKey);
                    teams.put(teamKey, team);
                }
            }
            AwardRenderer.RenderArgs args = new AwardRenderer.RenderArgs(teams, mTeamKey);
            mDataToBind.add(mRenderer.renderFromModel(award, args));
        }
    }

    @Override
    public boolean isDataValid() {
        return super.isDataValid() && !mAPIData.isEmpty();
    }

    @Override
    protected boolean shouldPostToEventBus() {
        return true;
    }

    @Override
    protected void postToEventBus(EventBus eventBus) {
        eventBus.post(new EventAwardsEvent(mAPIData));
    }
}
