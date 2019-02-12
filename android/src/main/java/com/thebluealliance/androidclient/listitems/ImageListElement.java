package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;

public class ImageListElement extends ListElement {

    public final String imageUrl, linkUrl;
    public final Boolean isVideo;

    public ImageListElement(String imageUrl, String linkUrl, Boolean isVideo) {
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.isVideo = isVideo;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ImageListElement)) {
            return false;
        }
        ImageListElement element = (ImageListElement) o;
        return imageUrl.equals(element.imageUrl)
          && linkUrl.equals(element.linkUrl)
          && isVideo == element.isVideo;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_image, null);
            holder = new ViewHolder();
            holder.imageItem = (RelativeLayout) convertView.findViewById(R.id.image_item);
            holder.imageContainer = (FrameLayout) convertView.findViewById(R.id.image_container);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.youtubePlayIcon = (ImageView) convertView.findViewById(R.id.youtube_play_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (imageUrl != null) {
            Picasso picasso = Picasso.with(c);
            picasso.load(imageUrl).into(holder.image);
        }

        if (isVideo) {
            holder.youtubePlayIcon.setVisibility(View.VISIBLE);
        } else {
            holder.youtubePlayIcon.setVisibility(View.GONE);
        }

        holder.imageItem.setOnClickListener(v -> {
            //Track Click
            AnalyticsHelper.sendSocialUpdate(c, isVideo ? "youtube" : "cd", linkUrl);
            c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl)));
        });

        return convertView;
    }

    private static class ViewHolder {
        RelativeLayout imageItem;
        FrameLayout imageContainer;
        ImageView image;
        ImageView youtubePlayIcon;
    }

}
