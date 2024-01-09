package com.thebluealliance.androidclient.itemviews;

import android.content.Context;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.ListItemNotificationGenericBinding;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.viewmodels.GenericNotificationViewModel;

import io.nlopez.smartadapters.views.BindableFrameLayout;

public class GenericNotificationItemView extends BindableFrameLayout<GenericNotificationViewModel> {

    private ListItemNotificationGenericBinding mBinding;

    public GenericNotificationItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_notification_generic;
    }

    @Override
    public void onViewInflated() {
        mBinding = ListItemNotificationGenericBinding.bind(this);
    }

    @Override
    public void bind(GenericNotificationViewModel model) {
        mBinding.cardHeader.setText(model.getHeader());
        mBinding.title.setText(model.getTitle());
        mBinding.message.setText(model.getSummary());
        mBinding.notificationTime.setText(model.getTimeString());
        mBinding.summaryContainer.setOnClickListener(new GamedayTickerClickListener(getContext(), model.getIntent()));
    }
}
