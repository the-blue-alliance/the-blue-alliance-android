package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thebluealliance.androidclient.R;

public class ContributorListElement extends ListElement {

    private String username;
    private int contributionCount;
    private String avatarUrl;

    public ContributorListElement(String username, int contributionCount, String avatarUrl) {
        super(username);
        this.username = username;
        this.contributionCount = contributionCount;
        this.avatarUrl = avatarUrl;
    }

    @Override
    public View getView(final Context context, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_contributor, null);

            holder = new ViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.contributionCount = (TextView) convertView.findViewById(R.id.contribution_count);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.username.setText(username);
        holder.contributionCount.setText(context.getResources().getQuantityString(R.plurals
                .contribution_count, contributionCount, contributionCount));
        Picasso.with(context).load(avatarUrl).into(holder.avatar);

        return convertView;
    }

    private static class ViewHolder {
        TextView username;
        TextView contributionCount;
        ImageView avatar;
    }
}
