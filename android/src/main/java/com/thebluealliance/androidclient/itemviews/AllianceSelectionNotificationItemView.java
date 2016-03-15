package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.viewmodels.AllianceSelectionNotificationViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class AllianceSelectionNotificationItemView extends BindableFrameLayout<AllianceSelectionNotificationViewModel> {
    @Bind(R.id.card_header) TextView header;
    @Bind(R.id.details) TextView details;
    @Bind(R.id.notification_time) TextView time;

    public AllianceSelectionNotificationItemView(Context context) {
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
    public void bind(AllianceSelectionNotificationViewModel model) {
        header.setText(model.getTitle());
        details.setText(getContext().getString(R.string.notification_alliances_updated_gameday_details));
        time.setText(model.getTimeString());

    }
}
