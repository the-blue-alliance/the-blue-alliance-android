package com.thebluealliance.androidclient.binders;

import android.content.res.Resources;
import android.support.annotation.Nullable;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.viewmodels.EventViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

public class DistrictEventsBinder extends RecyclerViewBinder {

    private final EventBus mEventBus;
    private final Resources mResources;

    @Inject
    public DistrictEventsBinder(EventBus eventBus, Resources resources) {
        mEventBus = eventBus;
        mResources = resources;
    }

    @Override
    public void updateData(@Nullable List<Object> data) {
        super.updateData(data);
        // Get the first district in the list and use it to infer the action bar title
        // Because we don't want to rely on the key -> constant mappings
        if (isDataBound() && mList != null) {
            for (int i = 0; i < mList.size(); i++) {
                Object item = mList.get(i);
                if (item instanceof EventViewModel) {
                    EventViewModel viewModel = ((EventViewModel) item);
                    String district = viewModel.getDistrictString();
                    int year = viewModel.getYear();
                    if (district != null && !district.isEmpty()) {
                        String title = mResources.getString(R.string.district_title_format,
                                                            year,
                                                            district);
                        mEventBus.post(new ActionBarTitleEvent(title));
                        return;
                    }
                }
            }
        }
    }
}
