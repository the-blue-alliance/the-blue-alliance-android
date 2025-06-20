package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.listitems.DistrictListElement;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.types.ModelType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import thebluealliance.api.model.District;

@Singleton
public class DistrictRenderer implements ModelRenderer<District, DistrictRenderer.RenderArgs> {

    APICache mDatafeed;

    @Inject
    public DistrictRenderer(APICache datafeed) {
        mDatafeed = datafeed;
    }

    /**
     * Only {@code showMyTba} is preserved from args param
     */
    @WorkerThread
    @Override
    public @Nullable DistrictListElement renderFromKey(String key, ModelType type, RenderArgs args) {
        District district = mDatafeed.fetchDistrict(key).toBlocking().first();
        if (district == null) {
            return null;
        }
        List<Event> events = mDatafeed.fetchDistrictEvents(key).toBlocking().first();
        RenderArgs newArgs = new RenderArgs(events != null ? events.size() : 0, args != null && args.showMyTba);
        return renderFromModel(district, newArgs);
    }

    @WorkerThread
    @Override
    public @Nullable DistrictListElement renderFromModel(District district, RenderArgs args) {
        return new DistrictListElement(
                district,
                args != null ? args.numEvents : 0,
                args != null && args.showMyTba);
    }

    public static class RenderArgs {
        public final int numEvents;
        public final boolean showMyTba;

        public RenderArgs(int numEvents, boolean showMyTba) {
            this.numEvents = numEvents;
            this.showMyTba = showMyTba;
        }
    }
}
