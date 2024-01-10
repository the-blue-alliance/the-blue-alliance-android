package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.LayoutInflater;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.ListItemNotificationUpcomingMatchBinding;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.viewmodels.UpcomingMatchNotificationViewModel;

import io.nlopez.smartadapters.views.BindableFrameLayout;

public class UpcomingMatchNotificationItemView extends BindableFrameLayout<UpcomingMatchNotificationViewModel> {
    private ListItemNotificationUpcomingMatchBinding mBinding;

    public UpcomingMatchNotificationItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_notification_upcoming_match;
    }

    @Override
    public void onViewInflated() {
        mBinding = ListItemNotificationUpcomingMatchBinding.bind(this);
    }

    @Override
    public void bind(UpcomingMatchNotificationViewModel model) {
        mBinding.cardHeader.setText(model.getHeader());
        mBinding.title.setText(model.getTitle());
        mBinding.title.setText(model.getNotificationTime());
        mBinding.summaryContainer.setOnClickListener(new GamedayTickerClickListener(getContext(), model.getIntent()));

        new MatchListElement(model.getRedTeams(), model.getBlueTeams(), model.getMatchKey(), model.getMatchTime(), null).getView(getContext(), LayoutInflater.from(getContext()), mBinding.matchDetails);
    }
}
