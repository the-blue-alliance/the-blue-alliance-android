package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.ListItemNotificationAwardsPostedBinding;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.viewmodels.AwardsPostedNotificationViewModel;

import io.nlopez.smartadapters.views.BindableFrameLayout;

public class AwardsPostedNotificationItemView extends BindableFrameLayout<AwardsPostedNotificationViewModel> {

    private ListItemNotificationAwardsPostedBinding mBinding;

    public AwardsPostedNotificationItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_notification_awards_posted;
    }

    @Override
    public void onViewInflated() {
        mBinding = ListItemNotificationAwardsPostedBinding.bind(this);
    }

    @Override
    public void bind(AwardsPostedNotificationViewModel model) {
        mBinding.cardHeader.setText(getContext().getString(R.string.gameday_ticker_event_title_format, EventHelper.shortName(model.getEventName()), EventHelper.getShortCodeForEventKey(model.getEventKey()).toUpperCase()));
        mBinding.details.setText(getContext().getString(R.string.notification_awards_updated_gameday_details));
        mBinding.notificationTime.setText(model.getTimeString());
        mBinding.summaryContainer.setOnClickListener(new GamedayTickerClickListener(getContext(), model.getIntent()));
    }
}
