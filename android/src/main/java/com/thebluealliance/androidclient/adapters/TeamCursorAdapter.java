package com.thebluealliance.androidclient.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.database.Database;

/**
 * Created by Nathan on 6/15/2014.
 */
public class TeamCursorAdapter extends CursorAdapter {

    public String getKey(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        return c.getString(c.getColumnIndex(Database.Teams.KEY));
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
        int teamNumber = cursor.getInt(cursor.getColumnIndex(Database.Teams.NUMBER));
        String teamName = cursor.getString(cursor.getColumnIndex(Database.Teams.SHORTNAME));
        ((TextView) view.findViewById(R.id.team_number)).setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(Database.Teams.NUMBER))));
        ((TextView) view.findViewById(R.id.team_name)).setText(teamName.isEmpty() ? "Team " + teamNumber : teamName);
        ((TextView) view.findViewById(R.id.team_location)).setText(cursor.getString(cursor.getColumnIndex(Database.Teams.LOCATION)));
        view.findViewById(R.id.team_info).setVisibility(View.GONE);
    }
}
