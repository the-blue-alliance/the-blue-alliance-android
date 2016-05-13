package com.thebluealliance.androidclient.itemviews;

import com.thebluealliance.androidclient.Interactions;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.ModelSettingsClickListener;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.viewmodels.EventViewModel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class EventItemView extends BindableFrameLayout<EventViewModel> {

    @Bind(R.id.event_name) TextView eventName;
    @Bind(R.id.event_dates) TextView eventDates;
    @Bind(R.id.event_location) TextView eventLocation;
    @Bind(R.id.model_settings) ImageView modelSettings;

    public EventItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_event;
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void bind(EventViewModel model) {
        this.setOnClickListener(v -> notifyItemAction(Interactions.EVENT_CLICKED));
        this.setClickable(true);
        this.setFocusable(true);

        boolean showMyTba = model.shouldShowMyTbaSettings();

        eventLocation.setText(model.getLocation());
        eventDates.setText(model.getDateString());

        /* When rendering in mytba list, show year with event name */
        if (showMyTba) {
            eventName.setText(String.format("%1$d %2$s", model.getYear(), model.getShortName()));
        } else {
            eventName.setText(model.getShortName());
        }

        if (showMyTba) {
            // When rendered in MyTba, add a specific click listener because we can't add
            // one to the parent ListView
            modelSettings.setOnClickListener(new ModelSettingsClickListener(getContext(), model.getKey(), ModelType.EVENT));
        }
        eventDates.setVisibility(showMyTba ? View.GONE : View.VISIBLE);
        modelSettings.setVisibility(showMyTba ? View.VISIBLE : View.GONE);
    }
}
