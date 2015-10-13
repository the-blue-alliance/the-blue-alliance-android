package com.thebluealliance.androidclient.subscribers;

import android.content.Context;

import com.thebluealliance.androidclient.comparators.FavoriteSortByModelComparator;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Favorite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavoriteListSubscriber extends BaseAPISubscriber<List<Favorite>, List<ListItem>> {

    private Context mContext;
    private Comparator<Favorite> mComparator;

    public FavoriteListSubscriber(Context context) {
        mDataToBind = new ArrayList<>();
        mContext = context;
        mComparator = new FavoriteSortByModelComparator();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData == null) {
            return;
        }
        int lastModel = -1;
        Collections.sort(mAPIData, mComparator);
        for (int i = 0; i < mAPIData.size(); i++) {
            Favorite favorite = mAPIData.get(i);
            ListItem item = ModelHelper.renderModelFromKey(
              mContext,
              favorite.getModelKey(),
              favorite.getModelType());
            if (item != null) {
                if (lastModel != favorite.getModelEnum()) {
                    mDataToBind.add(new EventTypeHeader(favorite.getModelType().getTitle()));
                }
                mDataToBind.add(item);
            }
            lastModel = favorite.getModelEnum();
        }
    }
}
