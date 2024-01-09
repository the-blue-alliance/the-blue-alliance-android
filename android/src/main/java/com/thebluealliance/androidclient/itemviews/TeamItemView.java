package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.Interactions;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.ListItemTeamBinding;
import com.thebluealliance.androidclient.listeners.ModelSettingsClickListener;
import com.thebluealliance.androidclient.listeners.TeamClickListener;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.viewmodels.TeamViewModel;

import io.nlopez.smartadapters.views.BindableRelativeLayout;

public class TeamItemView extends BindableRelativeLayout<TeamViewModel> {
    private ListItemTeamBinding mBinding;

    public TeamItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_team;
    }

    public void onViewInflated() {
        mBinding = ListItemTeamBinding.bind(this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void bind(TeamViewModel model) {
        this.setOnClickListener(v -> notifyItemAction(Interactions.TEAM_ITEM_CLICKED));
        this.setClickable(true);
        this.setFocusable(true);

        mBinding.teamNumber.setText(String.format("%1$d", model.getTeamNumber()));

        if (model.getTeamName() == null || model.getTeamName().isEmpty()) {
            mBinding.teamName.setText(String.format("Team %1$s", model.getTeamNumber()));
        } else {
            mBinding.teamName.setText(model.getTeamName());
        }

        mBinding.teamLocation.setText(model.getTeamLocation());

        if (model.shouldShowLinkToTeamDetails()) {
            mBinding.teamInfo.setVisibility(View.VISIBLE);
            mBinding.teamInfo.setOnClickListener(new TeamClickListener(getContext(), model.getTeamKey()));
        } else {
            mBinding.teamInfo.setVisibility(View.GONE);
        }

        if (model.shouldShowMyTbaDetails()) {
            mBinding.modelSettings.setVisibility(View.VISIBLE);
            mBinding.modelSettings.setOnClickListener(new ModelSettingsClickListener(getContext(), model.getTeamKey(), ModelType.TEAM));
        } else {
            mBinding.modelSettings.setVisibility(View.GONE);
        }
    }
}
