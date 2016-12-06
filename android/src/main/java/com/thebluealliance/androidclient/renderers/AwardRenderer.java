package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.listitems.CardedAwardListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.types.ModelType;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AwardRenderer implements ModelRenderer<Award, AwardRenderer.RenderArgs> {

   @Retention(RetentionPolicy.SOURCE)
   @IntDef({RENDER_CARDED})
   public @interface RenderType{}
    public static final int RENDER_CARDED = 0;

    private APICache mDatafeed;

    @Inject
    public AwardRenderer(APICache datafeed) {
        mDatafeed = datafeed;
    }

    @Override
    public @Nullable ListElement renderFromKey(String key, ModelType type, RenderArgs args) {
        return null;
    }

    @Override
    public @Nullable ListElement renderFromModel(Award award, RenderArgs args) {
        switch (args.renderType) {
            case RENDER_CARDED:
                return new CardedAwardListElement(
                        mDatafeed,
                        award.getName(),
                        award.getEventKey(),
                        award.getWinners(),
                        args.teams,
                        args.selectedTeamKey);
        }
        return null;
    }

    public static class RenderArgs {
        public final @RenderType int renderType;
        public final Map<String, Team> teams;
        public final String selectedTeamKey;

        /**
         * Constructor to render carded element
         */
        public RenderArgs(Map<String, Team> teams, String selectedTeamKey) {
            renderType = RENDER_CARDED;
            this.teams = teams;
            this.selectedTeamKey = selectedTeamKey;
        }
    }
}
