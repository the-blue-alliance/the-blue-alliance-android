package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.viewmodels.GenericNotificationViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class GenericNotificationItemView extends BindableFrameLayout<GenericNotificationViewModel> {
    @Bind(R.id.card_header) TextView header;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.message) TextView message;
    @Bind(R.id.notification_time) TextView time;
    @Bind(R.id.summary_container) View summaryContainer;

    public GenericNotificationItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_notification_generic;
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
    }

    @Override
    public void bind(GenericNotificationViewModel model) {
        header.setText(model.getHeader());
        title.setText(model.getTitle());
        message.setText(model.getSummary());
        time.setText(model.getTimeString());
        summaryContainer.setOnClickListener(new GamedayTickerClickListener(getContext(), model.getIntent()));
    }
}
