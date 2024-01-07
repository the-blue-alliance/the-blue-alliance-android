package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.ListItemNotificationCompLevelStartingBinding;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.viewmodels.CompLevelStartingNotificationViewModel;

import io.nlopez.smartadapters.views.BindableFrameLayout;

public class CompLevelStartingNotificationItemView extends BindableFrameLayout<CompLevelStartingNotificationViewModel> {
    private ListItemNotificationCompLevelStartingBinding mBinding;

    public CompLevelStartingNotificationItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_notification_comp_level_starting;
    }

    @Override
    public void onViewInflated() {
        mBinding = ListItemNotificationCompLevelStartingBinding.bind(this);
    }

    @Override
    public void bind(CompLevelStartingNotificationViewModel model) {
        mBinding.cardHeader.setText(model.getHeader());
        mBinding.title.setText(model.getDetails());
        mBinding.notificationTime.setText(model.getTimeString());
        mBinding.summaryContainer.setOnClickListener(new GamedayTickerClickListener(getContext(), model.getIntent()));
    }
}
