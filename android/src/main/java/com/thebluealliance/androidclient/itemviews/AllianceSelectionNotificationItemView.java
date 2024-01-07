package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.ListItemNotificationAllianceSelectionBinding;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.viewmodels.AllianceSelectionNotificationViewModel;

import io.nlopez.smartadapters.views.BindableFrameLayout;

public class AllianceSelectionNotificationItemView extends BindableFrameLayout<AllianceSelectionNotificationViewModel> {

    private ListItemNotificationAllianceSelectionBinding mBinding;

    public AllianceSelectionNotificationItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_notification_alliance_selection;
    }

    @Override
    public void onViewInflated() {
        mBinding = ListItemNotificationAllianceSelectionBinding.bind(this);
    }

    @Override
    public void bind(AllianceSelectionNotificationViewModel model) {
        mBinding.cardHeader.setText(model.getTitle());
        mBinding.details.setText(getContext().getString(R.string.notification_alliances_updated_gameday_details));
        mBinding.notificationTime.setText(model.getTimeString());
        mBinding.summaryContainer.setOnClickListener(new GamedayTickerClickListener(getContext(), model.getIntent()));
    }
}
