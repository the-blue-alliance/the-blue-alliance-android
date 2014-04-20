package com.thebluealliance.androidtest.datatypes;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidtest.R;
import com.thebluealliance.androidtest.fragments.EventListFragment;

/**
 * File created by phil on 4/20/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of TBA Test.
 * TBA Test is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * TBA Test is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with TBA Test. If not, see http://www.gnu.org/licenses/.
 */
public class EventWeekHeader extends ListHeader {

    public EventWeekHeader(String title){
        super(title);
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        view = inflater.inflate(R.layout.event_week_header, null);

        TextView text = (TextView) view.findViewById(R.id.event_week_separator);
        text.setText(getText());

        return view;
    }
}
