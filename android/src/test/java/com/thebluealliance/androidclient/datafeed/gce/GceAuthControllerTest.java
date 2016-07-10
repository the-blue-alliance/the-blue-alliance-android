package com.thebluealliance.androidclient.datafeed.gce;

import com.google.android.gms.auth.GoogleAuthException;

import com.thebluealliance.androidclient.accounts.AccountController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import android.accounts.Account;
import android.content.Context;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class GceAuthControllerTest {

    private static final String AUTH_TOKEN = "abc123";
    private static final String CLIENT_ID = "foo.bar";

    @Mock Context mContext;
    @Mock Account mAccount;
    @Mock AccountController mAccountController;

    private GceAuthController mAuthController;

    @Before
    public void setUp() throws GoogleAuthException, IOException {
        MockitoAnnotations.initMocks(this);
        mAuthController = spy(new GceAuthController(mContext, mAccountController));


        doReturn(AUTH_TOKEN)
                .when(mAuthController)
                .getGoogleAuthToken(mContext, mAccount, "audience:server:client_id:foo.bar");
    }

    @Test
    public void testGetAuthHeader() {
        when(mAccountController.getCurrentAccount()).thenReturn(mAccount);
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