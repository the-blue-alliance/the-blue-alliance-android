package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.comparators.SubscriptionSortByModelComparator;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Subscription;
import com.thebluealliance.androidclient.renderers.ModelRenderer;
import com.thebluealliance.androidclient.renderers.MyTbaModelRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SubscriptionListSubscriber
        extends BaseAPISubscriber<List<Subscription>, List<ListItem>> {

    private ModelRenderer mRenderer;
    private Comparator<Subscription> mComparator;

    public SubscriptionListSubscriber(MyTbaModelRenderer renderer) {
        mDataToBind = new ArrayList<>();
        mRenderer = renderer;
        mComparator = new SubscriptionSortByModelComparator();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData == null) {
            return;
        }

        Collections.sort(mAPIData, mComparator);
        int lastModel = -1;
        for (int i = 0; i < mAPIData.size(); i++) {
            Subscription subscription = mAPIData.get(i);
            ListItem item = mRenderer
                    .renderFromKey(subscription.getModelKey(), subscription.getModelType(), null);
            if (item != null) {
                if (lastModel != subscription.getModelEnum()) {
                    mDataToBind.add(new EventTypeHeader(subscription.getModelType().getTitle()));
                }
                mDataToBind.add(item);
            }
            lastModel = subscription.getModelEnum();
        }

    }
}
