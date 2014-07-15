package com.thebluealliance.androidclient.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.helpers.EventHelper;

import java.util.Date;

/**
 * Created by Nathan on 6/15/2014.
 */
public class EventCursorAdapter extends CursorAdapter {

    public String getKey(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        return c.getString(c.getColumnIndex(Database.Events.KEY));
    }

    public EventCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_item_event, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView dates = (TextView) view.findViewById(R.id.event_dates);
        Date startDate = null, endDate = null;
        Log.d(Constants.LOG_TAG, "Start: " + cursor.getString(cursor.getColumnIndex(Database.Events.START)));
        try {
            startDate = new Date(cursor.getLong(cursor.getColumnIndex(Database.Events.START)));
            endDate = new Date(cursor.getLong(cursor.getColumnIndex(Database.Events.END)));
        } catch (Exception e) {
            // Oops.
        }
        dates.setText(EventHelper.getDateString(startDate, endDate));

        TextView name = (TextView) view.findViewById(R.id.event_name);
        name.setText(cursor.getString(cursor.getColumnIndex(Database.Events.NAME)));

        TextView location = (TextView) view.findViewById(R.id.event_location);
        location.setText(cursor.getString(cursor.getColumnIndex(Database.Events.LOCATION)));
    }
}
