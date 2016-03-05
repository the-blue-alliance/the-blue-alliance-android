package com.thebluealliance.androidclient.fragments.framework;

import com.google.common.base.Preconditions;

import com.thebluealliance.androidclient.BaseTestActivity;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;

import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import android.support.annotation.IntDef;
import android.support.v4.app.FragmentManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DatafeedFragmentTestController<F extends DatafeedFragment> {

    private static final String FRAGMENT_TAG = "fragment";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_NOTHING, STATE_ATTACHED, STATE_PAUSED, STATE_STOPPED, STATE_DESTROYED})
    public @interface FragmentState {}
    public static final int STATE_NOTHING = 0;
    public static final int STATE_ATTACHED = 1;
    public static final int STATE_PAUSED = 2;
    public static final int STATE_STOPPED = 3;
    public static final int STATE_DESTROYED = 4;

    private @FragmentState int mState;
    private F mFragment;
    private CacheableDatafeed mDatafeed;
    private ActivityController<BaseTestActivity> mActivityController;
    private BaseTestActivity mActivity;

    public DatafeedFragmentTestController() {
        mState = STATE_NOTHING;
    }

    public DatafeedFragmentTestController(F fragment) {
        this();
        withFragment(fragment);
    }

    public DatafeedFragmentTestController<F> withFragment(F fragment) {
        mFragment = fragment;
        return this;
    }

    public F getFragment() {
        return mFragment;
    }

    public DatafeedFragmentTestController<F> withDatafeed(CacheableDatafeed datafeed) {
        mDatafeed = datafeed;
        return this;
    }

    public DatafeedFragmentTestController<F> withActivityController
            (ActivityController<BaseTestActivity> activityController) {
        mActivityController = activityController;
        return this;
    }

    public DatafeedFragmentTestController<F> makeTestActivityController() {
        mActivityController = Robolectric.buildActivity(BaseTestActivity.class);
        return this;
    }

    public DatafeedFragmentTestController<F> makeActivity() {
        Preconditions.checkState(mActivityController != null, "You must have an ActivityController");
        mActivity = mActivityController.create().start().resume().visible().get();
        return this;
    }

    public DatafeedFragmentTestController<F> attach() {
        Preconditions.checkState(mState == STATE_NOTHING, "Must have no state to attach");
        if (mActivityController == null) {
            makeTestActivityController();
        }
        if (mActivity == null) {
            makeActivity();
        }
        FragmentManager manager = mActivity.getSupportFragmentManager();
        manager.beginTransaction()
                .add(mFragment, FRAGMENT_TAG).commit();
        mState = STATE_ATTACHED;
        return this;
    }

    public DatafeedFragmentTestController<F> pause() {
        Preconditions.checkState(mState == STATE_ATTACHED, "Must have attached to stop");
        mActivityController.pause();
        mState = STATE_PAUSED;
        return this;
    }

    public DatafeedFragmentTestController<F> stop() {
        Preconditions.checkState(mState == STATE_PAUSED, "Must have paused to stop");
        mActivityController.stop();
        mState = STATE_STOPPED;
        return this;
    }

    public DatafeedFragmentTestController<F> destroy() {
        Preconditions.checkState(mState == STATE_STOPPED, "Must have stopped to destroy");
        mActivityController.destroy();
        mState = STATE_DESTROYED;
        return this;
    }

    public BaseTestActivity getActivity() {
        return mActivity;
    }

}
