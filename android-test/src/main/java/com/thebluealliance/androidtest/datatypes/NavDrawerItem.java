package com.thebluealliance.androidtest.datatypes;

import android.view.LayoutInflater;
import android.view.View;

/**
 * File created by phil on 4/20/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of TBA Test.
 * TBA Test is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * TBA Test is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with TBA Test. If not, see http://www.gnu.org/licenses/.
 */
public class NavDrawerItem implements ListItem {

    private String title;
    private int icon;

    public NavDrawerItem(){

    }

    public NavDrawerItem(String title){
        this.title = title;
    }

    public NavDrawerItem(String title, int icon){
        this.title = title;
        this.icon = icon;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        return null;
    }

    @Override
    public void setSelected(boolean selected) {

    }
}
