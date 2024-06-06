package com.dhkim.timecapsule.profile.presentation

import androidx.lifecycle.ViewModel
import com.dhkim.timecapsule.profile.data.di.FirebaseModule
import com.dhkim.timecapsule.profile.domain.Friend
import com.dhkim.timecapsule.profile.domain.User
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

typealias UserId = String

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @FirebaseModule.UserFirebaseDatabase private val database: DatabaseReference
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.value as? Map<*, *>
                val friends = (data?.get("kdh1234") as? Map<*, *>)?.get("friends") as? List<Map<*, *>> ?: listOf()
                val requests = (data?.get("kdh1234") as? Map<*, *>)?.get("requests") as? List<String> ?: listOf()
                val getFriends = friends.map {
                    Friend(it["id"]!! as String, it["pending"]!! as Boolean)
                }

                _uiState.value = _uiState.value.copy(isLoading = false, friends = getFriends, requests = requests)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }

        database.addValueEventListener(userListener)
    }

    fun updateUser(id: String, profileImageUrl: String, friends: List<Friend>, requests: List<UserId>) {
        val user = User(
            id = id,
            profileImageUrl = profileImageUrl,
            friends = friends,
            requests = requests
        )

        userRepository.updateUser(user = user)
    }
}