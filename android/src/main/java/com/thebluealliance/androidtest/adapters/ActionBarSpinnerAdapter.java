package com.thebluealliance.androidtest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

import java.util.ArrayList;

/**
 * File created by phil on 4/21/14.
 */
public class ActionBarSpinnerAdapter extends BaseAdapter {

    Context context;
    int layoutResourceId;
    String[] data;
    LayoutInflater inflater;

    public ActionBarSpinnerAdapter(Context context, int textViewResourceId,String[] data) {
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.layoutResourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View actionBarView = inflater.inflate(R.layout.actionbar_spinner, null);
        TextView title = (TextView) actionBarView.findViewById(R.id.title);
        TextView subtitle = (TextView) actionBarView.findViewById(R.id.subtitle);
        title.setText(context.getResources().getString(R.string.tab_events));
        subtitle.setText(data[position]);
        return actionBarView;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View actionBarDropDownView = inflater.inflate(
                R.layout.actionbar_spinner_dropdown, null);
        TextView dropDownTitle = (TextView) actionBarDropDownView
                .findViewById(R.id.title);

        dropDownTitle.setText(data[position]);

        return actionBarDropDownView;

    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
