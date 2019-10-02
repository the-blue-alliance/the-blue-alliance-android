package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.viewmodels.ScoreNotificationViewModel;
import com.thebluealliance.androidclient.views.MatchView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class ScoreNotificationItemView extends BindableFrameLayout<ScoreNotificationViewModel> {
    @BindView(R.id.card_header) TextView header;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.match_details) MatchView matchView;
    @BindView(R.id.notification_time) TextView time;
    @BindView(R.id.summary_container) ViewGroup summaryContainer;

    MatchRenderer mRenderer;

    public ScoreNotificationItemView(Context context) {
        super(context);
        mRenderer = new MatchRenderer(null, getResources());
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

        MatchListElement renderedMatch = mRenderer.renderFromModel(model.getMatch(), MatchRenderer.RENDER_NOTIFICATION);
        if (renderedMatch != null) {
            renderedMatch.getView(getContext(), LayoutInflater.from(getContext()), matchView);
        }
    }
}
