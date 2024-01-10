package com.thebluealliance.androidclient.itemviews;

import android.animation.ValueAnimator;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.thebluealliance.androidclient.Interactions;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.ListItemRankingBinding;
import com.thebluealliance.androidclient.viewmodels.TeamRankingViewModel;

import io.nlopez.smartadapters.views.BindableRelativeLayout;

public class TeamRankingItemView extends BindableRelativeLayout<TeamRankingViewModel> {
    private ListItemRankingBinding mBinding;
    private int originalHeight;
    private int expandedHeightDelta;
    private boolean isViewExpanded;

    public TeamRankingItemView(Context context) {
        super(context);
        originalHeight = 0;
        expandedHeightDelta = 0;
        isViewExpanded = false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_ranking;
    }

    @Override
    public void onViewInflated() {
        mBinding = ListItemRankingBinding.bind(this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Set expanding Views to View.GONE and .setEnabled(false)
        mBinding.rankingBreakdownContainer.setVisibility(View.GONE);
        mBinding.rankingBreakdownContainer.setEnabled(false);
    }

    @Override
    public void bind(TeamRankingViewModel model) {
        this.setOnClickListener(v -> notifyItemAction(Interactions.EXPAND_TEAM_RANKING));
        this.setClickable(true);
        this.setFocusable(true);

        mBinding.rankingDetailButton.setOnClickListener(v -> notifyItemAction(Interactions.TEAM_RANKING_CLICKED));
        mBinding.teamNumber.setText(String.valueOf(model.getTeamNumber()));
        mBinding.teamRank.setText(String.format(getContext().getString(R.string.team_rank), model.getRank()));
        mBinding.teamRecord.setText(model.getRecord());
        mBinding.teamName.setText(model.getTeamNickname());
        mBinding.rankingSummary.setText(model.getRankingSummary());
        mBinding.rankingBreakdown.setText(Html.fromHtml(model.getRankingBreakdown()));
    }

    public void toggleRankingsExpanded() {
        if (originalHeight == 0) {
            originalHeight = getHeight();
            mBinding.rankingBreakdownContainer.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

            expandedHeightDelta = mBinding.rankingBreakdownContainer.getMeasuredHeight();
        }
        ValueAnimator valueAnimator;
        if (!isViewExpanded) {
            mBinding.rankingBreakdownContainer.setVisibility(View.VISIBLE);
            mBinding.rankingBreakdownContainer.setEnabled(true);
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
                    mBinding.rankingBreakdownContainer.setVisibility(View.INVISIBLE);
                    mBinding.rankingBreakdownContainer.setEnabled(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            // Set the animation on the custom view
            mBinding.rankingBreakdownContainer.startAnimation(a);
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
