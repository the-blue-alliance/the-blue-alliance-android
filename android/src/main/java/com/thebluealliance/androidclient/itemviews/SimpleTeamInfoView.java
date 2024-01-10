package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.View;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.databinding.ListItemSimpleTeamBinding;
import com.thebluealliance.androidclient.viewmodels.SimpleTeamViewModel;

import io.nlopez.smartadapters.views.BindableFrameLayout;

public class SimpleTeamInfoView extends BindableFrameLayout<SimpleTeamViewModel> {
    private ListItemSimpleTeamBinding mBinding;

    public SimpleTeamInfoView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_simple_team;
    }

    @Override
    public void onViewInflated() {
        mBinding = ListItemSimpleTeamBinding.bind(this);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void bind(SimpleTeamViewModel model) {
        mBinding.teamName.setText(model.getTeamNickname());
        mBinding.teamLocation.setText(model.getTeamLocation());
        mBinding.teamFullNameContainer.setVisibility(View.GONE);
        mBinding.teamMottoContainer.setVisibility(View.GONE);
        mBinding.teamNameContainer.setOnClickListener(v -> getContext().startActivity(ViewTeamActivity.newInstance(getContext(), model.getTeamKey(), model.getYear())));
    }
}
