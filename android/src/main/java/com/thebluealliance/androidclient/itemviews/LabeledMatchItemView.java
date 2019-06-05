package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.viewmodels.LabeledMatchViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class LabeledMatchItemView extends BindableFrameLayout<LabeledMatchViewModel> {

    @BindView(R.id.label) TextView label;
    @BindView(R.id.match_container) FrameLayout matchContainer;

    public LabeledMatchItemView(Context context) {
        super(context);
    }

    @Override public int getLayoutId() {
        return R.layout.list_item_labeled_match;
    }

    @Override public void onViewInflated() {
        ButterKnife.bind(this);

        setLayoutParams(new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

    }

    @Override public void bind(LabeledMatchViewModel model) {
        label.setText(model.getLabel());
        matchContainer.removeAllViews();
        matchContainer.addView(model.getMatch().getView(getContext(), LayoutInflater.from(getContext()), null));
    }
}
