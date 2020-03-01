package com.thebluealliance.androidclient.listeners;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class EventTeamClickListenerTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = mock(Context.class, RETURNS_DEEP_STUBS);
    }

    @Test
    public void testGetKeysFromTag() {
        EventTeamClickListener listener = new EventTeamClickListener(mContext);
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag("2015arc_frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc254B"}, listener.getKeysFromTag("2015arc_frc254B"));
        assertArrayEquals(new Object[]{null, null}, listener.getKeysFromTag(null));

        listener = new EventTeamClickListener(mContext, "2015arc_frc254");
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag("2015arc_frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc254B"}, listener.getKeysFromTag("2015arc_frc254B"));
        assertArrayEquals(new Object[]{"2015gal", "frc254"}, listener.getKeysFromTag("2015gal_frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag("frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc111"}, listener.getKeysFromTag("frc111"));
        assertArrayEquals(new Object[]{"2015arc", "frc254B"}, listener.getKeysFromTag("frc254B"));
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag(null));

        listener = new EventTeamClickListener(mContext, "2015arc", null);
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag("2015arc_frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc254B"}, listener.getKeysFromTag("2015arc_frc254B"));
        assertArrayEquals(new Object[]{"2015gal", "frc254"}, listener.getKeysFromTag("2015gal_frc254"));
        assertArrayEquals(new Object[]{"2015gal", "frc111"}, listener.getKeysFromTag("2015gal_frc111"));
        assertArrayEquals(new Object[]{"2015arc", "frc111"}, listener.getKeysFromTag("frc111"));
        assertArrayEquals(new Object[]{"2015arc", "frc111B"}, listener.getKeysFromTag("frc111B"));
        assertArrayEquals(new Object[]{"2015arc", null}, listener.getKeysFromTag(null));

        listener = new EventTeamClickListener(mContext, "2015arc", "frc254");
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag("2015arc_frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc254B"}, listener.getKeysFromTag("2015arc_frc254B"));
        assertArrayEquals(new Object[]{"2015gal", "frc254"}, listener.getKeysFromTag("2015gal_frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag("frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc111"}, listener.getKeysFromTag("frc111"));
        assertArrayEquals(new Object[]{"2015arc", "frc254B"}, listener.getKeysFromTag("frc254B"));
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag(null));
    }
}
