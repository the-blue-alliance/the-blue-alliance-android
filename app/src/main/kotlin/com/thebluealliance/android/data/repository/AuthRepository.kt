package com.thebluealliance.android.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) {
    val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            FirebaseCrashlytics.getInstance().setUserId(user?.uid.orEmpty())
            trySend(user)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    fun isSignedIn(): Boolean = firebaseAuth.currentUser != null

    suspend fun getIdToken(): String? {
        val user = firebaseAuth.currentUser ?: return null
        return user.getIdToken(false).await().token
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
