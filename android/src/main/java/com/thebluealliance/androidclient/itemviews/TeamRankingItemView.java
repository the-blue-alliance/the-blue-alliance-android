package com.thebluealliance.androidclient.itemviews;

import com.thebluealliance.androidclient.Interactions;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.viewmodels.TeamRankingViewModel;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class TeamRankingItemView extends BindableFrameLayout<TeamRankingViewModel> {

    @Bind(R.id.team_number) TextView teamNumber;
    @Bind(R.id.team_rank) TextView teamRank;
    @Bind(R.id.team_record) TextView teamRecord;
    @Bind(R.id.team_name) TextView teamName;
    @Bind(R.id.ranking_breakdown) TextView rankingBreakdown;

    public TeamRankingItemView(Context context) {
        super(context);
    }

    @Override public int getLayoutId() {
        return R.layout.list_item_ranking;
    }

    @Override public void onViewInflated() {
        ButterKnife.bind(this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override public void bind(TeamRankingViewModel model) {
        this.setOnClickListener(v -> notifyItemAction(Interactions.TEAM_RANKING_CLICKED));
        this.setClickable(true);
        this.setFocusable(true);

        teamNumber.setText(String.valueOf(model.getTeamNumber()));
        teamRank.setText(String.format(getContext().getString(R.string.team_rank), model.getRank()));
        teamRecord.setText(model.getRecord());
        teamName.setText(model.getTeamNickname());
        rankingBreakdown.setText(model.getRankingBreakdown());
    }
}
