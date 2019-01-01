package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.viewmodels.CompLevelStartingNotificationViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class CompLevelStartingNotificationItemView extends BindableFrameLayout<CompLevelStartingNotificationViewModel> {
    @Bind(R.id.card_header) TextView header;
    @Bind(R.id.title) TextView details;
    @Bind(R.id.notification_time) TextView time;
    @Bind(R.id.summary_container) View summaryContainer;

    public CompLevelStartingNotificationItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_notification_comp_level_starting;
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
    }

    @Override
    public void bind(CompLevelStartingNotificationViewModel model) {
        header.setText(model.getHeader());
        details.setText(model.getDetails());
        time.setText(model.getTimeString());
        summaryContainer.setOnClickListener(new GamedayTickerClickListener(getContext(), model.getIntent()));


    }
}
