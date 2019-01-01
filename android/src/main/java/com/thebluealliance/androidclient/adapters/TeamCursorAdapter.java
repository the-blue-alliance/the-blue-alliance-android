package com.thebluealliance.androidclient.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * A class that adapts a cursor retrieved from
 * {@link com.thebluealliance.androidclient.database.tables.TeamsTable#getForSearchQuery(String)}
 * and binds it to views
 *
 * WARNING: TERRIBLE HACKS WITHIN
 * For some reason, I was having trouble getting the returned cursor to get the right column index
 * from a column name (would always return -1/not found), so the indexes are just directly done
 */public class TeamCursorAdapter extends CursorAdapter {

    public String getKey(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        return c.getString(1);
    }

    public TeamCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_item_team, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int teamNumber = cursor.getInt(2);
        String teamName = cursor.getString(4);
        ((TextView) view.findViewById(R.id.team_number)).setText(cursor.getString(2));
        ((TextView) view.findViewById(R.id.team_name)).setText(teamName == null || teamName.isEmpty()
                                                               ? "Team " + teamNumber : teamName);
        ((TextView) view.findViewById(R.id.team_location)).setText(cursor.getString(5));
        view.findViewById(R.id.team_info).setVisibility(View.GONE);
    }
}
