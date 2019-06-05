package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.viewmodels.UpcomingMatchNotificationViewModel;
import com.thebluealliance.androidclient.views.MatchView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class UpcomingMatchNotificationItemView extends BindableFrameLayout<UpcomingMatchNotificationViewModel> {
    @BindView(R.id.card_header) TextView header;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.match_details) MatchView matchView;
    @BindView(R.id.notification_time) TextView time;
    @BindView(R.id.summary_container) View summaryContainer;

    public UpcomingMatchNotificationItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_notification_upcoming_match;
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
    }

    @Override
    public void bind(UpcomingMatchNotificationViewModel model) {
        header.setText(model.getHeader());
        title.setText(model.getTitle());
        time.setText(model.getNotificationTime());
        summaryContainer.setOnClickListener(new GamedayTickerClickListener(getContext(), model.getIntent()));

        new MatchListElement(model.getRedTeams(), model.getBlueTeams(), model.getMatchKey(), model.getMatchTime(), null).getView(getContext(), LayoutInflater.from(getContext()), matchView);
    }
}
