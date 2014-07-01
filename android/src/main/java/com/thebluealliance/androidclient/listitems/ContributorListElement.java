package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thebluealliance.androidclient.R;

/**
 * Created by Nathan on 6/20/2014.
 */
public class ContributorListElement extends ListElement {

    private String login;
    private String avatarUrl;

    public ContributorListElement(String login, String avatarUrl) {
        super(login);
        this.login = login;
        this.avatarUrl = avatarUrl;
    }

    @Override
    public View getView(final Context context, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_contributor, null);

            holder = new ViewHolder();
            holder.login = (TextView) convertView.findViewById(R.id.login);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.login.setText(login);

        Picasso.with(context).load(avatarUrl).into(holder.avatar);

        return convertView;
    }

    private static class ViewHolder {
        TextView login;
        ImageView avatar;
    }
}
