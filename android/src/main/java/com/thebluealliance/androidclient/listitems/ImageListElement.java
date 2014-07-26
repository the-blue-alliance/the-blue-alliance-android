package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;
import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 5/31/14.
 */
public class ImageListElement extends ListElement {

    private String imageUrl, linkUrl;
    private Boolean isVideo;

    public ImageListElement(String imageUrl, String linkUrl, Boolean isVideo) {
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.isVideo = isVideo;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_image, null);
            holder = new ViewHolder();
            holder.image_item = (RelativeLayout) convertView.findViewById(R.id.image_item);
            holder.image_container = (FrameLayout) convertView.findViewById(R.id.image_container);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.youtube_play_icon = (ImageView) convertView.findViewById(R.id.youtube_play_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso picasso = Picasso.with(c);
        picasso.load(imageUrl).into(holder.image);
        if (isVideo) {
            holder.youtube_play_icon.setVisibility(View.VISIBLE);
        } else {
            holder.youtube_play_icon.setVisibility(View.GONE);
        }

        holder.image_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Track Click
                Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, c);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("media_click")
                        .setAction(linkUrl)
                        .setLabel(isVideo?"video":"cd_photo")
                        .build());

                c.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(linkUrl)));
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        RelativeLayout image_item;
        FrameLayout image_container;
        ImageView image;
        ImageView youtube_play_icon;
    }

}
