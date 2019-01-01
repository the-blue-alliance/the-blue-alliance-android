package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.viewmodels.ListSectionHeaderViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class ListSectionHeaderItemView extends BindableFrameLayout<ListSectionHeaderViewModel> {

    @Bind(R.id.event_type) TextView eventType;

    public ListSectionHeaderItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_event_type_header;
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override public void bind(ListSectionHeaderViewModel model) {
        eventType.setText(model.getTitle());
    }
}
