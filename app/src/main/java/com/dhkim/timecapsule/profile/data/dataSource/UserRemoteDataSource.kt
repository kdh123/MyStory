package com.dhkim.timecapsule.profile.data.dataSource

import com.dhkim.timecapsule.profile.data.di.FirebaseModule
import com.dhkim.timecapsule.profile.domain.User
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

typealias isSuccessful = Boolean

class UserRemoteDataSource @Inject constructor(
    @FirebaseModule.FirebaseDatabase private val database: DatabaseReference
) {

    fun updateUser(user: User) {
        database.child("users").child(user.id).setValue(user)
    }

    fun updateFcmToken(userId: String, fcmToken: String): Flow<isSuccessful> {
        return callbackFlow {
            database.child("users").child(userId).child("fcmToken").setValue(fcmToken)
                .addOnSuccessListener {
                    trySend(true)
                }
                .addOnFailureListener {
                    trySend(false)
                }
            awaitClose()
        }
    }
}