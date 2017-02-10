package com.thebluealliance.androidclient.views.breakdowns;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.types.MatchType;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public abstract class AbstractMatchBreakdownView extends FrameLayout {

    public AbstractMatchBreakdownView(Context context) {
        super(context);
        init();
    }

    public AbstractMatchBreakdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AbstractMatchBreakdownView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    abstract void init();

    public abstract boolean initWithData(MatchType matchType,
                                         JsonObject allianceData,
                                         JsonObject scoredata);
}
