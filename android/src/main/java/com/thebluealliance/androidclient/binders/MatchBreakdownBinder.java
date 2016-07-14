package com.thebluealliance.androidclient.binders;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownView2016;

import android.support.annotation.Nullable;
import com.thebluealliance.androidclient.Log;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MatchBreakdownBinder extends AbstractDataBinder<MatchBreakdownBinder.Model> {

    @Bind(R.id.match_breakdown) MatchBreakdownView2016 breakdown;
    @Bind(R.id.progress) ProgressBar progressBar;

    private MatchType mMatchType;

    public void setMatchType(MatchType matchType) {
        mMatchType = matchType;
    }

    @Override
    public void updateData(@Nullable MatchBreakdownBinder.Model data) {
        if (data == null || data.allianceData == null || data.scoreData == null
                || breakdown == null) {
            if (!isDataBound()) {
                setDataBound(false);
            }
            return;
        }
        long startTime = System.currentTimeMillis();
        Log.d("BINDING DATA");
        boolean success = breakdown.initWithData(mMatchType, data.allianceData, data.scoreData);

        if (!success) {
            setDataBound(false);
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        breakdown.setVisibility(View.VISIBLE);
        mNoDataBinder.unbindData();
        Log.d("BINDING COMPLETE; ELAPSED TIME: " + (System.currentTimeMillis() - startTime) + "ms");
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
        Log.e(Log.getStackTraceString(throwable));

        // If we received valid data from the cache but get an error from the network operations,
        // don't display the "No data" message.
        if (!isDataBound()) {
            bindNoDataView();
        }
    }

    private void bindNoDataView() {
        // Set up views for "no data" message
        try {
            breakdown.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            mNoDataBinder.bindData(mNoDataParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Model {
        public final JsonObject allianceData;
        public final JsonObject scoreData;

        public Model(JsonObject allianceData, JsonObject scoreData) {
            this.allianceData = allianceData;
            this.scoreData = scoreData;
        }

        @Override
        public boolean equals(Object o) {
            return o != null && o instanceof Model
                    && ((Model) o).scoreData.equals(scoreData)
                    && ((Model) o).allianceData.equals(allianceData);
        }
    }
}
