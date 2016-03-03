package com.thebluealliance.androidclient.fragments.framework;

import com.thebluealliance.androidclient.BaseTestActivity;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;

import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import android.support.annotation.IntDef;
import android.support.v4.app.FragmentManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.mockito.Mockito.spy;

public class DatafeedFragmentTestController<F extends DatafeedFragment> {

    private static final String FRAGMENT_TAG = "fragment";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_NOTHING, STATE_ATTACHED, STATE_STOPPED})
    public @interface FragmentState {}
    public static final int STATE_NOTHING = 0;
    public static final int STATE_ATTACHED = 1;
    public static final int STATE_STOPPED = 2;

    private @FragmentState int mState;
    private DatafeedFragment mFragment;
    private CacheableDatafeed mDatafeed;
    private ActivityController mActivityController;
    private BaseTestActivity mActivity;

    public DatafeedFragmentTestController() {
        mState = STATE_NOTHING;
    }

    public DatafeedFragmentTestController(F fragment) {
        this();
        withFragment(fragment);
    }

    public DatafeedFragmentTestController<F> withFragment(F fragment) {
        mFragment = spy(fragment);
        return this;
    }

    public DatafeedFragmentTestController<F> withDatafeed(CacheableDatafeed datafeed) {
        mDatafeed = datafeed;
        return this;
    }

    public DatafeedFragmentTestController<F> attach() {
        mActivityController = Robolectric.buildActivity(BaseTestActivity.class);
        mActivity = (BaseTestActivity) mActivityController.create().start().resume().visible().get();

        FragmentManager manager = mActivity.getSupportFragmentManager();
        manager.beginTransaction()
                .add(mFragment, FRAGMENT_TAG).commit();
        return this;
    }
}
