package com.thebluealliance.androidclient.itemviews;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.Interactions;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.ListItemEventBinding;
import com.thebluealliance.androidclient.listeners.ModelSettingsClickListener;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.viewmodels.EventViewModel;

import io.nlopez.smartadapters.views.BindableRelativeLayout;

public class EventItemView extends BindableRelativeLayout<EventViewModel> {

    private ListItemEventBinding mBinding;

    public EventItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_event;
    }

    @Override
    public void onViewInflated() {
        mBinding = ListItemEventBinding.bind(this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void bind(EventViewModel model) {
        this.setOnClickListener(v -> notifyItemAction(Interactions.EVENT_CLICKED));
        this.setClickable(true);
        this.setFocusable(true);

        boolean showMyTba = model.shouldShowMyTbaSettings();

        mBinding.eventLocation.setText(model.getLocation());
        mBinding.eventDates.setText(model.getDateString());

        /* When rendering in mytba list, show year with event name */
        if (showMyTba) {
            mBinding.eventName.setText(String.format("%1$d %2$s", model.getYear(), model.getShortName()));
        } else {
            mBinding.eventName.setText(model.getShortName());
        }

        if (showMyTba) {
            // When rendered in MyTba, add a specific click listener because we can't add
            // one to the parent ListView
            mBinding.modelSettings.setOnClickListener(new ModelSettingsClickListener(getContext(), model.getKey(), ModelType.EVENT));
        }
        mBinding.eventDates.setVisibility(showMyTba ? View.GONE : View.VISIBLE);
        mBinding.modelSettings.setVisibility(showMyTba ? View.VISIBLE : View.GONE);
    }
}
