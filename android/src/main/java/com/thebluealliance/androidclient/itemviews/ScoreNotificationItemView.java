package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.LayoutInflater;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.ListItemNotificationScoreBinding;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.viewmodels.ScoreNotificationViewModel;

import io.nlopez.smartadapters.views.BindableFrameLayout;

public class ScoreNotificationItemView extends BindableFrameLayout<ScoreNotificationViewModel> {
    private ListItemNotificationScoreBinding mBinding;

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
        mBinding = ListItemNotificationScoreBinding.bind(this);
    }

    @Override
    public void bind(ScoreNotificationViewModel model) {
        mBinding.cardHeader.setText(model.getHeader());
        mBinding.title.setText(model.getTitle());
        mBinding.notificationTime.setText(model.getNotificationTime());
        mBinding.summaryContainer.setOnClickListener(new GamedayTickerClickListener(getContext(), model.getIntent()));

        MatchListElement renderedMatch = mRenderer.renderFromModel(model.getMatch(), MatchRenderer.RENDER_NOTIFICATION);
        if (renderedMatch != null) {
            renderedMatch.getView(getContext(), LayoutInflater.from(getContext()), mBinding.matchDetails);
        }
    }
}
