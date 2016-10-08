package com.thebluealliance.androidclient.gcm;

import com.thebluealliance.androidclient.LocalProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class GcmControllerTest {

    private static final String TEST_ID = "meow";

    @Mock LocalProperties mLocalProperties;
    @Mock SharedPreferences mSharedPreferences;
    @Mock SharedPreferences.Editor mEditor;

    private GcmController mController;

    @SuppressLint("CommitPrefEdits") @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mSharedPreferences.edit()).thenReturn(mEditor);
        when(mEditor.putString(anyString(), anyString())).thenReturn(mEditor);
        mController = new GcmController(mLocalProperties, mSharedPreferences);
    }

    @Test
    public void getSenderId() throws Exception {
        when(mLocalProperties.readLocalProperty(GcmController.PREF_SENDER_ID))
                .thenReturn(TEST_ID);
        String senderId = mController.getSenderId();
        assertEquals(senderId, TEST_ID);
    }

    @Test
    public void getRegistrationId() {
        when(mSharedPreferences.getString(GcmController.PROPERTY_GCM_REG_ID, ""))
                .thenReturn(TEST_ID);
        String regId = mController.getRegistrationId();
        assertEquals(regId, TEST_ID);
    }

    @Test
    public void storeRegistrationId() {
        mController.storeRegistrationId(TEST_ID);
        verify(mEditor).putString(GcmController.PROPERTY_GCM_REG_ID, TEST_ID);
    }

}