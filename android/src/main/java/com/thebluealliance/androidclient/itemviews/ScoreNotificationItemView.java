package com.thebluealliance.androidclient.itemviews;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.gcm.notifications.ScoreNotification;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.viewmodels.ScoreNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.UpcomingMatchNotificationViewModel;
import com.thebluealliance.androidclient.views.MatchView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class ScoreNotificationItemView extends BindableFrameLayout<ScoreNotificationViewModel> {
    @Bind(R.id.card_header) TextView header;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.match_details) MatchView matchView;
    @Bind(R.id.notification_time) TextView time;
    @Bind(R.id.summary_container) ViewGroup summaryContainer;

    public ScoreNotificationItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_notification_score;
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
    }

    @Override
    public void bind(ScoreNotificationViewModel model) {
        header.setText(model.getHeader());
        title.setText(model.getTitle());
        time.setText(model.getNotificationTime());
        summaryContainer.setOnClickListener(new GamedayTickerClickListener(getContext(), model.getIntent()));

        model.getMatch().render(false, false, false, false).getView(getContext(), LayoutInflater.from(getContext()), matchView);
    }
}
