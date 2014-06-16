package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * Created by Nathan on 6/14/2014.
 */
public class EmptyListElement extends ListElement {

    private String text;

    public EmptyListElement(String text) {
        this.text = text;
    }

    @Override
    public View getView(final Context context, LayoutInflater inflater, View convertView) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_empty, null);

            ((TextView) view.findViewById(R.id.text)).setText(text);
        }
        return view;
    }
}
