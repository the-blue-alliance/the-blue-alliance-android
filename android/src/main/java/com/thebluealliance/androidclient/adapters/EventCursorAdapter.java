package com.thebluealliance.androidclient.adapters;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.helpers.EventHelper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Date;

/**
 * A class that adapts a cursor retrieved from
 * {@link com.thebluealliance.androidclient.database.tables.EventsTable#getForSearchQuery(String)}
 * and binds it to views
 *
 * WARNING: TERRIBLE HACKS WITHIN
 * For some reason, I was having trouble getting the returned cursor to get the right column index
 * from a column name (would always return -1/not found), so the indexes are just directly done
 */
public class EventCursorAdapter extends CursorAdapter {

    public String getKey(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        return c.getString(1);
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
        try {
            startDate = new Date(cursor.getLong(5));
            endDate = new Date(cursor.getLong(6));
        } catch (Exception e) {
            // Oops.
        }
        dates.setText(EventHelper.getDateString(startDate, endDate));

        TextView name = (TextView) view.findViewById(R.id.event_name);
        name.setText(cursor.getString(3));

        TextView location = (TextView) view.findViewById(R.id.event_location);
        location.setText(cursor.getString(8));
    }
}
