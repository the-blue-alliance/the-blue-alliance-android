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
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_contributor, null);

            TextView loginView = (TextView) view.findViewById(R.id.login);
            loginView.setText(login);

            ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
            Picasso.with(context).load(avatarUrl).into(avatar);
        }
        return view;
    }
}
