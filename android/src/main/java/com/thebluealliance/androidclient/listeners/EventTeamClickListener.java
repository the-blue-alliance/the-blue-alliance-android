package com.thebluealliance.androidclient.listeners;

import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.listitems.ListElement;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import com.thebluealliance.androidclient.TbaLogger;
import android.view.View;
import android.widget.AdapterView;

/**
 * General click handler for Team@Event items. Can be used with both long and short clicks on both
 * individual views and AdapterView items.
 *
 * To be used as a simple View click listener, you have several options:
 * <ul>
 * <li>Pass a valid EventTeam key in the constructor</li>
 * <li>Pass both an event key and a team key in the constructor</li>
 * <li>Pass just an event key in the constructor, and use a team key as the view's tag</li>
 * <li>Use an EventTeam key as the view's tag</li>
 * </ul>
 *
 * To be used as an AdapterView item click listener, the AdapterView's adapter should be an
 * instance of {@link ListViewAdapter} and the item at the clicked position should be an instance
 * of
 * {@link ListElement}. The element's key can be either a Team key (such as frc254) or an EventTeam
 * key (such as 2016cmp_frc254). If the key is just a team key, a valid event key should be
 * provided in the constructor.
 */
public class EventTeamClickListener implements View.OnClickListener, View.OnLongClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private Context mContext;
    private String mEventKey, mTeamKey;

    public EventTeamClickListener(@NonNull Context c) {
        super();
        mContext = c;
        mEventKey = null;
        mTeamKey = null;
    }

    public EventTeamClickListener(@NonNull Context c, @NonNull String eventTeamKey) {
        super();
        mContext = c;
        if (EventTeamHelper.validateEventTeamKey(eventTeamKey)) {
            mEventKey = EventTeamHelper.getEventKey(eventTeamKey);
            mTeamKey = EventTeamHelper.getTeamKey(eventTeamKey);
        } else {
            TbaLogger.w("EventTeamClickListener created with invalid EventTeam key! " + eventTeamKey);
            mEventKey = null;
            mTeamKey = null;
        }
    }

    public EventTeamClickListener(@NonNull Context c, @NonNull String eventKey, @Nullable String teamKey) {
        super();
        mContext = c;
        mEventKey = eventKey;
        mTeamKey = teamKey;
    }

    @Override
    public void onClick(View v) {
        String[] keys = getKeysFromTag(v.getTag());
        String eventKey = keys[0];
        String teamKey = keys[1];

        handleClick(eventKey, teamKey);
    }

    @Override public boolean onLongClick(View v) {
        String[] keys = getKeysFromTag(v.getTag());
        String eventKey = keys[0];
        String teamKey = keys[1];

        return handleLongClick(eventKey, teamKey);
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Attempt to get team key out of the adapter view
        String[] keys = getKeysFromTag(getKeyFromAdapterView(parent, position));
        String eventKey = keys[0];
        String teamKey = keys[1];

        handleClick(eventKey, teamKey);
    }

    @Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String[] keys = getKeysFromTag(getKeyFromAdapterView(parent, position));
        String eventKey = keys[0];
        String teamKey = keys[1];

        return handleLongClick(eventKey, teamKey);
    }

    private void handleClick(String eventKey, String teamKey) {
        if (TeamHelper.validateTeamKey(teamKey) ^ TeamHelper.validateMultiTeamKey(teamKey)) {
            teamKey = TeamHelper.baseTeamKey(teamKey);
            Intent intent;
            if (EventHelper.validateEventKey(eventKey)) {
                intent = TeamAtEventActivity.newInstance(mContext, eventKey, teamKey);
            } else {
                intent = ViewTeamActivity.newInstance(mContext, teamKey);
            }
            AnalyticsHelper.sendClickUpdate(mContext, "team@event_click", EventTeamHelper.generateKey(eventKey, teamKey), "");
            mContext.startActivity(intent);
        }
    }

    private boolean handleLongClick(String eventKey, String teamKey) {
        boolean didHandleClick = false;
        if (TeamHelper.validateTeamKey(teamKey) ^ TeamHelper.validateMultiTeamKey(teamKey)) {
            teamKey = TeamHelper.baseTeamKey(teamKey);
            if (EventHelper.validateEventKey(eventKey)) {
                showViewEventTeamDialog(eventKey, teamKey);
                didHandleClick = true;
            }
            AnalyticsHelper.sendClickUpdate(mContext, "team@event_longclick", EventTeamHelper.generateKey(eventKey, teamKey), "");
        }

        return didHandleClick;
    }

    String[] getKeysFromTag(Object tagObject) {
        String eventKey = null, teamKey = null;

        if (tagObject != null) {
            String tag = tagObject.toString();
            if (EventTeamHelper.validateEventTeamKey(tag)) {
                eventKey = EventTeamHelper.getEventKey(tag);
                teamKey = EventTeamHelper.getTeamKey(tag);
            } else if (TeamHelper.validateTeamKey(tag) || TeamHelper.validateMultiTeamKey(tag)) {
                eventKey = mEventKey;
                teamKey = tag;
            } else {
                TbaLogger.w("EventTeamClickListener received invalid EventTeam Key! " + tag);
                // Use defaults provided in constructor
                eventKey = mEventKey;
                teamKey = mTeamKey;
            }
        } else {
            eventKey = mEventKey;
            teamKey = mTeamKey;
        }

        return new String[]{eventKey, teamKey};
    }

    private String getKeyFromAdapterView(AdapterView adapterView, int position) {
        if (adapterView.getAdapter() instanceof ListViewAdapter) {
            ListViewAdapter adapter = (ListViewAdapter) adapterView.getAdapter();
            if (adapter.getItem(position) instanceof ListElement) {
                ListElement element = (ListElement) adapter.getItem(position);
                return element.getKey();
            }
        }
        return "";
    }

    private void showViewEventTeamDialog(String eventKey, String teamKey) {
        final String[] items = new String[]{"View Team@Event", "View Team"};
        new AlertDialog.Builder(mContext)
                .setItems(items, (dialog, which) -> {
                    if (which == 0) {
                        // show team@event
                        mContext.startActivity(TeamAtEventActivity.newInstance(mContext, eventKey, teamKey));
                    } else {
                        // show team
                        mContext.startActivity(ViewTeamActivity.newInstance(mContext, teamKey));
                    }
                }).show();
    }
}
