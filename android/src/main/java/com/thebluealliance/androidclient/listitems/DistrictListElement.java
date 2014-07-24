package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.listeners.DistrictClickListener;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;

/**
 * Created by phil on 7/24/14.
 */
public class DistrictListElement extends ListElement {

    private DistrictHelper.DISTRICTS type;
    private String key;

    public DistrictListElement(District district) throws BasicModel.FieldNotDefinedException {
        super(district.getKey());
        type = DistrictHelper.DISTRICTS.fromEnum(district.getEnum());
        key = district.getKey();
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_district, null);

            holder = new ViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(type.getName()+" District");

        convertView.setOnClickListener(new DistrictClickListener(c, key));

        return convertView;
    }

    private static class ViewHolder{
        TextView title;
    }
}
