package com.dhkim.timecapsule.timecapsule.data

import android.annotation.SuppressLint
import android.util.Log
import com.dhkim.timecapsule.common.DateUtil
import com.dhkim.timecapsule.common.presentation.NotificationManager
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.dhkim.timecapsule.setting.domain.SettingRepository
import com.dhkim.timecapsule.timecapsule.data.dataSource.remote.DeleteTimeCapsule
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

    @Inject
    lateinit var settingRepository: SettingRepository

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e("fcm", "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.e("fcm", "Message data payload: ${remoteMessage.data}")

            val isDelete = remoteMessage.data.containsKey("isDelete")
            val jsonData = Gson().toJson(remoteMessage.data)

            if (isDelete) {
                val deleteTimeCapsule = Gson().fromJson(jsonData, DeleteTimeCapsule::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    timeCapsuleRepository.deleteReceivedTimeCapsule(id = deleteTimeCapsule.timeCapsuleId)
                    notificationManager.showNotification(
                        title = "나의이야기",
                        desc = "${deleteTimeCapsule.sender}님이 나에게 공유한 타임캡슐을 삭제하였습니다."
                    )
                }
            } else {
                val sharedTimeCapsule = Gson().fromJson(jsonData, SharedTimeCapsule::class.java)
                Log.e("data", "sharedData : $sharedTimeCapsule")

                sharedTimeCapsule.run {
                    val receivedTimeCapsule = ReceivedTimeCapsule(
                        id = timeCapsuleId,
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
                        notificationManager.showNotification(
                            title = "나의이야기",
                            desc = "${sender}님이 타임캡슐을 공유하였습니다."
                        )
                    }
                }
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.e("fcm", "Message Notification Body: ${it.body}")
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