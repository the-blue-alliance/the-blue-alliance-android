package com.thebluealliance.androidclient.subscribers;

import android.app.Activity;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Media;

import java.util.ArrayList;
import java.util.List;

public class MediaListSubscriber extends BaseAPISubscriber<List<Media>, List<ListGroup>> {

    private ListGroup mCdPhotos, mYtVideos;

    public MediaListSubscriber(Activity activity) {
        super();
        mCdPhotos = new ListGroup(activity.getString(R.string.cd_header));
        mYtVideos = new ListGroup(activity.getString(R.string.yt_header));
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException{
        mDataToBind.clear();
        mCdPhotos.clear();
        mYtVideos.clear();
        for (int i=0; i < mAPIData.size(); i++) {
            Media media = mAPIData.get(i);
            switch (media.getMediaType()) {
                case CD_PHOTO_THREAD:
                    mCdPhotos.children.add(media);
                    break;
                case YOUTUBE:
                    mYtVideos.children.add(media);
                    break;
            }
        }
        if (!mCdPhotos.children.isEmpty()) {
            mDataToBind.add(mCdPhotos);
        }
        if (!mYtVideos.children.isEmpty()) {
            mDataToBind.add(mYtVideos);
        }
    }
}
