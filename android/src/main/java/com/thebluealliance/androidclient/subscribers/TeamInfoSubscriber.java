package com.thebluealliance.androidclient.subscribers;

import android.content.Context;

import androidx.collection.ArrayMap;

import com.thebluealliance.androidclient.binders.TeamInfoBinder;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.helpers.PitLocationHelper;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.types.MediaType;

import java.util.List;
import java.util.Map;

public class TeamInfoSubscriber extends BaseAPISubscriber<TeamInfoSubscriber.Model, TeamInfoBinder.Model>{

    private final Context mContext;
    private final AppConfig mAppConfig;

    public TeamInfoSubscriber(Context context, AppConfig appConfig) {
        mContext = context;
        mAppConfig = appConfig;
        mDataToBind = null;
    }

    @Override
    public void parseData() {
        mDataToBind = new TeamInfoBinder.Model();
        Map<MediaType, String> socialMediaByType = new ArrayMap<MediaType, String>();
        Team team = mAPIData.team;
        List<Media> socialMedia = mAPIData.socialMedia;
        mDataToBind.teamKey = team.getKey();
        mDataToBind.fullName = team.getName();
        mDataToBind.nickname = team.getNickname();
        mDataToBind.teamNumber = team.getTeamNumber();
        mDataToBind.location = team.getLocation();
        if (team.getWebsite() != null) {
            mDataToBind.website = team.getWebsite();
        } else {
            mDataToBind.website = "";
        }
        if (team.getMotto() != null) {
            mDataToBind.motto = team.getMotto();
        } else {
            mDataToBind.motto = "";
        }

        // Separate social medias by type
        mDataToBind.socialMedia = socialMediaByType;
        for (int i = 0; socialMedia != null && i < socialMedia.size(); i++) {
            Media media = socialMedia.get(i);
            MediaType mediaType = MediaType.fromString(media.getType());
            socialMediaByType.put(mediaType, media.getForeignKey());
        }

        // CMP Pit Location Stuff
        mDataToBind.showPitLocation = PitLocationHelper.shouldShowPitLocation(mAppConfig);
        mDataToBind.pitLocation = PitLocationHelper.getPitLocation(mContext, team.getKey());
    }

    @Override
    public boolean isDataValid() {
        return super.isDataValid() && mAPIData.team != null;
    }

    public static class Model {
        public final Team team;
        public final List<Media> socialMedia;

        public Model(Team team, List<Media> socialMedia) {
            this.team = team;
            this.socialMedia = socialMedia;
        }
    }
}
