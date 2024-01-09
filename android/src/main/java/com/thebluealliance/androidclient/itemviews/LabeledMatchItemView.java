package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.ListItemLabeledMatchBinding;
import com.thebluealliance.androidclient.viewmodels.LabeledMatchViewModel;

import io.nlopez.smartadapters.views.BindableFrameLayout;

public class LabeledMatchItemView extends BindableFrameLayout<LabeledMatchViewModel> {

    private ListItemLabeledMatchBinding mBinding;

    public LabeledMatchItemView(Context context) {
        super(context);
    }

    @Override public int getLayoutId() {
        return R.layout.list_item_labeled_match;
    }

    @Override public void onViewInflated() {
        mBinding = ListItemLabeledMatchBinding.bind(this);
        setLayoutParams(new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

    }

    @Override public void bind(LabeledMatchViewModel model) {
        mBinding.label.setText(model.getLabel());
        mBinding.matchContainer.removeAllViews();
        mBinding.matchContainer.addView(model.getMatch().getView(getContext(), LayoutInflater.from(getContext()), null));
    }
}
