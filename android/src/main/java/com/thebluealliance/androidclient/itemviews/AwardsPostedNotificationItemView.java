package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.viewmodels.AllianceSelectionNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.AwardsPostedNotificationViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class AwardsPostedNotificationItemView extends BindableFrameLayout<AwardsPostedNotificationViewModel> {
    @Bind(R.id.card_header)
    TextView header;

    @Bind(R.id.details)
    TextView details;

    @Bind(R.id.notification_time)
    TextView time;

    @Bind(R.id.summary_container)
    View summaryContainer;

    public AwardsPostedNotificationItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_notification_alliance_selection;
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
    }

    @Override
    public void bind(AwardsPostedNotificationViewModel model) {
        header.setText(getContext().getString(R.string.gameday_ticker_event_title_format, EventHelper.shortName(model.getEventName()), EventHelper.getShortCodeForEventKey(model.getEventKey()).toUpperCase()));
        details.setText(getContext().getString(R.string.notification_awards_updated_gameday_details));
        time.setText(model.getTimeString());
        summaryContainer.setOnClickListener(new GamedayTickerClickListener(getContext(), model.getIntent()));
    }
}
