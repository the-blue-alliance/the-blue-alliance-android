package com.thebluealliance.androidclient.fragments;

import com.google.android.gms.analytics.Tracker;

import com.thebluealliance.androidclient.BaseTestActivity;
import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.binders.NoDataBinder;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.DatafeedFragmentTestController;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;
import com.thebluealliance.androidclient.fragments.framework.SimpleBinder;
import com.thebluealliance.androidclient.fragments.framework.SimpleDatafeedFragment;
import com.thebluealliance.androidclient.fragments.framework.SimpleSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(IntegrationRobolectricRunner.class)
public class TestDatafeedFragment extends BaseFragmentTest {

    private SimpleDatafeedFragment mFragment;
    private DatafeedFragmentTestController<SimpleDatafeedFragment> mController;
    private BaseTestActivity mActivity;
    private Subject<String, String> mSubject;

    private SimpleSubscriber mSubscriber;
    private SimpleBinder mBinder;
    private NoDataBinder mNoDataBinder;
    private RefreshController mRefreshController;
    private TBAStatusController mStatusController;
    private EventBus mEventBus;
    private Tracker mAnalyticsTracker;

    @Before
    public void setUp() {
        mFragment = spy(new SimpleDatafeedFragment());
        mSubject = spy(new SerializedSubject<>(PublishSubject.create()));
        mFragment.setObservable(mSubject);
        mController = FragmentTestDriver.getController(mFragment)
                .makeTestActivityController()
                .makeActivity();
        mActivity = mController.getActivity();

        mRefreshController = mActivity.getRefreshController();
        mStatusController = mActivity.getStatusController();
        mEventBus = mActivity.getEventBus();
    }

    @Test
    public void testLifecycle() {
        // Activity has already been created, we've called up through onResume
        verify(mRefreshController).reset();
        verify(mEventBus).register(mActivity);
        verify(mStatusController).fetchApiStatus();

        mController.attach();
        getInjectedFragmentParams();
        verifyFragmentAttach();

        mController.pause();
        verify(mRefreshController).unregisterRefreshable("SimpleTest");
        verify(mEventBus).unregister(mActivity);
        verify(mEventBus, never()).unregister(mSubscriber);

        mController.stop();
        verify(mSubscriber).onParentStop();

        mController.destroy();
        verify(mBinder).unbind(true);
    }

    private void verifyFragmentAttach() {
        verify(mSubscriber).setConsumer(mBinder);
        verify(mSubscriber).setRefreshController(mRefreshController);
        verify(mSubscriber).setRefreshTag("SimpleTest");
        verify(mSubscriber).setTracker(mAnalyticsTracker);
        verify(mBinder).setActivity(mActivity);
        verify(mBinder).setNoDataBinder(mNoDataBinder);
        verify(mBinder).setNoDataParams(null);
        verify(mSubscriber).onRefreshStart(RefreshController.NOT_REQUESTED_BY_USER);
        verify(mRefreshController).registerRefreshable("SimpleTest", mFragment);
        verify(mEventBus, never()).register(mSubscriber);

    }

    // TODO push something to subject and test subscriber receives
    // TODO test commonStatus update when things are down
    // TODO test setting shouldRegisterSubscriberToEventBus
    // TODO test instance saving

    private void getInjectedFragmentParams() {
        mSubscriber = mFragment.getSubscriber();
        mBinder = mFragment.getBinder();
        mNoDataBinder = mFragment.getNoDataBinder();
        mAnalyticsTracker = mFragment.getAnalyticsTracker();
    }
}
