package com.thebluealliance.androidclient.datafeed.gce;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.thebluealliance.androidclient.auth.firebase.FirebaseAuthProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
public class GceAuthControllerTest {

    private static final String AUTH_TOKEN = "abc123";

    @Mock FirebaseAuthProvider mFirebaseAuth;

    private GceAuthController mAuthController;

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        MockitoAnnotations.initMocks(this);
        mAuthController = spy(new GceAuthController(mFirebaseAuth));

        doReturn(AUTH_TOKEN)
                .when(mAuthController)
                .getGoogleAuthToken();
    }

    @Test
    public void testGetAuthHeader() {
        String header = mAuthController.getAuthHeader();
        assertEquals(header, "Bearer abc123");
    }

    @Test
    public void testGetAuthHeaderNoAccount() throws ExecutionException, InterruptedException {
        doReturn(null)
                .when(mAuthController)
                .getGoogleAuthToken();
        String header = mAuthController.getAuthHeader();
        assertNull(header);
    }

}