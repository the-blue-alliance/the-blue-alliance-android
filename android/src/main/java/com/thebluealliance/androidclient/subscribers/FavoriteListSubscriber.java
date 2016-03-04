package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.comparators.FavoriteSortByModelComparator;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.renderers.ModelRenderer;
import com.thebluealliance.androidclient.renderers.MyTbaModelRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavoriteListSubscriber extends BaseAPISubscriber<List<Favorite>, List<ListItem>> {

    private ModelRenderer mRenderer;
    private Comparator<Favorite> mComparator;

    public FavoriteListSubscriber(MyTbaModelRenderer renderer) {
        mDataToBind = new ArrayList<>();
        mRenderer = renderer;
        mComparator = new FavoriteSortByModelComparator();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        int lastModel = -1;
        Collections.sort(mAPIData, mComparator);
        for (int i = 0; i < mAPIData.size(); i++) {
            Favorite favorite = mAPIData.get(i);
            ListItem item = mRenderer.renderFromKey(favorite.getModelKey(), favorite.getModelType(), null);
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
