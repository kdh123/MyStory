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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dhkim.camera.navigation.cameraNavigation
import com.dhkim.friend.presentation.navigation.FRIEND_ROUTE
import com.dhkim.friend.presentation.navigation.changeFriendInfoNavigation
import com.dhkim.friend.presentation.navigation.friendNavigation
import com.dhkim.friend.presentation.navigation.navigateToChangeFriendInfo
import com.dhkim.friend.presentation.navigation.navigateToFriend
import com.dhkim.location.domain.Place
import com.dhkim.location.presentation.navigation.searchNavigation
import com.dhkim.map.presentation.navigation.MAP_ROUTE
import com.dhkim.map.presentation.navigation.mapNavigation
import com.dhkim.notification.navigation.navigateToNotification
import com.dhkim.notification.navigation.notificationNavigation
import com.dhkim.setting.presentation.navigation.navigateToSetting
import com.dhkim.setting.presentation.navigation.settingNavigation
import com.dhkim.home.presentation.navigation.ADD_TIME_CAPSULE_ROUTE
import com.dhkim.home.presentation.navigation.TIME_CAPSULE_ROUTE
import com.dhkim.home.presentation.navigation.addTimeCapsuleNavigation
import com.dhkim.home.presentation.navigation.imageDetailNavigation
import com.dhkim.home.presentation.navigation.moreTimeCapsuleNavigation
import com.dhkim.home.presentation.navigation.navigateToAddTimeCapsule
import com.dhkim.home.presentation.navigation.navigateToImageDetail
import com.dhkim.home.presentation.navigation.navigateToMore
import com.dhkim.home.presentation.navigation.timeCapsuleDetailNavigation
import com.dhkim.home.presentation.navigation.timeCapsuleNavigation
import com.dhkim.home.presentation.navigation.timeCapsuleOpenNavigation
import com.dhkim.ui.WarningDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    showGuide: Boolean,
    onCloseGuide: () -> Unit,
    onNeverShowGuideAgain: () -> Unit
) {
    val context = LocalContext.current
    val state = rememberStandardBottomSheetState(
        skipHiddenState = false
    )
    val scaffoldState = rememberBottomSheetScaffoldState(state)
    val navController = rememberNavController()
    val items = listOf(Screen.TimeCapsule, Screen.AddTimeCapsule, Screen.Map, Screen.Friend)
    val isBottomNavShow = navController
        .currentBackStackEntryAsState()
        .value?.destination?.route in listOf(TIME_CAPSULE_ROUTE, MAP_ROUTE, FRIEND_ROUTE)
    var selectedPlace: Place? by remember {
        mutableStateOf(null)
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomNavShow,
                enter = fadeIn() + slideIn { IntOffset(0, it.height) },
                exit = fadeOut() + slideOut { IntOffset(0, it.height) }
            ) {
                if (selectedPlace == null) {
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(align = Alignment.Bottom),
                        containerColor = colorResource(id = R.color.white),
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        items.forEach { screen ->
                            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                            NavigationBarItem(
                                icon = {
                                    if (isSelected) {
                                        Icon(painterResource(id = screen.selected), contentDescription = null, tint = Color.Unspecified)
                                    } else {
                                        Icon(painterResource(id = screen.unSelected), contentDescription = null, tint = Color.Unspecified)
                                    }
                                },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    val route = if (screen.route == ADD_TIME_CAPSULE_ROUTE) {
                                        val friendId = " "
                                        "$ADD_TIME_CAPSULE_ROUTE/$friendId"
                                    } else {
                                        screen.route
                                    }

                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
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
            navController = navController,
            startDestination = "timeCapsule"
        ) {
            mapNavigation(
                scaffoldState = scaffoldState,
                onNavigateToSearch = { lat, lng ->
                    navController.navigate("search/$lat/$lng")
                },
                onHideBottomNav = { place ->
                    selectedPlace = place
                },
                onInitSavedState = {
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("place", null)
                },
                onNavigateToAdd = { place ->
                    val friendId = " "
                    navController.navigate("$ADD_TIME_CAPSULE_ROUTE/$friendId")
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("place", place)
                }
            )
            timeCapsuleDetailNavigation(
                onNavigateToImageDetail = { index, images ->
                    navController.navigateToImageDetail(index, images)
                },
                onBack = {
                    navController.navigateUp()
                }
            )
            imageDetailNavigation()
            moreTimeCapsuleNavigation(
                onNavigateToDetail = { id, isReceived ->
                    navController.navigate("timeCapsuleDetail/$id/${isReceived}")
                },
                onBack = {
                    navController.navigateUp()
                }
            )
            timeCapsuleOpenNavigation(
                onNavigateToDetail = { id, isReceived ->
                    navController.navigate("timeCapsuleDetail/$id/${isReceived}") {
                        popUpTo(navController.currentDestination?.id ?: return@navigate) {
                            inclusive = true
                        }
                    }
                }
            )
            timeCapsuleNavigation(
                onNavigateToAdd = {
                    val friendId = " "
                    navController.navigate("$ADD_TIME_CAPSULE_ROUTE/$friendId")
                },
                onNavigateToOpen = { id, isReceived ->
                    navController.navigate("timeCapsuleOpen/$id/${isReceived}")
                },
                onNavigateToDetail = { id, isReceived ->
                    navController.navigate("timeCapsuleDetail/$id/${isReceived}")
                },
                onNavigateToNotification = {
                    navController.navigateToNotification()
                },
                onNavigateToSetting = {
                    navController.navigateToSetting()
                },
                onNavigateToProfile = {
                    navController.navigateToFriend()
                },
                onNavigateToMore = {
                    navController.navigateToMore()
                },
                modifier = Modifier
                    .padding(
                        bottom = innerPadding.calculateBottomPadding()
                    )
            )
            notificationNavigation(
                onNavigateToTimeCapsule = {

                },
                onBack = {
                    navController.navigateUp()
                }
            )
            settingNavigation(
                onBack = {
                    navController.navigateUp()
                }
            )
            addTimeCapsuleNavigation(
                onNavigateToCamera = {
                    navController.navigate("camera")
                },
                onBack = {
                    navController.navigateUp()
                }
            )
            cameraNavigation(
                folderName = context.getString(com.dhkim.main.R.string.app_name),
                onNext = { imageUrl ->
                    navController.navigateUp()
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("imageUrl", imageUrl)
                },
                onBack = navController::navigateUp
            )
            searchNavigation {
                navController.navigateUp()
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("place", it)
            }
            friendNavigation(
                onAddTimeCapsule = {
                    navController.navigateToAddTimeCapsule(friendId = it)
                },
                onNavigateToChangeInfo = {
                    navController.navigateToChangeFriendInfo(it)
                },
                onBack = {
                    navController.navigateUp()
                },
                modifier = Modifier
                    .padding(bottom = innerPadding.calculateBottomPadding())
            )
            changeFriendInfoNavigation {
                navController.navigateUp()
            }
        }
    }
}


sealed class Screen(
    val title: String, val selected: Int, val unSelected: Int, val route: String
) {
    data object Map : Screen("홈", R.drawable.ic_map_primary, R.drawable.ic_map_black, MAP_ROUTE)
    data object AddTimeCapsule : Screen("추가", R.drawable.ic_add_primary, R.drawable.ic_add_black, ADD_TIME_CAPSULE_ROUTE)
    data object TimeCapsule : Screen("타임캡슐", R.drawable.ic_time_primary, R.drawable.ic_time_black, TIME_CAPSULE_ROUTE)
    data object Friend : Screen("프로필", R.drawable.ic_profile_primary, R.drawable.ic_profile_black, FRIEND_ROUTE)
}
