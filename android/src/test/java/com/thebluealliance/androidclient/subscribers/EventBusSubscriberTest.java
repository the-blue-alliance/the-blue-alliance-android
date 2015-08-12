package com.thebluealliance.androidclient.subscribers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import de.greenrobot.event.EventBus;

import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
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