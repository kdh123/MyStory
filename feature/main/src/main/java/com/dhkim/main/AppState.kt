package com.dhkim.main

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dhkim.friend.presentation.navigation.FRIEND_ROUTE
import com.dhkim.friend.presentation.navigation.navigateToChangeFriendInfo
import com.dhkim.friend.presentation.navigation.navigateToFriend
import com.dhkim.home.presentation.navigation.ADD_TIME_CAPSULE_ROUTE
import com.dhkim.home.presentation.navigation.TIME_CAPSULE_OPEN_ROUTE
import com.dhkim.home.presentation.navigation.TIME_CAPSULE_ROUTE
import com.dhkim.home.presentation.navigation.navigateToAddTimeCapsule
import com.dhkim.home.presentation.navigation.navigateToDetail
import com.dhkim.home.presentation.navigation.navigateToDetailFromOpen
import com.dhkim.home.presentation.navigation.navigateToImageDetail
import com.dhkim.home.presentation.navigation.navigateToMore
import com.dhkim.home.presentation.navigation.navigateToOpenTimeCapsule
import com.dhkim.location.domain.Place
import com.dhkim.location.presentation.navigation.navigateToSearch
import com.dhkim.map.presentation.navigation.MAP_ROUTE
import com.dhkim.notification.navigation.navigateToNotification
import com.dhkim.setting.presentation.navigation.navigateToSetting
import com.dhkim.trip.presentation.navigation.TRIP_ROUTE
import com.dhkim.trip.presentation.navigation.navigateToTripDetail
import com.dhkim.trip.presentation.navigation.navigateToTripImageDetail
import com.dhkim.trip.presentation.navigation.navigateToTripSchedule
import com.dhkim.user.domain.Friend

@Stable
class AppState(
    val navController: NavHostController
) {
    val bottomItems = listOf(
        Screen.TimeCapsule,
        Screen.Map,
        Screen.AddTimeCapsule,
        Screen.Trip,
        Screen.Friend
    )
    val routes = listOf(
        TIME_CAPSULE_ROUTE,
        MAP_ROUTE,
        TRIP_ROUTE,
        FRIEND_ROUTE
    )
    private val showBottomNavItems = bottomItems
        .filter { it.route != ADD_TIME_CAPSULE_ROUTE }
        .map { it.route }
    val isBottomNavShow: Boolean
        @Composable get() {
            val entry = navController.currentBackStackEntryAsState().value
            val route = entry?.destination?.route ?: return true

            return route in routes
        }
    val currentDestination: String
        @Composable get() {
            val entry = navController.currentBackStackEntryAsState().value
            val route = entry?.destination?.parent?.route ?: entry?.destination?.route ?: return Screen.TimeCapsule.route

            return route
        }

    fun navigateToTopLevelDestination(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun initSavedState() {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set("place", null)
    }

    fun navigateToAddTimeCapsule() {
        val friendId = " "
        navController.navigate("$ADD_TIME_CAPSULE_ROUTE/$friendId")
    }

    fun navigateToOpenTimeCapsule(id: String, isReceived: Boolean) =
        navController.navigateToOpenTimeCapsule(id, isReceived)

    fun navigateToDetailTimeCapsule(id: String, isReceived: Boolean) =
        navController.navigateToDetail(id, isReceived)

    fun navigateToDetailTimeCapsuleFromOpen(id: String, isReceived: Boolean) =
        navController.navigateToDetailFromOpen(id, isReceived)

    fun navigateToNotification() = navController.navigateToNotification()
    fun navigateToSetting() = navController.navigateToSetting()
    fun navigateToFriend() = navController.navigateToFriend()
    fun navigateToMoreTimeCapsule() = navController.navigateToMore()
    fun navigateToImageDetail(currentIndex: String, images: String) =
        navController.navigateToImageDetail(currentIndex, images)

    fun navigateToSearch(lat: Double, lng: Double) = navController.navigateToSearch(lat, lng)
    fun navigateToAddTimeCapsuleWithPlace(place: Place) {
        val friendId = " "
        navController.run {
            navigate("$ADD_TIME_CAPSULE_ROUTE/$friendId")
            currentBackStackEntry
                ?.savedStateHandle
                ?.set("place", place)
        }
    }

    fun navigateToTripSchedule(tripId: String) = navController.navigateToTripSchedule(tripId)
    fun navigateToTripDetail(tripId: String) = navController.navigateToTripDetail(tripId)
    fun navigateToTripImageDetail(imageUrl: String) =
        navController.navigateToTripImageDetail(imageUrl)

    fun navigateToAddTimeCapsuleWithFriend(friendId: String) =
        navController.navigateToAddTimeCapsule(friendId)

    fun navigateToChangeFriendInfo(friend: Friend) =
        navController.navigateToChangeFriendInfo(friend)

    fun navigateToAddTimeCapsuleFromSearch(place: Place) {
        navController.run {
            navigateUp()
            currentBackStackEntry
                ?.savedStateHandle
                ?.set("place", place)
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun rememberMyStoryAppState(
    navController: NavHostController = rememberNavController(),
): AppState {
    return remember(
        navController,
    ) {
        AppState(
            navController = navController
        )
    }
}