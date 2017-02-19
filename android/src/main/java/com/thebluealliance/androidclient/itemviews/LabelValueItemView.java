package com.thebluealliance.androidclient.itemviews;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.viewmodels.LabelValueViewModel;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class LabelValueItemView extends BindableFrameLayout<LabelValueViewModel> {

    @Bind(R.id.label) TextView label;
    @Bind(R.id.value) TextView value;

    public LabelValueItemView(Context context) {
        super(context);
    }

    @Override public int getLayoutId() {
        return R.layout.list_item_summary;
    }

    @Override public void onViewInflated() {
        ButterKnife.bind(this);
    }

    @Override public void bind(LabelValueViewModel model) {
        label.setText(model.getLabel());
        value.setText(model.getValue());
        if (model.getBoldText()) {
            value.setTypeface(null, Typeface.BOLD);
        }
    }
}
