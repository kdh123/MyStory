package com.dhkim.home.presentation.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.dhkim.home.presentation.TimeCapsuleScreen
import com.dhkim.home.presentation.TimeCapsuleViewModel
import com.dhkim.home.presentation.add.AddTimeCapsuleScreen
import com.dhkim.home.presentation.add.AddTimeCapsuleViewModel
import com.dhkim.home.presentation.detail.ImageDetailScreen
import com.dhkim.home.presentation.detail.TimeCapsuleDetailScreen
import com.dhkim.home.presentation.detail.TimeCapsuleDetailViewModel
import com.dhkim.home.presentation.detail.TimeCapsuleOpenScreen
import com.dhkim.home.presentation.more.MoreTimeCapsuleScreen
import com.dhkim.home.presentation.more.MoreTimeCapsuleViewModel
import com.dhkim.location.domain.model.Place
import com.dhkim.ui.Popup
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng

const val TIME_CAPSULE_MAIN_ROUTE = "mainTimeCapsule"
const val TIME_CAPSULE_OPEN_ROUTE = "timeCapsuleOpen"
const val TIME_CAPSULE_ROUTE = "timeCapsule"
const val ADD_TIME_CAPSULE_ROUTE = "addTimeCapsule"
const val IMAGE_DETAIL_ROUTE = "imageDetail"
const val MORE_TIME_CAPSULE = "moreTimeCapsule"
const val TIME_CAPSULE_DETAIL = "timeCapsuleDetail"

fun NavGraphBuilder.addTimeCapsuleScreen(
    onBack: () -> Unit,
) {
    composable("addTimeCapsule/{friendId}") { backStackEntry ->
        val viewModel = hiltViewModel<AddTimeCapsuleViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect = remember {
            viewModel.sideEffect
        }
        val place = backStackEntry.savedStateHandle.get<Place>("place") ?: Place()
        val imageUrl = backStackEntry.savedStateHandle.get<String>("imageUrl") ?: ""
        val friendId = backStackEntry.arguments?.getString("friendId") ?: ""

        AddTimeCapsuleScreen(
            uiState = uiState,
            sideEffect = { sideEffect },
            onAction = remember(viewModel) {
                viewModel::onAction
            },
            imageUrl = imageUrl,
            place = place,
            friendId = friendId.ifBlank { "" },
            onBack = onBack
        )
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
fun NavGraphBuilder.timeCapsuleScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToOpen: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToDetailFromOpen: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToSetting: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMore: () -> Unit,
    onNavigateToImageDetail: (String, String) -> Unit,
    showPopup: (Popup) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(startDestination = TIME_CAPSULE_ROUTE, route = TIME_CAPSULE_MAIN_ROUTE) {
        composable(TIME_CAPSULE_ROUTE) {
            val context = LocalContext.current
            val viewModel = hiltViewModel<TimeCapsuleViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sideEffect = remember { { viewModel.sideEffect } }
            val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
            val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
            val requestPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            val currentLocation = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                            viewModel.updateCurrentLocation(currentLocation)
                        }
                }
            }

            TimeCapsuleScreen(
                uiState = uiState,
                sideEffect = sideEffect,
                permissionState = locationPermissionState,
                modifier = modifier,
                requestPermission = {
                    LaunchedEffect(locationPermissionState) {
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                onDeleteTimeCapsule = remember(viewModel) { viewModel::deleteTimeCapsule },
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToOpen = onNavigateToOpen,
                onNavigateToDetail = onNavigateToDetail,
                onNavigateToNotification = onNavigateToNotification,
                onNavigateToSetting = onNavigateToSetting,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToMore = onNavigateToMore,
                showPopup = showPopup
            )
        }

        composable("${IMAGE_DETAIL_ROUTE}/{currentIndex}/{images}") {
            val currentIndex = it.arguments?.getString("currentIndex") ?: ""
            val images = it.arguments?.getString("images") ?: ""

            ImageDetailScreen(currentIndex = currentIndex.toInt(), images = images.split(","))
        }

        composable(MORE_TIME_CAPSULE) {
            val viewModel = hiltViewModel<MoreTimeCapsuleViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            MoreTimeCapsuleScreen(
                uiState = uiState,
                onNavigateToDetail = onNavigateToDetail,
                onBack = onBack
            )
        }

        composable(
            route = "$TIME_CAPSULE_DETAIL/{id}/{isReceived}",
            arguments = listOf(
                navArgument("id") {
                    defaultValue = ""
                },
                navArgument("isReceived") {
                    defaultValue = "false"
                }
            )
        ) {
            val viewModel = hiltViewModel<TimeCapsuleDetailViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sideEffect = remember {
                viewModel.sideEffect
            }

            TimeCapsuleDetailScreen(
                uiState = uiState,
                sideEffect = { sideEffect },
                onAction = remember(viewModel) {
                    viewModel::onAction
                },
                onNavigateToImageDetail = onNavigateToImageDetail,
                showPopup = showPopup,
                onBack = onBack
            )
        }

        composable(
            route = "$TIME_CAPSULE_OPEN_ROUTE/{id}/{isReceived}",
            arguments = listOf(
                navArgument("id") {
                    defaultValue = ""
                },
                navArgument("isReceived") {
                    defaultValue = "false"
                }
            )
        ) {
            val viewModel = hiltViewModel<TimeCapsuleDetailViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            TimeCapsuleOpenScreen(
                uiState = uiState,
                onNavigateToDetail = onNavigateToDetailFromOpen,
            )
        }
    }
}

fun NavController.navigateToOpenTimeCapsule(id: String, isReceived: Boolean) {
    navigate("$TIME_CAPSULE_OPEN_ROUTE/$id/${isReceived}")
}

fun NavController.navigateToDetail(id: String, isReceived: Boolean) {
    navigate("$TIME_CAPSULE_DETAIL/$id/${isReceived}")
}

fun NavController.navigateToDetailFromOpen(id: String, isReceived: Boolean) {
    navigate("$TIME_CAPSULE_DETAIL/$id/${isReceived}") {
        val currentRoute = currentDestination?.route ?: return@navigate
        popUpTo(currentRoute) {
            inclusive = true
        }
    }
}

fun NavController.navigateToImageDetail(currentIndex: String, images: String) {
    navigate("$IMAGE_DETAIL_ROUTE/$currentIndex/$images")
}

fun NavController.navigateToMore() {
    navigate(MORE_TIME_CAPSULE)
}

fun NavController.navigateToAddTimeCapsule(friendId: String) {
    navigate("$ADD_TIME_CAPSULE_ROUTE/$friendId")
}
