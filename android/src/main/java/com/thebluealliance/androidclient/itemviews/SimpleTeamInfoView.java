package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.viewmodels.SimpleTeamViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class SimpleTeamInfoView extends BindableFrameLayout<SimpleTeamViewModel> {

    @BindView(R.id.team_name)
    TextView teamName;
    @BindView(R.id.team_location)
    TextView teamLocation;
    @BindView(R.id.team_full_name_container)
    FrameLayout teamFullNameContainer;
    @BindView(R.id.team_motto_container)
    FrameLayout teamMottoContainer;
    @BindView(R.id.team_name_container)
    RelativeLayout teamNameContainer;

    public SimpleTeamInfoView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_simple_team;
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void bind(SimpleTeamViewModel model) {
        teamName.setText(model.getTeamNickname());
        teamLocation.setText(model.getTeamLocation());
        teamFullNameContainer.setVisibility(View.GONE);
        teamMottoContainer.setVisibility(View.GONE);
        teamNameContainer.setOnClickListener(v -> getContext().startActivity(ViewTeamActivity.newInstance(getContext(), model.getTeamKey(), model.getYear())));
    }
}
