package com.thebluealliance.androidclient.binders;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.subscribers.DistrictPointsListSubscriber.Type;

import java.util.List;

import javax.inject.Inject;

public class DistrictPointsListBinder extends ListViewBinder {

    public TextView nonDistrictWarning;

    private final Resources mResources;

    @Inject
    public DistrictPointsListBinder(Resources resources) {
        mResources = resources;
    }

    @Override
    public void updateData(@Nullable List<ListItem> data) {
        super.updateData(data);
        if (data instanceof Type && !((Type)data).isDistrict) {
            nonDistrictWarning.setText(mResources.getString(R.string.warning_not_real_district));
            nonDistrictWarning.setVisibility(View.VISIBLE);
        } else {
            nonDistrictWarning.setVisibility(View.GONE);
        }
    }
}
