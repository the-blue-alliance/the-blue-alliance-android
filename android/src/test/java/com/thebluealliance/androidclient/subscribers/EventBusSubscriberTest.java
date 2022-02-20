package com.thebluealliance.androidclient.subscribers;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.LooperMode;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.mockito.Mockito.verify;

@LooperMode(LooperMode.Mode.PAUSED)
@RunWith(AndroidJUnit4.class)
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