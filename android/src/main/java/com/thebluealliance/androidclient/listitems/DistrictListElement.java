package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.DistrictClickListener;
import com.thebluealliance.androidclient.listeners.ModelSettingsClickListener;
import com.thebluealliance.androidclient.types.ModelType;

import thebluealliance.api.model.District;

public class DistrictListElement extends ListElement {

    public final int numEvents;
    public final String key;
    public final String name;
    public final int year;
    public final boolean showMyTba;

    public DistrictListElement(District district, int numEvents, boolean showMyTba)
       {
        super(district.getKey());
        key = district.getKey();
        year = district.getYear();
        name = district.getDisplayName();
        this.numEvents = numEvents;
        this.showMyTba = showMyTba;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_district, null);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.events = (TextView) convertView.findViewById(R.id.num_events);
            holder.myTbaSettings = (ImageView) convertView.findViewById(R.id.model_settings);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        /* For mytba list, show the year as well */
        if (showMyTba) {
            holder.title.setText(String.format("%1$d %2$s District", year, name));
        } else {
            holder.title.setText(String.format("%1$s District", name));
        }
        if (numEvents != -1) {
            holder.events.setVisibility(View.VISIBLE);
            holder.events.setText(String.format("%1$d Events", numEvents));
        } else {
            holder.events.setVisibility(View.GONE);
        }

        convertView.setOnClickListener(new DistrictClickListener(c, key));

        holder.myTbaSettings.setVisibility(showMyTba ? View.VISIBLE : View.GONE);
        if (showMyTba) {
            holder.myTbaSettings.setOnClickListener(new ModelSettingsClickListener(c, key, ModelType.DISTRICT));
            holder.title.setTextAppearance(c, R.style.normalText);
        }

        return convertView;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DistrictListElement)) {
            return false;
        }
        DistrictListElement element = (DistrictListElement) o;
        return numEvents == element.numEvents
          && key.equals(element.key);
    }

    private static class ViewHolder {
        TextView title;
        TextView events;
        ImageView myTbaSettings;
    }
}
