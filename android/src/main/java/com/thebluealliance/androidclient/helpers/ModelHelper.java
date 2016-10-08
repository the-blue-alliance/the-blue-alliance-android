package com.thebluealliance.androidclient.helpers;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.activities.TeamAtDistrictActivity;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.activities.ViewDistrictActivity;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.types.ModelType;

import android.content.Context;
import android.content.Intent;

public final class ModelHelper {

    private ModelHelper() {
        // unused
    }

    public static ModelType getModelFromEnum(int model_enum) {
        return ModelType.values()[model_enum];
    }

    public static String[] getNotificationTypes(ModelType type) {
        TbaLogger.d("getting notifications for: " + type);
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

    public static Intent getIntentFromKey(Context context, String key, ModelType type) {
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
}
