package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.ListItemNotificationScheduleUpdatedBinding;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.viewmodels.ScheduleUpdatedNotificationViewModel;

import io.nlopez.smartadapters.views.BindableFrameLayout;

public class ScheduleUpdatedNotificationItemView extends BindableFrameLayout<ScheduleUpdatedNotificationViewModel> {

    private ListItemNotificationScheduleUpdatedBinding mBinding;

    public ScheduleUpdatedNotificationItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_notification_schedule_updated;
    }

    @Override
    public void onViewInflated() {
        mBinding = ListItemNotificationScheduleUpdatedBinding.bind(this);
    }

    @Override
    public void bind(ScheduleUpdatedNotificationViewModel model) {
        mBinding.cardHeader.setText(model.getTitle());
        mBinding.details.setText(model.getDetails());
        mBinding.notificationTime.setText(model.getTimeString());
        mBinding.summaryContainer.setOnClickListener(new GamedayTickerClickListener(getContext(), model.getIntent()));
    }
}
