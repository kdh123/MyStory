package com.dhkim.timecapsule.timecapsule.data

import android.annotation.SuppressLint
import android.util.Log
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
@AndroidEntryPoint
class TimeCapsuleMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e("fcm", "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.e("fcm", "Message data payload: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.e("fcm", "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        CoroutineScope(Dispatchers.IO).launch {
            userRepository.run {
                val isSuccessful = registerPush(uuid = getUuid(), fcmToken = token)
                if (isSuccessful) {
                    updateLocalFcmToken(fcmToken = token)
                } else {
                    updateLocalFcmToken(fcmToken = "")
                }
            }
        }
    }
}