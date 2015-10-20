package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.helpers.DistrictHelper.DISTRICTS;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.helpers.ModelType;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.ModelListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Team;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyTbaModelRenderer implements ModelRenderer {

    private APICache mDatafeed;

    @Inject
    public MyTbaModelRenderer(APICache datafeed) {
        mDatafeed = datafeed;
    }

    @Override
    public ListItem renderFromKey(String key, ModelType.MODELS type) {
            String text;
        try {
            switch (type) {
                case EVENT:
                    Event event = mDatafeed.fetchEvent(key).toBlocking().first();
                    if (event == null) {
                        text = key;
                        break;
                    }
                    text = event.getEventYear() + " " + event.getEventShortName();
                    break;
                case TEAM:
                    Team team = mDatafeed.fetchTeam(key).toBlocking().first();
                    if (team == null) {
                        text = key;
                        break;
                    }
                    text = team.getNickname();
                    break;
                case MATCH:
                    Match match = mDatafeed.fetchMatch(key).toBlocking().first();
                    if (match == null) {
                        text = key;
                        break;
                    }
                    text = match.getEventKey() + " " + match.getTitle();
                    break;
                case EVENTTEAM:
                    String teamKey = EventTeamHelper.getTeamKey(key);
                    String eventKey = EventTeamHelper.getEventKey(key);
                    Team eTeam = mDatafeed.fetchTeam(teamKey).toBlocking().first();
                    Event eEvent = mDatafeed.fetchEvent(eventKey).toBlocking().first();
                    if (eTeam == null || eEvent == null) {
                        text = String.format("%1$s @ %2$s", teamKey, eventKey);
                        break;
                    }
                    text = String.format("%1$s @ %2$d %3$s",
                      eTeam.getNickname(),
                      eEvent.getEventYear(),
                      eEvent.getEventShortName());
                    break;
                case DISTRICT:
                    District district = mDatafeed.fetchDistrict(key).toBlocking().first();
                    if (district == null) {
                        text = key;
                        break;
                    }
                    String districtName = DISTRICTS.fromAbbreviation(district.getAbbreviation())
                      .getName();
                    text = String.format("%1$d %2$s", district.getYear(), districtName);
                    break;
                default:
                    return null;
            }
            return new ModelListElement(text, key, type);
        } catch (BasicModel.FieldNotDefinedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
