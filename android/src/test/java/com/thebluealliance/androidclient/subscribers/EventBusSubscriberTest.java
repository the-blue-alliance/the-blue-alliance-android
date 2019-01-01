package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.DefaultTestRunner;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

@RunWith(DefaultTestRunner.class)
public class EventBusSubscriberTest {

    @Mock public EventBus mEventBus;

    private EventBusSubscriber mSubscriber;
    private Object mData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mSubscriber = new EventBusSubscriber(mEventBus);
        mData = new Object();
    }

    @Test
    public void testPostToBus() {
        mSubscriber.onNext(mData);
        verify(mEventBus).post(mData);
    }
}