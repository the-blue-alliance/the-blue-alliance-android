package com.thebluealliance.androidclient.gcm;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.thebluealliance.androidclient.config.AppConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class GcmControllerTest {

    private static final String TEST_ID = "meow";

    @Mock AppConfig mAppConfig;
    @Mock SharedPreferences mSharedPreferences;
    @Mock SharedPreferences.Editor mEditor;

    private GcmController mController;

    @SuppressLint("CommitPrefEdits") @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mSharedPreferences.edit()).thenReturn(mEditor);
        when(mEditor.putString(anyString(), anyString())).thenReturn(mEditor);
        mController = new GcmController(mAppConfig, mSharedPreferences);
    }

    @Test
    public void getSenderId() throws Exception {
        when(mAppConfig.getString(GcmController.PREF_SENDER_ID))
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