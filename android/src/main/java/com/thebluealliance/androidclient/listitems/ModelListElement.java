package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.ModelClickListener;

/**
 * Created by Phil on 8/13/2014.
 */
public class ModelListElement extends ListElement {

    private String text;
    private String key;

    public ModelListElement(String text, String key) {
        this.text = text;
        this.key = key;
    }

    @Override
    public View getView(final Context context, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_model, null);

            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(text);

        if(!key.isEmpty()){
            convertView.setOnClickListener(new ModelClickListener(context, key));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView text;
    }
}
