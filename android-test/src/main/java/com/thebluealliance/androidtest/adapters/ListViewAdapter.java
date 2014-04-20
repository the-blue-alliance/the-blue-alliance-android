package com.thebluealliance.androidtest.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.thebluealliance.androidtest.datatypes.ListItem;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * File created by phil on 4/20/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of TBA Test.
 * TBA Test is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * TBA Test is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with TBA Test. If not, see http://www.gnu.org/licenses/.
 */
public class ListViewAdapter extends ArrayAdapter<ListItem> {
    private LayoutInflater mInflater;
    public ArrayList<ListItem> values;
    public ArrayList<String> keys;

    public enum ItemType{
        LIST_ITEM,HEADER_ITEM
    }

    public ListViewAdapter(Context context,ArrayList<ListItem> values, ArrayList<String> keys){
        super(context,android.R.layout.simple_list_item_1,values);
        this.values = values;
        this.keys = keys;
        mInflater = LayoutInflater.from(context);
    }

    public void removeAt(int index) {
        if (index >= 0) {
            values.remove(index);
            keys.remove(index);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = getItem(position).getView(mInflater, convertView);

        return v;
    }

    public String getKey(int position){
        return keys.get(position);
    }

    public void updateListData(){
        notifyDataSetChanged();
    }

}
