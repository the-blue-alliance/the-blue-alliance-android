package com.thebluealliance.androidclient.helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.activities.TeamAtDistrictActivity;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.activities.ViewDistrictActivity;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.ModelListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Team;

/**
 * File created by phil on 8/13/14.
 */
public class ModelHelper {
    public enum MODELS {
        EVENT,
        TEAM,
        MATCH,
        EVENTTEAM,
        DISTRICT,
        DISTRICTTEAM,
        AWARD;

        public String getTitle() {
            switch (this) {
                case EVENT:
                    return "Events";
                case TEAM:
                    return "Teams";
                case MATCH:
                    return "Matches";
                case EVENTTEAM:
                    return "Team@Event";
                case DISTRICT:
                    return "Districts";
                case DISTRICTTEAM:
                    return "Team@District";
                case AWARD:
                    return "Awards";
            }
            return "";
        }
    }

    public static MODELS getModelFromKey(String key) {
        if (DistrictHelper.validateDistrictKey(key)) {
            return MODELS.DISTRICT;
        } else if (EventHelper.validateEventKey(key)) {
            return MODELS.EVENT;
        } else if (TeamHelper.validateTeamKey(key)) {
            return MODELS.TEAM;
        } else if (MatchHelper.validateMatchKey(key)) {
            return MODELS.MATCH;
        } else if (EventTeamHelper.validateEventTeamKey(key)) {
            return MODELS.EVENTTEAM;
        } else if (DistrictTeamHelper.validateDistrictTeamKey(key)) {
            return MODELS.DISTRICTTEAM;
        } else if (AwardHelper.validateAwardKey(key)) {
            return MODELS.AWARD;
        } else {
            return null;
        }
    }

    public static String[] getNotificationTypes(MODELS type) {
        Log.d(Constants.LOG_TAG, "getting notifications for: " + type);
        switch (type) {
            case EVENT:
                return Event.NOTIFICATION_TYPES;
            case TEAM:
                return Team.NOTIFICATION_TYPES;
            case MATCH:
                return Match.NOTIFICATION_TYPES;
            case EVENTTEAM:
                return EventTeam.NOTIFICATION_TYPES;
            case DISTRICT:
                return District.NOTIFICATION_TYPES;
            default:
                return new String[]{};
        }
    }

    public static Intent getIntentFromKey(Context context, String key) {
        return getIntentFromKey(context, key, null);
    }

    public static Intent getIntentFromKey(Context context, String key, MODELS type) {
        if (type == null) {
            type = getModelFromKey(key);
        }
        if (type == null) {
            return null;
        }
        switch (type) {
            case EVENT:
                return ViewEventActivity.newInstance(context, key);
            case TEAM:
                return ViewTeamActivity.newInstance(context, key);
            case MATCH:
                return ViewMatchActivity.newInstance(context, key);
            case EVENTTEAM:
                return TeamAtEventActivity.newInstance(context, key);
            case DISTRICT:
                return ViewDistrictActivity.newInstance(context, key);
            case DISTRICTTEAM:
                return TeamAtDistrictActivity.newInstance(context, key);
            default:
                return null;
        }
    }

    public static ListItem renderModelFromKey(Context context, String key) {
        try {
            String text;
            switch (getModelFromKey(key)) {
                case EVENT:
                    Event event = DataManager.Events.getEvent(context, key, false).getData();
                    text = event.getEventYear() + " " + event.getShortName();
                    break;
                case TEAM:
                    Team team = DataManager.Teams.getTeam(context, key, false).getData();
                    text = team.getNickname();
                    break;
                case MATCH:
                    Match match = DataManager.Matches.getMatch(context, key, false).getData();
                    text = match.getEventKey() + " " + match.getTitle();
                    break;
                case EVENTTEAM:
                    Team eTeam = DataManager.Teams.getTeam(context, EventTeamHelper.getTeamKey(key), false).getData();
                    Event eEvent = DataManager.Events.getEvent(context, EventTeamHelper.getEventKey(key), false).getData();
                    text = eTeam.getNickname() + " @ " + eEvent.getEventYear() + " " + eEvent.getShortName();
                    break;
                case DISTRICT:
                    District district = DataManager.Districts.getDistrict(context, key).getData();
                    text = district.getYear() + " " + DistrictHelper.DISTRICTS.fromAbbreviation(district.getAbbreviation()).getName();
                    break;
                default:
                    return null;
            }
            return new ModelListElement(text, key);
        } catch (BasicModel.FieldNotDefinedException | DataManager.NoDataException e) {
            e.printStackTrace();
        }
        return null;
    }
}
