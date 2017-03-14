package com.thebluealliance.androidclient.datafeed.combiners;

import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.subscribers.TeamInfoSubscriber;

import java.util.List;

import rx.functions.Func2;

public class TeamMediaAndSocialMediaCombiner implements Func2<Team, List<Media>, TeamInfoSubscriber.Model> {

    @Override
    public TeamInfoSubscriber.Model call(Team team, List<Media> medias) {
        return new TeamInfoSubscriber.Model(team, medias);
    }
}
