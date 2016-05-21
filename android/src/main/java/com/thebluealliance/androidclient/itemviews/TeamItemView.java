package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Interactions;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.ModelSettingsClickListener;
import com.thebluealliance.androidclient.listeners.TeamClickListener;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.viewmodels.TeamViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class TeamItemView extends BindableFrameLayout<TeamViewModel> {

    @Bind(R.id.team_number) TextView teamNumber;
    @Bind(R.id.team_name) TextView teamName;
    @Bind(R.id.team_location) TextView teamLocation;
    @Bind(R.id.team_info) ImageView teamInfo;
    @Bind(R.id.model_settings) ImageView modelSettings;

    public TeamItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_team;
    }

    public void onViewInflated() {
        ButterKnife.bind(this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void bind(TeamViewModel model) {
        this.setOnClickListener(v -> notifyItemAction(Interactions.TEAM_ITEM_CLICKED));
        this.setClickable(true);
        this.setFocusable(true);

        teamNumber.setText(String.format("%1$d", model.getTeamNumber()));

        if (model.getTeamName().isEmpty()) {
            teamName.setText(String.format("Team %1$s", model.getTeamNumber()));
        } else {
            teamName.setText(model.getTeamName());
        }

        teamLocation.setText(model.getTeamLocation());

        if (model.shouldShowLinkToTeamDetails()) {
            teamInfo.setVisibility(View.VISIBLE);
            teamInfo.setOnClickListener(new TeamClickListener(getContext(), model.getTeamKey()));
        } else {
            teamInfo.setVisibility(View.GONE);
        }

        if (model.shouldShowMyTbaDetails()) {
            modelSettings.setVisibility(View.VISIBLE);
            modelSettings.setOnClickListener(new ModelSettingsClickListener(getContext(), model.getTeamKey(), ModelType.TEAM));
        } else {
            modelSettings.setVisibility(View.GONE);
        }
    }
}
