package com.dhkim.timecapsule.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dhkim.timecapsule.home.domain.Category
import com.dhkim.timecapsule.profile.domain.Friend
import com.dhkim.timecapsule.profile.domain.User
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.dhkim.timecapsule.search.domain.Place
import com.dhkim.timecapsule.search.domain.SearchRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private lateinit var database: DatabaseReference


    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<HomeSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()


    init {
        /*viewModelScope.launch {
            userRepository.getMyInfo().catch {  }
                .collect {
                    val a = it
                    Log.e("user", "user33 : $it")
                }
        }*/

        database = Firebase.database.reference

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val data = dataSnapshot.value as? Map<*, *>
                Log.e("user", "user22wer : ${data}")

                if (data != null) {
                    val getData = data["uuuuooo"] as? Map<*, *>
                    if (getData != null) {
                        Log.e("user", "user2222rr : ${getData["id"]}")
                        Log.e("user", "user3333rr : ${getData["profileImage"]}")
                        Log.e("user", "user4444rr : ${getData["friends"]}")
                        Log.e("user", "user4444rr : ${getData["requests"]}")

                        val friends = getData["friends"] as? List<Map<*, *>>
                        val friend = friends?.get(0)?.get("id") ?: "No"
                        Log.e("friend", "freind : $friend")
                    }
                }

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message

            }
        }

        //Firebase.database.getReference("users").child("uuuuooo").addValueEventListener(postListener)

        val user = User(
            id = "dh3",
            profileImage = "",
            friends = listOf(Friend("f1", ), Friend("f2"), Friend("f3")),
            requests = listOf(Friend("f1"), Friend("f2"), Friend("f3"))
        )

        /*viewModelScope.launch {
            userRepository.addFriends(userId = "qwer1234").catch {  }.firstOrNull()
        }*/


        /*database
            .child("users").child("dh4")
            .child("friends").child("friend1").setValue(Friend(id = "id2", uuid = "uid23"))

            database
            .child("users").child("dh4")
            .child("friends").child("friend1").setValue(null)
            */

        /*val key = "dh3"
        if (key == null) {
            Log.e("nono", "Couldn't get push key for posts")
        } else {
            //val post = Post(userId, username, title, body)
            val postValues = user

            val childUpdates = hashMapOf<String, Any>(
                "/users/$key/friends" to postValues,
                //"/user-posts/$userId/$key" to postValues,
            )

            database.updateChildren(childUpdates)
        }*/
    }

    fun searchPlacesByCategory(category: Category, lat: String, lng: String) {
        viewModelScope.launch {
            searchRepository.getPlaceByCategory(
                category = category,
                lat = lat,
                lng = lng
            ).cachedIn(viewModelScope)
                .collect {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        query = category.type,
                        category = category,
                        places = flowOf(it).stateIn(viewModelScope),
                        selectedPlace = null
                    )
                    _sideEffect.emit(HomeSideEffect.BottomSheet(isHide = false))
                }
        }
    }

    fun searchPlacesByKeyword(query: String, lat: String, lng: String) {
        viewModelScope.launch {
            searchRepository.getNearPlaceByKeyword(
                query = query,
                lat = lat,
                lng = lng
            ).cachedIn(viewModelScope)
                .collect {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        query = query,
                        category = Category.entries.first { category ->  category.type == query },
                        places = flowOf(it).stateIn(viewModelScope),
                        selectedPlace = null
                    )
                    _sideEffect.emit(HomeSideEffect.BottomSheet(isHide = false))
                }
        }
    }

    fun selectPlace(place: Place) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                query = place.name,
                category = Category.None,
                selectedPlace = place,
                places = MutableStateFlow(PagingData.empty())
            )

            _sideEffect.emit(HomeSideEffect.BottomSheet(isHide = true))
        }
    }

    fun closeSearch(selectPlace: Boolean) {
        _uiState.value = if (selectPlace) {
            _uiState.value.copy(
                places = MutableStateFlow(PagingData.empty()),
                category = Category.None
            )
        } else {
            _uiState.value.copy(
                query = "",
                places = MutableStateFlow(PagingData.empty()),
                category = Category.None,
                selectedPlace = null
            )
        }

        viewModelScope.launch {
            _sideEffect.emit(HomeSideEffect.BottomSheet(isHide = true))
        }
    }
}