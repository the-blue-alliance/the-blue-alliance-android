package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.viewmodels.ScheduleUpdatedNotificationViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class ScheduleUpdatedNotificationItemView extends BindableFrameLayout<ScheduleUpdatedNotificationViewModel> {
    @Bind(R.id.card_header) TextView header;
    @Bind(R.id.details) TextView details;
    @Bind(R.id.notification_time) TextView time;
    @Bind(R.id.summary_container) View summaryContainer;

    public ScheduleUpdatedNotificationItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_notification_schedule_updated;
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
    }

    @Override
    public void bind(ScheduleUpdatedNotificationViewModel model) {
        header.setText(model.getTitle());
        details.setText(model.getDetails());
        time.setText(model.getTimeString());
        summaryContainer.setOnClickListener(new GamedayTickerClickListener(getContext(), model.getIntent()));
    }
}
