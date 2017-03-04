package com.thebluealliance.androidclient.binders;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.androidclient.views.breakdowns.AbstractMatchBreakdownView;
import com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownView2015;
import com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownView2016;
import com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownView2017;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MatchBreakdownBinder extends AbstractDataBinder<MatchBreakdownBinder.Model> {

    @Bind(R.id.match_breakdown) FrameLayout breakdownContainer;
    @Bind(R.id.progress) ProgressBar progressBar;

    @Override
    public void updateData(@Nullable MatchBreakdownBinder.Model data) {
        if (data == null || data.allianceData == null || data.scoreData == null
                || breakdownContainer == null) {
            if (!isDataBound()) {
                setDataBound(false);
            }
            return;
        }
        long startTime = System.currentTimeMillis();
        TbaLogger.d("BINDING DATA");
        AbstractMatchBreakdownView breakdownView;
        switch (data.year) {
            case 2015:
                breakdownView = new MatchBreakdownView2015(mActivity);
                break;
            case 2016:
                breakdownView = new MatchBreakdownView2016(mActivity);
                break;
            case 2017:
                breakdownView = new MatchBreakdownView2017(mActivity);
                break;
            default:
                breakdownView = null;
                break;
        }

        if (breakdownView == null) {
            setDataBound(false);
            return;
        }

        breakdownContainer.addView(breakdownView);
        boolean success = breakdownView.initWithData(data.matchType,
                                                     data.winningAlliance,
                                                     data.allianceData,
                                                     data.scoreData);

        if (!success) {
            setDataBound(false);
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        breakdownContainer.setVisibility(View.VISIBLE);
        mNoDataBinder.unbindData();
        TbaLogger.d("BINDING COMPLETE; ELAPSED TIME: " + (System.currentTimeMillis() - startTime) + "ms");
        setDataBound(true);
    }

    @Override
    public void onComplete() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (!isDataBound()) {
            bindNoDataView();
        }
    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this, mRootView);
    }

    @Override
    public void onError(Throwable throwable) {
        TbaLogger.e(TbaLogger.getStackTraceString(throwable));

        // If we received valid data from the cache but get an error from the network operations,
        // don't display the "No data" message.
        if (!isDataBound()) {
            bindNoDataView();
        }
    }

    private void bindNoDataView() {
        // Set up views for "no data" message
        try {
            breakdownContainer.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            mNoDataBinder.bindData(mNoDataParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Model {
        public final IMatchAlliancesContainer allianceData;
        public final JsonObject scoreData;
        public final MatchType matchType;
        public final String winningAlliance;
        public final int year;

        public Model(MatchType matchType, int year, String winningAlliance,
                     IMatchAlliancesContainer
                     allianceData, JsonObject scoreData) {
            this.matchType = matchType;
            this.year = year;
            this.allianceData = allianceData;
            this.scoreData = scoreData;
            this.winningAlliance = winningAlliance;
        }

        @Override
        public boolean equals(Object o) {
            return o != null && o instanceof Model
                    && ((Model) o).year == year
                    && ((Model) o).matchType == matchType
                    && ((Model) o).scoreData.equals(scoreData)
                    && ((Model) o).winningAlliance.equals(winningAlliance)
                    && ((Model) o).allianceData.equals(allianceData);
        }
    }
}
