package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.helpers.DistrictType;
import com.thebluealliance.androidclient.listeners.DistrictClickListener;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;

public class DistrictListElement extends ListElement {

    public final DistrictType type;
    public final int numEvents;
    public final String key;

    public DistrictListElement(District district, int numEvents) throws BasicModel.FieldNotDefinedException {
        super(district.getKey());
        type = DistrictType.fromEnum(district.getEnum());
        key = district.getKey();
        this.numEvents = numEvents;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_district, null);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.events = (TextView) convertView.findViewById(R.id.num_events);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(type.getName() + " District");
        if (numEvents != -1) {
            holder.events.setVisibility(View.VISIBLE);
            holder.events.setText(numEvents + " Events");
        } else {
            holder.events.setVisibility(View.GONE);
        }

        convertView.setOnClickListener(new DistrictClickListener(c, key));

        return convertView;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DistrictListElement)) {
            return false;
        }
        DistrictListElement element = (DistrictListElement) o;
        return type == element.type
          && numEvents == element.numEvents
          && key.equals(element.key);
    }

    private static class ViewHolder {
        TextView title;
        TextView events;
    }
}
