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
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.RequestParams;
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
        AWARD,
        MEDIA;

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

        public String getSingularTitle() {
            switch (this) {
                case EVENT:
                    return "Event";
                case TEAM:
                    return "Team";
                case MATCH:
                    return "Match";
                case EVENTTEAM:
                    return "Team@Event";
                case DISTRICT:
                    return "District";
                case DISTRICTTEAM:
                    return "Team@District";
                case AWARD:
                    return "Awards";
            }
            return "";
        }

        public int getEnum() {
            return this.ordinal();
        }
    }

    public static MODELS getModelFromEnum(int model_enum) {
        return MODELS.values()[model_enum];
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

    public static Intent getIntentFromKey(Context context, String key, MODELS type) {
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

    public static ListItem renderModelFromKey(Context context, String key, MODELS type, boolean showSettingsButton) {
        try {
            String text;
            Database db = Database.getInstance(context);
            switch (type) {
                case EVENT:
                    if (!db.getEventsTable().exists(key)) return null;
                    Event event = DataManager.Events.getEvent(context, key, new RequestParams()).getData();
                    text = event.getEventYear() + " " + event.getEventShortName();
                    break;
                case TEAM:
                    if (!db.getTeamsTable().exists(key)) return null;
                    Team team = DataManager.Teams.getTeam(context, key, new RequestParams()).getData();
                    text = team.getNickname();
                    break;
                case MATCH:
                    if (!db.getMatchesTable().exists(key)) return null;
                    Match match = DataManager.Matches.getMatch(context, key, new RequestParams()).getData();
                    text = match.getEventKey() + " " + match.getTitle();
                    break;
                case EVENTTEAM:
                    String teamKey = EventTeamHelper.getTeamKey(key), eventKey = EventTeamHelper.getEventKey(key);
                    if (!db.getEventsTable().exists(eventKey) || !db.getTeamsTable().exists(teamKey))
                        return null;
                    Team eTeam = DataManager.Teams.getTeam(context, teamKey, new RequestParams()).getData();
                    Event eEvent = DataManager.Events.getEvent(context, eventKey, new RequestParams()).getData();
                    text = eTeam.getNickname() + " @ " + eEvent.getEventYear() + " " + eEvent.getEventShortName();
                    break;
                case DISTRICT:
                    if (!db.getDistrictsTable().exists(key)) return null;
                    District district = DataManager.Districts.getDistrict(context, key).getData();
                    text = district.getYear() + " " + DistrictHelper.DISTRICTS.fromAbbreviation(district.getAbbreviation()).getName();
                    break;
                default:
                    return null;
            }
            return new ModelListElement(text, key, type);
        } catch (BasicModel.FieldNotDefinedException | DataManager.NoDataException e) {
            e.printStackTrace();
        }
        return null;
    }
}
