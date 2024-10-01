package com.dhkim.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import com.dhkim.friend.presentation.navigation.FRIEND_MAIN_ROUTE
import com.dhkim.friend.presentation.navigation.friendScreen
import com.dhkim.home.presentation.navigation.ADD_TIME_CAPSULE_ROUTE
import com.dhkim.home.presentation.navigation.TIME_CAPSULE_MAIN_ROUTE
import com.dhkim.home.presentation.navigation.addTimeCapsuleScreen
import com.dhkim.home.presentation.navigation.timeCapsuleScreen
import com.dhkim.location.presentation.navigation.searchScreen
import com.dhkim.map.presentation.navigation.MAP_ROUTE
import com.dhkim.map.presentation.navigation.mapScreen
import com.dhkim.notification.navigation.notificationScreen
import com.dhkim.setting.presentation.navigation.settingScreen
import com.dhkim.trip.presentation.navigation.TRIP_MAIN_ROUTE
import com.dhkim.trip.presentation.navigation.tripScreen
import com.dhkim.ui.Popup
import com.dhkim.ui.WarningDialog

@Composable
fun MainScreen(
    appState: AppState,
    showGuide: Boolean,
    onCloseGuide: () -> Unit,
    onNeverShowGuideAgain: () -> Unit,
    currentPopup: Popup?,
    showPopup: (Popup?) -> Unit
) {
    var isPlaceSelected by remember {
        mutableStateOf(false)
    }
    val isBottomNavShow = appState.isBottomNavShow && !isPlaceSelected

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomNavShow,
                enter = fadeIn() + slideIn { IntOffset(0, it.height) },
                exit = fadeOut() + slideOut { IntOffset(0, it.height) }
            ) {
                if (isBottomNavShow) {
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(align = Alignment.Bottom),
                        containerColor = colorResource(id = R.color.white)
                    ) {
                        appState.bottomItems.forEach { screen ->
                            val isSelected = screen.route == appState.currentDestination
                            val onBottomClick = remember {
                                {
                                    val route = if (screen.route == ADD_TIME_CAPSULE_ROUTE) {
                                        val friendId = " "
                                        "$ADD_TIME_CAPSULE_ROUTE/$friendId"
                                    } else {
                                        screen.route
                                    }

                                    appState.navigateToTopLevelDestination(route)
                                }
                            }

                            NavigationBarItem(
                                icon = {
                                    if (isSelected) {
                                        Icon(
                                            painterResource(id = screen.selected),
                                            contentDescription = null,
                                            tint = Color.Unspecified
                                        )
                                    } else {
                                        Icon(
                                            painterResource(id = screen.unSelected),
                                            contentDescription = null,
                                            tint = Color.Unspecified
                                        )
                                    }
                                },
                                selected = isSelected,
                                onClick = onBottomClick
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (showGuide) {
            WarningDialog(
                dialogTitle = "알림",
                dialogText = "나의이야기는 사용자의 이름, 전화번호, 주소 등의 어떠한 개인정보도 수집하지 않습니다. " +
                        "그리고 앱에서 작성한 글, 사진 등은 모두 서버가 아닌 디바이스에 저장되기 때문에 " +
                        "앱 삭제시 모든 데이터가 삭제될 수 있으니 이 점 유의하시길 바랍니다.",
                negativeText = "확인",
                positiveText = "다시 보지 않기",
                onConfirmation = onNeverShowGuideAgain,
                onDismissRequest = onCloseGuide
            )
        }

        NavHost(
            modifier = Modifier
                .fillMaxSize(),
            navController = appState.navController,
            startDestination = TIME_CAPSULE_MAIN_ROUTE
        ) {
            timeCapsuleScreen(
                onNavigateToAdd = appState::navigateToAddTimeCapsule,
                onNavigateToOpen = appState::navigateToOpenTimeCapsule,
                onNavigateToDetail = appState::navigateToDetailTimeCapsule,
                onNavigateToDetailFromOpen = appState::navigateToDetailTimeCapsuleFromOpen,
                onNavigateToNotification = appState::navigateToNotification,
                onNavigateToSetting = appState::navigateToSetting,
                onNavigateToProfile = appState::navigateToFriend,
                onNavigateToMore = appState::navigateToMoreTimeCapsule,
                onNavigateToImageDetail = appState::navigateToImageDetail,
                showPopup = showPopup,
                onBack = appState.navController::navigateUp,
                modifier = Modifier
                    .padding(bottom = innerPadding.calculateBottomPadding())
            )
            mapScreen(
                onNavigateToSearch = appState::navigateToSearch,
                onHideBottomNav = {
                    isPlaceSelected = it != null
                },
                onInitSavedState = appState::initSavedState,
                onNavigateToAdd = appState::navigateToAddTimeCapsuleWithPlace
            )
            addTimeCapsuleScreen(
                onBack = appState.navController::navigateUp
            )
            tripScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                onNavigateToSchedule = appState::navigateToTripSchedule,
                onNavigateToDetail = appState::navigateToTripDetail,
                onNavigateToImageDetail = appState::navigateToTripImageDetail,
                showPopup = showPopup,
                onBack = appState.navController::navigateUp
            )
            friendScreen(
                onNavigateToAddTimeCapsule = appState::navigateToAddTimeCapsuleWithFriend,
                onNavigateToChangeInfo = appState::navigateToChangeFriendInfo,
                onBack = appState.navController::navigateUp,
                showPopup = showPopup,
                modifier = Modifier
                    .padding(bottom = innerPadding.calculateBottomPadding())
            )
            notificationScreen(
                onNavigateToTimeCapsule = {},
                onBack = appState.navController::navigateUp
            )
            settingScreen(onBack = appState.navController::navigateUp)
            searchScreen(onBack = appState::navigateToAddTimeCapsuleFromSearch)
        }
    }

    when (currentPopup) {
        is Popup.OneButton -> {

        }

        is Popup.Warning -> {
            with(currentPopup) {
                WarningDialog(
                    dialogTitle = title,
                    dialogText = desc,
                    onConfirmation = {
                        onPositiveClick()
                        showPopup(null)
                    },
                    onDismissRequest = {
                        onDismissRequest?.invoke()
                        showPopup(null)
                    }
                )
            }
        }

        null -> {}
    }
}


sealed class Screen(
    val title: String, val selected: Int, val unSelected: Int, val route: String
) {
    data object Map : Screen("홈", R.drawable.ic_map_primary, R.drawable.ic_map_black, MAP_ROUTE)
    data object AddTimeCapsule :
        Screen("추가", R.drawable.ic_add_primary, R.drawable.ic_add_black, ADD_TIME_CAPSULE_ROUTE)

    data object TimeCapsule :
        Screen(
            "타임캡슐",
            R.drawable.ic_home_primary,
            R.drawable.ic_home_black,
            TIME_CAPSULE_MAIN_ROUTE
        )

    data object Friend :
        Screen("프로필", R.drawable.ic_profile_primary, R.drawable.ic_profile_black, FRIEND_MAIN_ROUTE)

    data object Trip :
        Screen("여행", R.drawable.ic_trip_primary, R.drawable.ic_trip_black, TRIP_MAIN_ROUTE)
}
