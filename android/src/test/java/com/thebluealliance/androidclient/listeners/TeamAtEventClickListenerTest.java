package com.thebluealliance.androidclient.listeners;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertArrayEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TeamAtEventClickListenerTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = mock(Context.class, RETURNS_DEEP_STUBS);
    }

    @Test
    public void testGetKeysFromTag() {
        TeamAtEventClickListenerV2 listener = new TeamAtEventClickListenerV2(mContext);
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag("2015arc_frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc254B"}, listener.getKeysFromTag("2015arc_frc254B"));
        assertArrayEquals(new Object[]{null, null}, listener.getKeysFromTag(null));

        listener = new TeamAtEventClickListenerV2(mContext, "2015arc_frc254");
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag("2015arc_frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc254B"}, listener.getKeysFromTag("2015arc_frc254B"));
        assertArrayEquals(new Object[]{"2015gal", "frc254"}, listener.getKeysFromTag("2015gal_frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag("frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc111"}, listener.getKeysFromTag("frc111"));
        assertArrayEquals(new Object[]{"2015arc", "frc254B"}, listener.getKeysFromTag("frc254B"));
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag(null));

        listener = new TeamAtEventClickListenerV2(mContext, "2015arc", null);
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag("2015arc_frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc254B"}, listener.getKeysFromTag("2015arc_frc254B"));
        assertArrayEquals(new Object[]{"2015gal", "frc254"}, listener.getKeysFromTag("2015gal_frc254"));
        assertArrayEquals(new Object[]{"2015gal", "frc111"}, listener.getKeysFromTag("2015gal_frc111"));
        assertArrayEquals(new Object[]{"2015arc", "frc111"}, listener.getKeysFromTag("frc111"));
        assertArrayEquals(new Object[]{"2015arc", "frc111B"}, listener.getKeysFromTag("frc111B"));
        assertArrayEquals(new Object[]{"2015arc", null}, listener.getKeysFromTag(null));

        listener = new TeamAtEventClickListenerV2(mContext, "2015arc", "frc254");
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag("2015arc_frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc254B"}, listener.getKeysFromTag("2015arc_frc254B"));
        assertArrayEquals(new Object[]{"2015gal", "frc254"}, listener.getKeysFromTag("2015gal_frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag("frc254"));
        assertArrayEquals(new Object[]{"2015arc", "frc111"}, listener.getKeysFromTag("frc111"));
        assertArrayEquals(new Object[]{"2015arc", "frc254B"}, listener.getKeysFromTag("frc254B"));
        assertArrayEquals(new Object[]{"2015arc", "frc254"}, listener.getKeysFromTag(null));
    }
}
