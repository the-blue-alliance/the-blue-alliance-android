package com.thebluealliance.androidtest.datatypes;

import android.view.LayoutInflater;
import android.view.View;

import com.thebluealliance.androidtest.adapters.ListViewAdapter;

/**
 * File created by phil on 4/20/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of TBA Test.
 * TBA Test is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * TBA Test is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with TBA Test. If not, see http://www.gnu.org/licenses/.
 */
public abstract class ListHeader implements ListItem{
    private final String name;

    public ListHeader(){
        name = "";
    }

    public ListHeader(String name) {
        this.name = name;
    }

    @Override
    public int getViewType() {
        return ListViewAdapter.ItemType.HEADER_ITEM.ordinal();
    }

    public String getText(){
        return name;
    }

    @Override
    public abstract View getView(LayoutInflater inflater, View convertView);

    @Override
    public void setSelected(boolean selected) {

    }
}
