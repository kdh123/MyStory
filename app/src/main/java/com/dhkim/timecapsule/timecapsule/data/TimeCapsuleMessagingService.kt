package com.dhkim.timecapsule.timecapsule.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.TimeCapsuleApplication
import com.dhkim.timecapsule.common.DateUtil
import com.dhkim.timecapsule.onboarding.OnboardingActivity
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.dhkim.timecapsule.timecapsule.domain.ReceivedTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.SharedTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsuleRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
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

    @Inject
    lateinit var timeCapsuleRepository: TimeCapsuleRepository

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e("fcm", "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.e("fcm", "Message data payload: ${remoteMessage.data}")
            val jsonData = Gson().toJson(remoteMessage.data)

            val sharedTimeCapsule = Gson().fromJson(jsonData, SharedTimeCapsule::class.java)
            Log.e("data", "sharedData : $sharedTimeCapsule")

            sharedTimeCapsule.run {
                val receivedTimeCapsule = ReceivedTimeCapsule(
                    id = "${System.currentTimeMillis()}",
                    profileImage = profileImage,
                    date = DateUtil.todayDate(),
                    openDate = openDate,
                    sender = sender,
                    lat = if (checkLocation) {
                        lat
                    } else {
                        "0.0"
                    },
                    lng = if (checkLocation) {
                        lng
                    } else {
                        "0.0"
                    },
                    placeName = if (checkLocation) {
                        placeName
                    } else {
                        ""
                    },
                    address = if (checkLocation) {
                        address
                    } else {
                        ""
                    },
                    content = content,
                    checkLocation = checkLocation,
                    isOpened = false
                )

                CoroutineScope(Dispatchers.IO).launch {
                    timeCapsuleRepository.saveReceivedTimeCapsule(receivedTimeCapsule)
                }

                showNotification("TimeCapsule", "${sender}님이 타임캡슐을 공유하였습니다.")
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.e("fcm", "Message Notification Body: ${it.body}")
        }
    }

    private fun showNotification(title: String, desc: String) {
        // Create an explicit intent for an Activity in your app.
        val intent = Intent(this, OnboardingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, TimeCapsuleApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_time_primary)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@TimeCapsuleMessagingService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)

        /*CoroutineScope(Dispatchers.IO).launch {
            userRepository.run {
                val isSuccessful = registerPush(uuid = getMyUuid(), fcmToken = token)
                if (isSuccessful) {
                    updateLocalFcmToken(fcmToken = token)
                } else {
                    updateLocalFcmToken(fcmToken = "")
                }
            }
        }*/
    }
}