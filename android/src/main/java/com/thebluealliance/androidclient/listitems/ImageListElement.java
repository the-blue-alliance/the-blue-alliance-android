package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 5/31/14.
 */
public class ImageListElement extends ListElement {

    private String imageUrl, linkUrl;

    public ImageListElement(String imageUrl, String linkUrl) {
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_image, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso picasso = Picasso.with(c);
        picasso.load(imageUrl).into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(linkUrl)));
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView image;
    }

}
