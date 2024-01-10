package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.LinearLayout;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.ListItemSummaryBinding;
import com.thebluealliance.androidclient.viewmodels.LabelValueViewModel;

import io.nlopez.smartadapters.views.BindableLinearLayout;

public class LabelValueItemView extends BindableLinearLayout<LabelValueViewModel> {

    private ListItemSummaryBinding mBinding;

    public LabelValueItemView(Context context) {
        super(context);
    }

    @Override public int getLayoutId() {
        return R.layout.list_item_summary;
    }

    @Override public void onViewInflated() {
        mBinding = ListItemSummaryBinding.bind(this);
    }

    @Override public void bind(LabelValueViewModel model) {
        mBinding.label.setText(model.getLabel());
        mBinding.value.setText(model.getValue());
        if (model.getBoldText()) {
            mBinding.value.setTypeface(null, Typeface.BOLD);
        }
    }

    @Override
    public int getOrientation() {
        return LinearLayout.VERTICAL;
    }
}
