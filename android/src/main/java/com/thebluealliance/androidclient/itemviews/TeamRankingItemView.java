package com.thebluealliance.androidclient.itemviews;

import com.thebluealliance.androidclient.Interactions;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.viewmodels.TeamRankingViewModel;

import android.animation.ValueAnimator;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class TeamRankingItemView extends BindableFrameLayout<TeamRankingViewModel> {

    @Bind(R.id.team_number) TextView teamNumber;
    @Bind(R.id.team_rank) TextView teamRank;
    @Bind(R.id.team_record) TextView teamRecord;
    @Bind(R.id.team_name) TextView teamName;
    @Bind(R.id.ranking_breakdown_container) LinearLayout breakdownContainer;
    @Bind(R.id.ranking_breakdown) TextView rankingBreakdown;
    @Bind(R.id.ranking_summary) TextView rankingSummary;
    @Bind(R.id.ranking_detail_button) Button rankingDetail;

    private int originalHeight;
    private int expandedHeightDelta;
    private boolean isViewExpanded;

    public TeamRankingItemView(Context context) {
        super(context);
        originalHeight = 0;
        expandedHeightDelta = 0;
        isViewExpanded = false;

        // Set expanding Views to View.GONE and .setEnabled(false)
        breakdownContainer.setVisibility(View.GONE);
        breakdownContainer.setEnabled(false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_ranking;
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void bind(TeamRankingViewModel model) {
        this.setOnClickListener(v -> notifyItemAction(Interactions.EXPAND_TEAM_RANKING));
        this.setClickable(true);
        this.setFocusable(true);

        rankingDetail.setOnClickListener(v -> notifyItemAction(Interactions.TEAM_RANKING_CLICKED));
        teamNumber.setText(String.valueOf(model.getTeamNumber()));
        teamRank.setText(String.format(getContext().getString(R.string.team_rank), model.getRank()));
        teamRecord.setText(model.getRecord());
        teamName.setText(model.getTeamNickname());
        rankingSummary.setText(model.getRankingSummary());
        rankingBreakdown.setText(Html.fromHtml(model.getRankingBreakdown()));
    }

    public void toggleRankingsExpanded() {
        if (originalHeight == 0) {
            originalHeight = getHeight();
            breakdownContainer.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

            expandedHeightDelta = breakdownContainer.getMeasuredHeight();
        }
        ValueAnimator valueAnimator;
        if (!isViewExpanded) {
            breakdownContainer.setVisibility(View.VISIBLE);
            breakdownContainer.setEnabled(true);
            isViewExpanded = true;
            valueAnimator = ValueAnimator.ofInt(originalHeight, originalHeight + expandedHeightDelta);
        } else {
            isViewExpanded = false;
            valueAnimator = ValueAnimator.ofInt(originalHeight + expandedHeightDelta, originalHeight);

            Animation a = new AlphaAnimation(1.00f, 0.00f); // Fade out

            a.setDuration(200);
            // Set a listener to the animation and configure onAnimationEnd
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    breakdownContainer.setVisibility(View.INVISIBLE);
                    breakdownContainer.setEnabled(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            // Set the animation on the custom view
            breakdownContainer.startAnimation(a);
        }
        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            getLayoutParams().height = (Integer) animation.getAnimatedValue();
            requestLayout();
        });
        valueAnimator.start();
    }
}
