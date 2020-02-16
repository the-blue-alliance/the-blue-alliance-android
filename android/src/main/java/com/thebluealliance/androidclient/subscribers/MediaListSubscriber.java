package com.thebluealliance.androidclient.subscribers;

import android.content.res.Resources;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.eventbus.TeamAvatarUpdateEvent;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.types.MediaType;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MediaListSubscriber extends BaseAPISubscriber<List<Media>, List<ListGroup>> {

    private ListGroup mPhotos, mVideos;
    private EventBus mEventBus;

    public MediaListSubscriber(Resources resources, EventBus eventBus) {
        super();
        mEventBus = eventBus;
        mPhotos = new ListGroup(resources.getString(R.string.media_images_header));
        mVideos = new ListGroup(resources.getString(R.string.media_videos_header));
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData() {
        mDataToBind.clear();
        mPhotos.clear();
        mVideos.clear();

        String encodedAvatar = "";

        for (int i=0; i < mAPIData.size(); i++) {
            Media media = mAPIData.get(i);
            MediaType mediaType = MediaType.fromString(media.getType());
            if (mediaType.isImage()) {
                mPhotos.children.add(media);
            } else if (mediaType.isVideo()) {
                mVideos.children.add(media);
            } else if (mediaType.isAvatar()) {
                encodedAvatar = media.getBase64Image();
            }
        }
        if (!mPhotos.children.isEmpty()) {
            mDataToBind.add(mPhotos);
        }
        if (!mVideos.children.isEmpty()) {
            mDataToBind.add(mVideos);
        }
        mEventBus.post(new TeamAvatarUpdateEvent(encodedAvatar));
    }

    @Override
    public boolean isDataValid() {
        return super.isDataValid() && !mAPIData.isEmpty();
    }
}
