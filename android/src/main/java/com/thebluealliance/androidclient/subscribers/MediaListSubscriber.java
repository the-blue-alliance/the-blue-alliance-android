package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.types.MediaType;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

public class MediaListSubscriber extends BaseAPISubscriber<List<Media>, List<ListGroup>> {

    private ListGroup mPhotos, mVideos;

    public MediaListSubscriber(Resources resources) {
        super();
        mPhotos = new ListGroup(resources.getString(R.string.media_images_header));
        mVideos = new ListGroup(resources.getString(R.string.media_videos_header));
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData() {
        mDataToBind.clear();
        mPhotos.clear();
        mVideos.clear();

        for (int i=0; i < mAPIData.size(); i++) {
            Media media = mAPIData.get(i);
            MediaType mediaType = MediaType.fromString(media.getType());
            if (mediaType.isImage()) {
                mPhotos.children.add(media);
            } else if (mediaType.isVideo()) {
                mVideos.children.add(media);
            }
        }
        if (!mPhotos.children.isEmpty()) {
            mDataToBind.add(mPhotos);
        }
        if (!mVideos.children.isEmpty()) {
            mDataToBind.add(mVideos);
        }
    }

    @Override
    public boolean isDataValid() {
        return super.isDataValid() && !mAPIData.isEmpty();
    }
}
