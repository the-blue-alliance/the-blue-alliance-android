package com.thebluealliance.androidclient.gcm.notifications;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EventDownNotificationTest {

    private static final String KEY = "DOWN";
    private static final String MOCK_TITLE = KEY + " No Event Data Available";
    private static final String MOCK_TEXT = "Down Event has no data";

    private EventDownNotification mNotification;
    private JsonObject mData;
    private Context context;

    @Before
    public void setUp() {
        context = mock(Context.class, RETURNS_DEEP_STUBS);
        mData = ModelMaker.getModel(JsonObject.class, "notification_event_down");
        mNotification = new EventDownNotification(mData.toString());

        when(context.getString(R.string.notification_event_down, KEY)).thenReturn(MOCK_TITLE);
        when(context.getString(R.string.notification_event_down_content, "Down Event"))
          .thenReturn(MOCK_TEXT);
    }

    @Test
    public void testParseData() {
        mNotification.parseMessageData();
        mNotification.buildNotification(context, null);

        assertEquals(MOCK_TITLE, mNotification.getTitle());
        assertEquals(MOCK_TEXT, mNotification.getMessage());
    }
}