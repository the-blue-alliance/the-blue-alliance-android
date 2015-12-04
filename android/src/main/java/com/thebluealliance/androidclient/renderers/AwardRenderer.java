package com.thebluealliance.androidclient.renderers;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.helpers.ModelType;
import com.thebluealliance.androidclient.listitems.AwardListElement;
import com.thebluealliance.androidclient.listitems.CardedAwardListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AwardRenderer implements ModelRenderer<Award, AwardRenderer.RenderArgs> {

   @Retention(RetentionPolicy.SOURCE)
   @IntDef({RENDER_CARDED, RENDER_NONCARDED})
   public @interface RenderType{}
    public static final int RENDER_CARDED = 0;
    public static final int RENDER_NONCARDED = 1;

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
        try {
            switch (args.renderType) {
                case RENDER_CARDED:
                    return new CardedAwardListElement(
                      mDatafeed,
                      award.getName(),
                      award.getEventKey(),
                      award.getWinners(),
                      args.teams,
                      args.selectedTeamKey);
                case RENDER_NONCARDED:
                    return new AwardListElement(mDatafeed, award.getName(), award.getWinners());
            }
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Unable to render award: " + award.getKey());
            e.printStackTrace();
        }
        return null;
    }

    public static class RenderArgs {
        public final @RenderType int renderType;
        public final Map<String, Team> teams;
        public final String selectedTeamKey;

        /**
         * Constructor to render old, non-carded element
         */
        public RenderArgs() {
            renderType = RENDER_NONCARDED;
            teams = null;
            selectedTeamKey = null;
        }

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
