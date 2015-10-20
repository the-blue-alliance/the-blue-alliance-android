package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.settings.MyTBAModelSettingsActivity;
import com.thebluealliance.androidclient.helpers.ModelType;
import com.thebluealliance.androidclient.listeners.ModelClickListener;

/**
 * Created by Phil on 8/13/2014.
 */
public class ModelListElement extends ListElement {

    private String text;
    private String key;
    private ModelType.MODELS type;

    public ModelListElement(String text, String key, ModelType.MODELS type) {
        this.text = text;
        this.key = key;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public String getKey() {
        return key;
    }

    public ModelType.MODELS getType() {
        return type;
    }

    @Override
    public View getView(final Context context, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_model, null);

            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.settingsButton = (ImageView) convertView.findViewById(R.id.model_settings);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(text);

        holder.settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(MyTBAModelSettingsActivity.newInstance(context, key, type));
            }
        });

        if (!key.isEmpty()) {
            convertView.setOnClickListener(new ModelClickListener(context, key, type));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView text;
        ImageView settingsButton;
    }
}
