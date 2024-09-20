package com.dhkim.home.presentation.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dhkim.common.Constants
import com.dhkim.home.domain.TimeCapsule
import com.dhkim.home.presentation.LocationDialog
import com.dhkim.home.presentation.TimeCapsuleScreen
import com.dhkim.home.presentation.TimeCapsuleSideEffect
import com.dhkim.home.presentation.TimeCapsuleUiState
import com.dhkim.home.presentation.TimeCapsuleViewModel
import com.dhkim.home.presentation.add.AddTimeCapsuleScreen
import com.dhkim.home.presentation.add.AddTimeCapsuleViewModel
import com.dhkim.home.presentation.detail.ImageDetailScreen
import com.dhkim.home.presentation.detail.TimeCapsuleDetailScreen
import com.dhkim.home.presentation.detail.TimeCapsuleDetailViewModel
import com.dhkim.home.presentation.detail.TimeCapsuleOpenScreen
import com.dhkim.home.presentation.more.MoreTimeCapsuleScreen
import com.dhkim.home.presentation.more.MoreTimeCapsuleViewModel
import com.dhkim.location.domain.Place
import com.dhkim.ui.WarningDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.flow.Flow

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
            sideEffect = sideEffect,
            imageUrl = imageUrl,
            place = place,
            friendId = friendId.ifBlank { "" },
            onSaveTimeCapsule = viewModel::saveTimeCapsule,
            onSetCheckShare = viewModel::setCheckShare,
            onSetCheckLocation = viewModel::setCheckLocation,
            onSetSelectImageIndex = viewModel::setSelectImageIndex,
            onSetOpenDate = viewModel::setOpenDate,
            onTyping = viewModel::typing,
            onCheckSharedFriend = viewModel::checkSharedFriend,
            onQuery = viewModel::onQuery,
            onPlaceClick = viewModel::onPlaceClick,
            onSearchAddress = viewModel::searchAddress,
            onInitPlace = viewModel::initPlace,
            onAddFriend = viewModel::addFriend,
            onAddImage = viewModel::addImage,
            onBack = onBack
        )
    }
}

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
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(startDestination = TIME_CAPSULE_ROUTE, route = TIME_CAPSULE_MAIN_ROUTE) {
        composable(TIME_CAPSULE_ROUTE) {
            val viewModel = hiltViewModel<TimeCapsuleViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sideEffect = remember {
                {
                    viewModel.sideEffect
                }
            }

            /*TestScreen(
                uiState = uiState,
                sideEffect = sideEffect,
                //modifier = modifier,
                onDeleteTimeCapsule = viewModel::deleteTimeCapsule,
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToOpen = onNavigateToOpen,
                onNavigateToDetail = onNavigateToDetail,
                onNavigateToNotification = onNavigateToNotification,
                onNavigateToSetting = onNavigateToSetting,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToMore = onNavigateToMore
            )*/

            TimeCapsuleScreen(
                uiState = uiState,
                sideEffect = sideEffect,
                modifier = modifier,
                onDeleteTimeCapsule = remember(viewModel) {
                    viewModel::deleteTimeCapsule
                },
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToOpen = onNavigateToOpen,
                onNavigateToDetail = onNavigateToDetail,
                onNavigateToNotification = onNavigateToNotification,
                onNavigateToSetting = onNavigateToSetting,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToMore = onNavigateToMore
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

        composable("timeCapsuleDetail/{id}/{isReceived}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val isReceived =
                (backStackEntry.arguments?.getString("isReceived") ?: "false").toBoolean()
            val viewModel = hiltViewModel<TimeCapsuleDetailViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sideEffect = remember {
                viewModel.sideEffect
            }

            TimeCapsuleDetailScreen(
                timeCapsuleId = id,
                isReceived = isReceived,
                uiState = uiState,
                sideEffect = sideEffect,
                onNavigateToImageDetail = onNavigateToImageDetail,
                onBack = onBack,
                onDelete = viewModel::deleteTImeCapsule,
                init = viewModel::init
            )
        }

        composable("$TIME_CAPSULE_OPEN_ROUTE/{id}/{isReceived}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val isReceived =
                (backStackEntry.arguments?.getString("isReceived") ?: "false").toBoolean()
            val viewModel = hiltViewModel<TimeCapsuleDetailViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            TimeCapsuleOpenScreen(
                timeCapsuleId = id,
                isReceived = isReceived,
                uiState = uiState,
                onNavigateToDetail = onNavigateToDetailFromOpen,
                init = viewModel::init
            )
        }
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TestScreen(
    uiState: TimeCapsuleUiState,
    sideEffect: () -> Flow<TimeCapsuleSideEffect>,
    //modifier: Modifier = Modifier,
    onDeleteTimeCapsule: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToOpen: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToSetting: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMore: () -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current
    var currentLocation by remember {
        mutableStateOf(Constants.defaultLocation)
    }
    var selectedTimeCapsule by remember {
        mutableStateOf(TimeCapsule())
    }
    val context = LocalContext.current
    var showLocationDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showOpenDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showMenuDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showPermissionDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    currentLocation = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                }
        }
    }

    LaunchedEffect(locationPermissionState) {
        if (!locationPermissionState.status.isGranted && locationPermissionState.status.shouldShowRationale) {
            // Show rationale if needed
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /*if (showPermissionDialog) {
        WarningDialog(
            dialogTitle = "위치 권한 요청",
            dialogText = "타임캡슐을 개봉하기 위해서 위치 권한을 허용해주세요.",
            onConfirmation = {
                showPermissionDialog = false
                val uri = Uri.fromParts("package", context.packageName, null)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    data = uri
                }
                context.startActivity(intent)
            },
            onDismissRequest = {
                showPermissionDialog = false
            }
        )
    }

    if (showMenuDialog) {
        Dialog(
            onDismissRequest = {
                selectedTimeCapsule = TimeCapsule()
                showMenuDialog = false
            }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    androidx.compose.material3.Text(
                        text = "메뉴",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    androidx.compose.material3.Text(
                        text = "삭제",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showMenuDialog = false
                                showDeleteDialog = true
                            }
                    )
                }
            }
        }
    }*/

    if (showDeleteDialog) {
        val desc = if (selectedTimeCapsule.sharedFriends.isNotEmpty() && !selectedTimeCapsule.isReceived) {
            "이 타임캡슐을 공유했던 친구들 디바이스에서도 삭제가 됩니다. 정말 삭제하겠습니까?"
        } else {
            "정말 삭제하겠습니까?"
        }

        WarningDialog(
            dialogTitle = "삭제",
            dialogText = desc,
            onConfirmation = {
                /*onDeleteTimeCapsule(selectedTimeCapsule.id, selectedTimeCapsule.isReceived)
                showDeleteDialog = false*/
            },
            onDismissRequest = {
                showDeleteDialog = false
            }
        )
    }

    if (showOpenDialog) {
        WarningDialog(
            dialogTitle = "개봉",
            dialogText = "정말 개봉하겠습니까?",
            onConfirmation = remember {
                {
                    showOpenDialog = false
                    onNavigateToOpen(selectedTimeCapsule.id, selectedTimeCapsule.isReceived)
                }
            },
            onDismissRequest = {
                showOpenDialog = false
            }
        )
    }

    if (showLocationDialog) {
        LocationDialog(timeCapsule = selectedTimeCapsule) {
            showLocationDialog = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "Hello World!")
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
