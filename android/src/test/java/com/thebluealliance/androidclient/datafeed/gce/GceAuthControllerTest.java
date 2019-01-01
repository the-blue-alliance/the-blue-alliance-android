package com.thebluealliance.androidclient.datafeed.gce;

import com.google.android.gms.auth.GoogleAuthException;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.accounts.AccountController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(DefaultTestRunner.class)
public class GceAuthControllerTest {

    private static final String AUTH_TOKEN = "abc123";
    private static final String CLIENT_ID = "foo.bar";
    private static final String ACCOUNT = "foo@bar.com";

    @Mock Context mContext;
    @Mock AccountController mAccountController;

    private GceAuthController mAuthController;

    @Before
    public void setUp() throws GoogleAuthException, IOException {
        MockitoAnnotations.initMocks(this);
        mAuthController = spy(new GceAuthController(mContext, mAccountController));

        doReturn(AUTH_TOKEN)
                .when(mAuthController)
                .getGoogleAuthToken(ACCOUNT, "audience:server:client_id:foo.bar");
    }

    @Test
    public void testGetAuthHeader() {
        when(mAccountController.getSelectedAccount()).thenReturn(ACCOUNT);
        when(mAccountController.getWebClientId()).thenReturn(CLIENT_ID);
        String header = mAuthController.getAuthHeader();
        assertEquals(header, "Bearer abc123");
    }

    @Test
    public void testGetAuthHeaderNoAccount() {
        String header = mAuthController.getAuthHeader();
        assertNull(header);
    }

}