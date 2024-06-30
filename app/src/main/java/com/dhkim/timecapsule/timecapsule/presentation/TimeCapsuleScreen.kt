@file:Suppress("UNCHECKED_CAST")

package com.dhkim.timecapsule.timecapsule.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dhkim.timecapsule.R
import com.dhkim.common.Constants
import com.dhkim.common.DateUtil
import com.dhkim.ui.ShimmerBrush
import com.dhkim.ui.WarningDialog
import com.dhkim.common.DistanceManager
import com.dhkim.common.StableList
import com.dhkim.ui.DefaultBackground
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsule
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.skydoves.landscapist.glide.GlideImage

@SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TimeCapsuleScreen(
    uiState: TimeCapsuleUiState,
    sideEffect: TimeCapsuleSideEffect,
    modifier: Modifier = Modifier,
    onDeleteTimeCapsule: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToOpen: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToSetting: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMore: () -> Unit
) {
    var currentLocation by remember {
        mutableStateOf(Constants.defaultLocation)
    }
    var selectedTimeCapsule by remember {
        mutableStateOf(TimeCapsule())
    }
    val context = LocalContext.current
    var showLocationDialog by remember {
        mutableStateOf(false)
    }
    var showOpenDialog by remember {
        mutableStateOf(false)
    }
    var showMenuDialog by remember {
        mutableStateOf(false)
    }
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }
    var showPermissionDialog by remember {
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

    if (showPermissionDialog) {
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
                    Text(
                        text = "메뉴",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
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
    }

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
                onDeleteTimeCapsule(selectedTimeCapsule.id, selectedTimeCapsule.isReceived)
                showDeleteDialog = false
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
            onConfirmation = {
                onNavigateToOpen(selectedTimeCapsule.id, selectedTimeCapsule.isReceived)
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

    LaunchedEffect(locationPermissionState) {
        if (!locationPermissionState.status.isGranted && locationPermissionState.status.shouldShowRationale) {
            // Show rationale if needed
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(sideEffect) {
        when (sideEffect) {
            is TimeCapsuleSideEffect.None -> {}

            is TimeCapsuleSideEffect.Message -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is TimeCapsuleSideEffect.NavigateToOpen -> {
                onNavigateToOpen(sideEffect.id, sideEffect.isReceived)
            }

            is TimeCapsuleSideEffect.NavigateToDetail -> {
                onNavigateToDetail(sideEffect.id, sideEffect.isReceived)
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
            ) {
                Text(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    text = "타임캡슐",
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .alpha(0f)
                        .width(0.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_notification_black),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .width(28.dp)
                        .height(28.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            onNavigateToNotification()
                        }
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_setting_black),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp, end = 10.dp)
                        .width(28.dp)
                        .height(28.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            onNavigateToSetting()
                        }
                )
            }
        }
    ) {
        Box(
            modifier = modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                LoadingScreen()
            } else {
                LazyColumn {
                    items(uiState.timeCapsules, key = {
                        it.id
                    }) {
                        when (it.type) {
                            TimeCapsuleType.Title -> {
                                val title = it.data as? String ?: ""
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                ) {
                                    Text(
                                        text = it.data as? String ?: "",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .width(0.dp)
                                            .weight(1f)
                                            .align(Alignment.CenterVertically)
                                    )

                                    if (title == "나의 이야기") {
                                        val interactionSource = remember { MutableInteractionSource() }
                                        Text(
                                            text = "더보기",
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically)
                                                .border(
                                                    color = colorResource(id = R.color.gray),
                                                    width = 1.dp,
                                                    shape = RoundedCornerShape(20.dp)
                                                )
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                                .clickable(
                                                    interactionSource = interactionSource,
                                                    indication = null
                                                ) {
                                                    onNavigateToMore()
                                                }
                                        )
                                    }
                                }
                            }

                            TimeCapsuleType.SubTitle -> {
                                Text(
                                    text = it.data as? String ?: "",
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp),
                                    color = colorResource(id = R.color.gray)
                                )
                            }

                            TimeCapsuleType.OpenableTimeCapsule -> {
                                OpenableTimeCapsules(
                                    timeCapsules = it.data as? StableList<TimeCapsule> ?: StableList(),
                                    currentLat = currentLocation.latitude,
                                    currentLng = currentLocation.longitude,
                                    onShowLocationDialog = {
                                        if (!locationPermissionState.status.isGranted) {
                                            showPermissionDialog = true
                                        } else {
                                            selectedTimeCapsule = it
                                            showLocationDialog = true
                                        }
                                    },
                                    onShowOpenDialog = {
                                        selectedTimeCapsule = it
                                        showOpenDialog = true
                                    },
                                    onLongClick = {
                                        selectedTimeCapsule = it
                                        showMenuDialog = true
                                    }
                                )
                            }

                            TimeCapsuleType.UnopenedTimeCapsule -> {
                                UnopenedTimeCapsules(
                                    timeCapsules = it.data as? StableList<TimeCapsule> ?: StableList(),
                                    onClick = {
                                        selectedTimeCapsule = it
                                        showLocationDialog = true
                                    },
                                    onLongClick = {
                                        selectedTimeCapsule = it
                                        showMenuDialog = true
                                    }
                                )
                            }

                            TimeCapsuleType.OpenedTimeCapsule -> {
                                OpenedTimeCapsules(
                                    timeCapsules = it.data as? StableList<TimeCapsule> ?: StableList(),
                                    onLongClick = {
                                        selectedTimeCapsule = it
                                        showMenuDialog = true
                                    },
                                    onNavigateToDetail = onNavigateToDetail
                                )
                            }

                            TimeCapsuleType.NoneTimeCapsule -> {
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp, vertical = 10.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .width(150.dp)
                                        .height(150.dp)
                                        .background(color = colorResource(id = R.color.light_gray))
                                        .clickable {
                                            onNavigateToAdd()
                                        }
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_add_black),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(50.dp)
                                            .align(Alignment.Center)
                                    )
                                }
                            }

                            TimeCapsuleType.InviteFriend -> {
                                InviteFriendItem(
                                    onNavigateToProfile = onNavigateToProfile
                                )
                            }

                            TimeCapsuleType.Line -> {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(38.dp)
                .background(
                    brush = ShimmerBrush(targetValue = 1300f)
                )
        )

        Box(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .width(150.dp)
                .height(150.dp)
                .background(
                    brush = ShimmerBrush(targetValue = 1300f)
                )
        )

        Box(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(38.dp)
                .background(
                    brush = ShimmerBrush(targetValue = 1300f)
                )
        )

        Box(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .width(240.dp)
                .height(360.dp)
                .background(
                    brush = ShimmerBrush(targetValue = 1300f)
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingScreenPreview() {
    LoadingScreen()
}

@Composable
private fun InviteFriendItem(onNavigateToProfile: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(top = 10.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
            .width(260.dp)
            .height(360.dp)
            .padding(bottom = 10.dp, end = 10.dp)
    ) {
        Box {
            DefaultBackground(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onNavigateToProfile()
                    }
            ) {
                Text(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    text = "친구 추가",
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun OpenedTimeCapsules(
    timeCapsules: StableList<TimeCapsule>,
    onLongClick: (TimeCapsule) -> Unit,
    onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit
) {
    if (timeCapsules.data.isEmpty()) {
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
    ) {
        items(items = timeCapsules.data, key = {
            it.id
        }) {
            OpenedBox(
                timeCapsule = it,
                onClick = {
                    onNavigateToDetail(it.id, it.isReceived)
                },
                onLongClick = {
                    onLongClick(it)
                }
            )
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun LocationDialog(
    timeCapsule: TimeCapsule,
    onDismissRequest: () -> Unit
) {
    val cameraPositionState = rememberCameraPositionState()
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(maxZoom = 20.0, minZoom = 5.0, locationTrackingMode = LocationTrackingMode.NoFollow)
        )
    }
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                isLocationButtonEnabled = false
            )
        )
    }

    cameraPositionState.move(
        CameraUpdate.toCameraPosition(
            CameraPosition(
                LatLng(timeCapsule.lat.toDouble(), timeCapsule.lng.toDouble()),
                15.0
            )
        )
    )

    val desc = if (timeCapsule.checkLocation) {
        "${timeCapsule.openDate} 이후에 ${timeCapsule.placeName} 근처에서 개봉할 수 있습니다."
    } else {
        "${timeCapsule.openDate} 이후에 어디에서나 개봉할 수 있습니다."
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = desc,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                )
                if (timeCapsule.isReceived && timeCapsule.sender.isNotEmpty()) {
                    Text(
                        fontWeight = FontWeight.Bold,
                        text = "공유한 친구 : ${timeCapsule.sender}",
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 10.dp)
                    )
                }

                if (timeCapsule.checkLocation) {
                    Text(
                        fontWeight = FontWeight.Bold,
                        text = timeCapsule.address,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    )

                    NaverMap(
                        cameraPositionState = cameraPositionState,
                        properties = mapProperties,
                        uiSettings = mapUiSettings,
                        modifier = Modifier
                            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                            .fillMaxWidth()
                            .aspectRatio(1.3f)
                            .padding(0.dp)
                    ) {
                        Marker(
                            state = MarkerState(
                                position = LatLng(
                                    timeCapsule.lat.toDouble(),
                                    timeCapsule.lng.toDouble()
                                )
                            ),
                            captionText = timeCapsule.placeName
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth(),
                    ) {
                        Text("확인")
                    }
                }
            }
        }
    }
}

@Composable
private fun OpenableTimeCapsules(
    timeCapsules: StableList<TimeCapsule>,
    currentLat: Double,
    currentLng: Double,
    onShowLocationDialog: (TimeCapsule) -> Unit,
    onShowOpenDialog: (TimeCapsule) -> Unit,
    onLongClick: (TimeCapsule) -> Unit
) {
    if (timeCapsules.data.isEmpty()) {
        return
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        items(items = timeCapsules.data, key = {
            it.id
        }) {
            LockTimeCapsule(
                timeCapsule = it,
                currentLat = currentLat,
                currentLng = currentLng,
                onShowLocationDialog = onShowLocationDialog,
                onShowOpenDialog = onShowOpenDialog,
                onLongClick = {
                    onLongClick(it)
                }
            )
        }
    }
}

@Composable
private fun UnopenedTimeCapsules(
    timeCapsules: StableList<TimeCapsule>,
    onClick: (TimeCapsule) -> Unit,
    onLongClick: (TimeCapsule) -> Unit
) {
    if (timeCapsules.data.isEmpty()) {
        return
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        items(items = timeCapsules.data, key = {
            it.id
        }) {
            LockTimeCapsule(
                timeCapsule = it,
                onClick = onClick,
                onLongClick = onLongClick
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun TimeCapsuleScreenPreview() {
    val unOpenedList = mutableListOf<TimeCapsule>()
    val openedList = mutableListOf<TimeCapsule>()

    repeat(5) {
        if (it % 2 == 0) {
            openedList.add(TimeCapsule(id = "$it", date = "2024-06-28", openDate = "2024-06-28", medias = listOf("")))
            unOpenedList.add(TimeCapsule(id = "$it", openDate = "2024-06-28", medias = listOf("")))
        } else {
            openedList.add(TimeCapsule(id = "$it", date = "2024-06-28", openDate = "2024-06-28", checkLocation = true, medias = listOf("")))
            unOpenedList.add(TimeCapsule(id = "$it", openDate = "2024-06-28", checkLocation = true, medias = listOf("")))
        }
    }

    TimeCapsuleScreen(
        uiState = TimeCapsuleUiState(),
        sideEffect = TimeCapsuleSideEffect.None,
        modifier = Modifier,
        onDeleteTimeCapsule = { _, _ -> },
        onNavigateToAdd = { },
        onNavigateToOpen = { _, _ -> },
        onNavigateToDetail = { _, _ -> },
        onNavigateToNotification = { },
        onNavigateToSetting = { },
        onNavigateToProfile = { },
        onNavigateToMore = { }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OpenedBox(timeCapsule: TimeCapsule, onClick: (TimeCapsule) -> Unit, onLongClick: (TimeCapsule) -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier
            .width(260.dp)
            .padding(bottom = 10.dp, end = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .combinedClickable(
                    onClick = {
                        onClick(timeCapsule)
                    },
                    onLongClick = {
                        onLongClick(timeCapsule)
                    }
                )
        ) {
            Box {
                if (timeCapsule.medias.isNotEmpty()) {
                    GlideImage(
                        imageModel = timeCapsule.medias[0],
                        previewPlaceholder = R.drawable.ic_launcher_background,
                        error = painterResource(id = R.drawable.ic_launcher_background),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.8f)
                    )
                } else {
                    DefaultBackground(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.8f)
                    ) {
                        Text(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            text = "사진이 존재하지 않습니다.",
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }

                Text(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp,
                    text = timeCapsule.date,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .align(Alignment.BottomCenter)
                )
            }
            Text(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                text = if (timeCapsule.isReceived) {
                    "친구"
                } else {
                    "나"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(10.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OpenedBoxPreview() {
    val timeCapsule = TimeCapsule(
        id = "",
        date = "2024-06-24",
        openDate = "2024-06-24",
        lat = "",
        lng = "",
        address = "",
        content = "",
        medias = listOf(""),
        checkLocation = false,
        isOpened = true,
        sharedFriends = listOf(),
        isReceived = false,
        sender = ""
    )

    OpenedBox(timeCapsule, onClick = {}, onLongClick = {})
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LockTimeCapsule(
    timeCapsule: TimeCapsule,
    currentLat: Double = 0.0,
    currentLng: Double = 0.0,
    onShowOpenDialog: ((TimeCapsule) -> Unit)? = null,
    onShowLocationDialog: ((TimeCapsule) -> Unit)? = null,
    onClick: ((TimeCapsule) -> Unit)? = null,
    onLongClick: (TimeCapsule) -> Unit
) {
    val checkLocation = timeCapsule.checkLocation
    val isNear = DistanceManager.getDistance(currentLat, currentLng, timeCapsule.lat.toDouble(), timeCapsule.lng.toDouble()) <= 100
    val canOpen = DateUtil.getDateGap(timeCapsule.openDate) <= 0

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier
            .width(165.dp)
            .padding(bottom = 10.dp, end = 10.dp)
    ) {
        val modifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Modifier
                .blur(16.dp)
        } else {
            Modifier
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        if (canOpen) {
                            if (checkLocation) {
                                if (isNear) {
                                    onShowOpenDialog?.invoke(timeCapsule)
                                } else {
                                    onShowLocationDialog?.invoke(timeCapsule)
                                }
                            } else {
                                onShowOpenDialog?.invoke(timeCapsule)
                            }
                        } else {
                            onClick?.invoke(timeCapsule)
                        }
                    },
                    onLongClick = {
                        onLongClick(timeCapsule)
                    }
                )
        ) {
            Box(
                modifier = Modifier
            ) {
                if (timeCapsule.medias.isNotEmpty()) {
                    GlideImage(
                        imageModel = timeCapsule.medias[0],
                        previewPlaceholder = R.drawable.ic_launcher_background,
                        error = painterResource(id = R.drawable.ic_launcher_background),
                        modifier = modifier
                            .aspectRatio(1f)
                    )
                } else {
                    DefaultBackground(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                    )
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = colorResource(id = R.color.transparent_black))
                    )
                }

                if (canOpen) {
                    Text(
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        text = "개봉 하기",
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.Center)
                    )
                } else {
                    Text(
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        text = "D - ${DateUtil.getDateGap(timeCapsule.openDate)}",
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.TopCenter)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.ic_lock_white),
                        contentDescription = null,
                        modifier = Modifier
                            .width(40.dp)
                            .height(40.dp)
                            .align(Alignment.Center)
                    )
                }

                if (timeCapsule.checkLocation) {
                    Text(
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        text = timeCapsule.placeName,
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                            .align(Alignment.BottomCenter)
                    )
                }
            }
            Text(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                text = if (timeCapsule.isReceived) {
                    "친구"
                } else {
                    "나"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(10.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LockTimeCapsulePreview() {
    val timeCapsule = TimeCapsule(
        medias = listOf("")
    )

    LockTimeCapsule(timeCapsule = timeCapsule, onLongClick = {

    })
}