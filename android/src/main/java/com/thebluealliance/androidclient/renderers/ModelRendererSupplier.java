package com.thebluealliance.androidclient.renderers;

import androidx.annotation.Nullable;

import com.thebluealliance.androidclient.types.ModelType;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A class that determines what the properer {@link ModelRenderer} is for a given {@link ModelType}
 */
@Singleton
public class ModelRendererSupplier {

    private final AwardRenderer mAwardRenderer;
    private final DistrictPointBreakdownRenderer mDistrictPointBreakdownRenderer;
    private final DistrictTeamRenderer mDistrictTeamRenderer;
    private final DistrictRenderer mDistrictRenderer;
    private final EventRenderer mEventRenderer;
    private final MatchRenderer mMatchRenderer;
    private final MediaRenderer mMediaRenderer;
    private final TeamRenderer mTeamRenderer;

    @Inject
    public ModelRendererSupplier(
      AwardRenderer mAwardRenderer,
      DistrictPointBreakdownRenderer mDistrictPointBreakdownRenderer,
      DistrictTeamRenderer mDistrictTeamRenderer,
      DistrictRenderer mDistrictRenderer,
      EventRenderer mEventRenderer,
      MatchRenderer mMatchRenderer,
      MediaRenderer mMediaRenderer,
      TeamRenderer mTeamRenderer) {
        this.mAwardRenderer = mAwardRenderer;
        this.mDistrictPointBreakdownRenderer = mDistrictPointBreakdownRenderer;
        this.mDistrictTeamRenderer = mDistrictTeamRenderer;
        this.mDistrictRenderer = mDistrictRenderer;
        this.mEventRenderer = mEventRenderer;
        this.mMatchRenderer = mMatchRenderer;
        this.mMediaRenderer = mMediaRenderer;
        this.mTeamRenderer = mTeamRenderer;
    }

    public @Nullable ModelRenderer getRendererForType(ModelType type) {
        switch (type) {
            default:
                return null;
            case AWARD:
                return mAwardRenderer;
            case DISTRICT:
                return mDistrictRenderer;
            case DISTRICTTEAM:
                return mDistrictTeamRenderer;
            case DISTRICTPOINTS:
                return mDistrictPointBreakdownRenderer;
            case EVENT:
                return mEventRenderer;
            case MATCH:
                return mMatchRenderer;
            case MEDIA:
                return mMediaRenderer;
            case TEAM:
                return mTeamRenderer;
        }
    }
}
