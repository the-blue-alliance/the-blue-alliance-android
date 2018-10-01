package com.thebluealliance.androidclient.auth.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thebluealliance.androidclient.auth.User;
import com.thebluealliance.androidclient.auth.google.GoogleAuthProvider;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(RobolectricTestRunner.class)
public class FirebaseAuthProviderTest {

    @Mock GoogleAuthProvider mGoogleAuthProvider;
    @Mock FirebaseAuth mFirebaseAuth;

    private FirebaseAuthProvider mFirebaseAuthProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mFirebaseAuthProvider = new FirebaseAuthProvider(mFirebaseAuth, mGoogleAuthProvider);
    }

    @Test
    public void onStart() {
        mFirebaseAuthProvider.onStart();
        verify(mGoogleAuthProvider).onStart();
    }

    @Test
    public void onStop() {
        mFirebaseAuthProvider.onStop();
        verify(mGoogleAuthProvider).onStop();
    }

    @Test
    public void isUserSignedIn() {
        assertFalse(mFirebaseAuthProvider.isUserSignedIn());

        FirebaseUser user = Mockito.mock(FirebaseUser.class);
        when(mFirebaseAuth.getCurrentUser()).thenReturn(user);
        assertTrue(mFirebaseAuthProvider.isUserSignedIn());
    }

    @Test
    public void getCurrentUser() {
        assertNull(mFirebaseAuthProvider.getCurrentUser());

        FirebaseUser user = Mockito.mock(FirebaseUser.class);
        when(mFirebaseAuth.getCurrentUser()).thenReturn(user);
        User currentUser = mFirebaseAuthProvider.getCurrentUser();
        assertNotNull(currentUser);
        assertTrue((currentUser instanceof FirebaseSignInUser));
    }

    @Test
    public void buildSignInIntent() {
        mFirebaseAuthProvider.buildSignInIntent();
        verify(mGoogleAuthProvider).buildSignInIntent();
    }
}