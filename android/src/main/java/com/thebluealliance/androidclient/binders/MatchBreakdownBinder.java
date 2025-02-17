package com.thebluealliance.androidclient.binders;

import android.view.View;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.databinding.FragmentMatchBreakdownBinding;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.androidclient.views.breakdowns.AbstractMatchBreakdownView;
import com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownView2015;
import com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownView2016;
import com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownView2017;
import com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownView2018;
import com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownView2019;
import com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownView2020;
import com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownView2022;
import com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownView2023;
import com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownView2024;
import com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownView2025;
import com.thebluealliance.api.model.IMatchAlliancesContainer;
public class MatchBreakdownBinder extends AbstractDataBinder<MatchBreakdownBinder.Model, FragmentMatchBreakdownBinding> {

    @Override
    public void updateData(@Nullable MatchBreakdownBinder.Model data) {
        if (data == null || data.allianceData == null || data.scoreData == null
                || mBinding == null) {
            if (!isDataBound()) {
                setDataBound(false);
            }
            return;
        }
        long startTime = System.currentTimeMillis();
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
            case 2018:
                breakdownView = new MatchBreakdownView2018(mActivity);
                break;
            case 2019:
                breakdownView = new MatchBreakdownView2019(mActivity);
                break;
            case 2020:
                breakdownView = new MatchBreakdownView2020(mActivity);
                break;
            case 2022:
                breakdownView = new MatchBreakdownView2022(mActivity);
                break;
            case 2023:
                breakdownView = new MatchBreakdownView2023(mActivity);
                break;
            case 2024:
                breakdownView = new MatchBreakdownView2024(mActivity);
                break;
            case 2025:
                breakdownView = new MatchBreakdownView2025(mActivity);
                break;
            default:
                breakdownView = null;
                break;
        }

        if (breakdownView == null) {
            setDataBound(false);
            return;
        }

        mBinding.matchBreakdown.addView(breakdownView);
        boolean success = breakdownView.initWithData(data.matchType,
                                                     data.winningAlliance,
                                                     data.allianceData,
                                                     data.scoreData);

        if (!success) {
            setDataBound(false);
            return;
        }

        mBinding.progress.setVisibility(View.GONE);
        mBinding.matchBreakdown.setVisibility(View.VISIBLE);
        mNoDataBinder.unbindData();
        TbaLogger.d("BINDING COMPLETE; ELAPSED TIME: " + (System.currentTimeMillis() - startTime) + "ms");
        setDataBound(true);
    }

    @Override
    public void onComplete() {
        if (mBinding != null) {
            mBinding.progress.setVisibility(View.GONE);
        }

        if (!isDataBound()) {
            bindNoDataView();
        }
    }

    @Override
    public void bindViews() {
        mBinding = FragmentMatchBreakdownBinding.bind(mRootView);
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
            mBinding.matchBreakdown.setVisibility(View.GONE);
            mBinding.progress.setVisibility(View.GONE);
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
