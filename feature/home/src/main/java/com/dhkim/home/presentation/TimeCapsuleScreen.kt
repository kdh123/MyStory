@file:Suppress("UNCHECKED_CAST")

package com.dhkim.home.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dhkim.common.DateUtil
import com.dhkim.common.DistanceManager
import com.dhkim.designsystem.MyStoryTheme
import com.dhkim.home.R
import com.dhkim.story.domain.model.TimeCapsule
import com.dhkim.ui.DefaultBackground
import com.dhkim.ui.Popup
import com.dhkim.ui.ShimmerBrush
import com.dhkim.ui.onStartCollect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TimeCapsuleScreen(
    uiState: TimeCapsuleUiState,
    sideEffect: () -> Flow<TimeCapsuleSideEffect>,
    permissionState: PermissionState,
    modifier: Modifier = Modifier,
    requestPermission: @Composable () -> Unit,
    onDeleteTimeCapsule: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToOpen: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToSetting: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMore: () -> Unit,
    showPopup: (Popup) -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current
    val context = LocalContext.current
    var selectedTimeCapsule by remember {
        mutableStateOf(TimeCapsule())
    }
    var showLocationDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showMenuDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showMenuDialog) {
        Dialog(
            onDismissRequest = {
                selectedTimeCapsule = TimeCapsule()
                showMenuDialog = false
            }
        ) {
            Card(
                modifier = modifier
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
                                val desc =
                                    if (selectedTimeCapsule.sharedFriends.isNotEmpty() && !selectedTimeCapsule.isReceived) {
                                        "이 타임캡슐을 공유했던 친구들 디바이스에서도 삭제가 됩니다. 정말 삭제하겠습니까?"
                                    } else {
                                        "정말 삭제하겠습니까?"
                                    }

                                showPopup(
                                    Popup.Warning(
                                        title = "삭제",
                                        desc = desc,
                                        onPositiveClick = {
                                            onDeleteTimeCapsule(
                                                selectedTimeCapsule.id,
                                                selectedTimeCapsule.isReceived
                                            )
                                        }
                                    )
                                )
                            }
                    )
                }
            }
        }
    }

    if (showLocationDialog) {
        LocationDialog(
            timeCapsule = selectedTimeCapsule,
            onDismissRequest = {
                showLocationDialog = false
            }
        )
    }

    if (!permissionState.status.isGranted && permissionState.status.shouldShowRationale) {
        // Show rationale if needed
    } else {
        requestPermission()
    }

    lifecycle.onStartCollect(sideEffect()) {
        when (it) {
            is TimeCapsuleSideEffect.None -> {}

            is TimeCapsuleSideEffect.Message -> {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }

            is TimeCapsuleSideEffect.NavigateToOpen -> {
                onNavigateToOpen(it.id, it.isReceived)
            }

            is TimeCapsuleSideEffect.NavigateToDetail -> {
                onNavigateToDetail(it.id, it.isReceived)
            }
        }
    }

    Scaffold(
        topBar = {
            Row {
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

                Icon(
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

                Icon(
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
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                LoadingScreen()
            } else {
                LazyColumn(
                    modifier = modifier
                        .testTag("timeCapsuleItems")
                ) {
                    items(uiState.timeCapsules, key = { it.id }) {
                        when (it.type) {
                            TimeCapsuleType.Title -> {
                                val title = it.data as? String ?: ""
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                ) {
                                    Text(
                                        text = it.data as? String ?: "",
                                        style = MyStoryTheme.typography.headlineSmallBold,
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
                                    timeCapsules = (it.data as? List<TimeCapsule>
                                        ?: listOf()).toImmutableList(),
                                    currentLat = uiState.currentLocation.latitude,
                                    currentLng = uiState.currentLocation.longitude,
                                    onShowLocationDialog = {
                                        if (!permissionState.status.isGranted) {
                                            showPopup(
                                                Popup.Warning(
                                                    title = "위치 권한 요청",
                                                    desc = "타임캡슐을 개봉하기 위해서 위치 권한을 허용해주세요.",
                                                    onPositiveClick = {
                                                        val uri = Uri.fromParts("package", context.packageName, null)
                                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                            data = uri
                                                        }
                                                        context.startActivity(intent)
                                                    }
                                                )
                                            )
                                        } else {
                                            selectedTimeCapsule = it
                                            showLocationDialog = true
                                        }
                                    },
                                    onShowOpenDialog = {
                                        showPopup(
                                            Popup.Warning(
                                                title = "개봉",
                                                desc = "정말 개봉하겠습니까?",
                                                onPositiveClick = {
                                                    onNavigateToOpen(it.id, it.isReceived)
                                                }
                                            )
                                        )
                                    },
                                    onLongClick = {
                                        selectedTimeCapsule = it
                                        showMenuDialog = true
                                    }
                                )
                            }

                            TimeCapsuleType.UnopenedTimeCapsule -> {
                                UnopenedTimeCapsules(
                                    timeCapsules = (it.data as? List<TimeCapsule> ?: listOf()).toImmutableList(),
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
                                    timeCapsules = (it.data as? List<TimeCapsule> ?: listOf()).toImmutableList(),
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
                                        .clickable(onClick = onNavigateToAdd)
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
                                InviteFriendItem(onNavigateToProfile = onNavigateToProfile)
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
                    .clickable(onClick = onNavigateToProfile)
            ) {
                Text(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
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
    timeCapsules: ImmutableList<TimeCapsule>,
    onLongClick: (TimeCapsule) -> Unit,
    onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit
) {
    if (timeCapsules.isEmpty()) {
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
    ) {
        items(items = timeCapsules, key = {
            it.id
        }) {
            OpenedBox(
                timeCapsule = it,
                onClick = {
                    onNavigateToDetail(it.id, it.isReceived)
                },
                onLongClick = onLongClick
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
            MapProperties(
                maxZoom = 20.0,
                minZoom = 5.0,
                locationTrackingMode = LocationTrackingMode.NoFollow
            )
        )
    }
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(isLocationButtonEnabled = false)
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
                        text = "공유한 친구 : ${timeCapsule.host.nickname}",
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
    timeCapsules: ImmutableList<TimeCapsule>,
    currentLat: Double,
    currentLng: Double,
    onShowLocationDialog: (TimeCapsule) -> Unit,
    onShowOpenDialog: (TimeCapsule) -> Unit,
    onLongClick: (TimeCapsule) -> Unit
) {
    if (timeCapsules.isEmpty()) {
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .testTag("openableTimeCapsules")
    ) {
        items(items = timeCapsules, key = {
            it.id
        }) {
            LockTimeCapsule(
                timeCapsule = it,
                currentLat = currentLat,
                currentLng = currentLng,
                onShowLocationDialog = onShowLocationDialog,
                onShowOpenDialog = onShowOpenDialog,
                onLongClick = onLongClick
            )
        }
    }
}

@Composable
private fun UnopenedTimeCapsules(
    timeCapsules: ImmutableList<TimeCapsule>,
    onClick: (TimeCapsule) -> Unit,
    onLongClick: (TimeCapsule) -> Unit
) {
    if (timeCapsules.isEmpty()) {
        return
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .testTag("unopenedTimeCapsules")
    ) {
        items(items = timeCapsules, key = {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OpenedBox(
    timeCapsule: TimeCapsule,
    onClick: (TimeCapsule) -> Unit,
    onLongClick: (TimeCapsule) -> Unit
) {
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
                if (timeCapsule.images.isNotEmpty()) {
                    GlideImage(
                        imageModel = { timeCapsule.images[0] },
                        previewPlaceholder = painterResource(id = R.drawable.ic_launcher_background),
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
    val isNear = DistanceManager.getDistance(
        currentLat,
        currentLng,
        timeCapsule.lat.toDouble(),
        timeCapsule.lng.toDouble()
    ) <= 100
    val canOpen = DateUtil.getDateGap(timeCapsule.openDate) <= 0

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier
            .width(165.dp)
            .padding(bottom = 10.dp, end = 10.dp)
            .testTag("lockTimeCapsule${timeCapsule.id}")
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
                if (timeCapsule.images.isNotEmpty()) {
                    GlideImage(
                        imageModel = { timeCapsule.images[0] },
                        previewPlaceholder = painterResource(id = R.drawable.ic_launcher_background),
                        modifier = modifier
                            .aspectRatio(1f)
                    )

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .background(color = colorResource(id = R.color.transparent_black))
                        )
                    }
                } else {
                    DefaultBackground(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
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
                text = if (timeCapsule.isReceived) "친구" else "나",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(10.dp)
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
class DefaultPermissionState : PermissionState {
    override val permission: String
        get() = ""
    override val status: PermissionStatus
        get() = PermissionStatus.Granted

    override fun launchPermissionRequest() {

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TimeCapsuleScreenDarkPreview() {
    val unOpenedList = mutableListOf<TimeCapsule>()
    val openedList = mutableListOf<TimeCapsule>()

    repeat(10) {
        if (it % 2 == 0) {
            openedList.add(
                TimeCapsule(
                    id = "$it",
                    date = "2024-06-28",
                    openDate = "2024-06-28",
                    images = listOf("")
                )
            )
            unOpenedList.add(TimeCapsule(id = "$it", openDate = "2024-06-28", images = listOf("")))
        } else if (it % 3 == 0) {
            openedList.add(
                TimeCapsule(
                    id = "$it",
                    date = "2024-06-28",
                    openDate = "2024-06-28",
                    images = listOf(""),
                    isOpened = true
                )
            )
        } else {
            openedList.add(
                TimeCapsule(
                    id = "$it",
                    date = "2024-06-28",
                    openDate = "2024-06-28",
                    checkLocation = true,
                    images = listOf("")
                )
            )
            unOpenedList.add(
                TimeCapsule(
                    id = "$it",
                    openDate = "2099-12-24",
                    checkLocation = true,
                    images = listOf("")
                )
            )
        }
    }

    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TimeCapsuleScreen(
                uiState = TimeCapsuleUiState(isLoading = false, timeCapsules = (unOpenedList + openedList).toItems(spaceId = 100).toImmutableList()),
                sideEffect = { flowOf() },
                permissionState = DefaultPermissionState(),
                requestPermission = {},
                onDeleteTimeCapsule = { _, _ -> },
                onNavigateToAdd = { },
                onNavigateToOpen = { _, _ -> },
                onNavigateToDetail = { _, _ -> },
                onNavigateToNotification = { },
                onNavigateToSetting = { },
                onNavigateToProfile = { },
                onNavigateToMore = { },
                showPopup = {}
            )
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TimeCapsuleScreenPreview() {
    val unOpenedList = mutableListOf<TimeCapsule>()
    val openedList = mutableListOf<TimeCapsule>()

    repeat(10) {
        if (it % 2 == 0) {
            openedList.add(
                TimeCapsule(
                    id = "$it",
                    date = "2024-06-28",
                    openDate = "2024-06-28",
                    images = listOf("")
                )
            )
            unOpenedList.add(TimeCapsule(id = "$it", openDate = "2024-06-28", images = listOf("")))
        } else if (it % 3 == 0) {
            openedList.add(
                TimeCapsule(
                    id = "$it",
                    date = "2024-06-28",
                    openDate = "2024-06-28",
                    images = listOf(""),
                    isOpened = true
                )
            )
        } else {
            openedList.add(
                TimeCapsule(
                    id = "$it",
                    date = "2024-06-28",
                    openDate = "2024-06-28",
                    checkLocation = true,
                    images = listOf("")
                )
            )
            unOpenedList.add(
                TimeCapsule(
                    id = "$it",
                    openDate = "2099-12-24",
                    checkLocation = true,
                    images = listOf("")
                )
            )
        }
    }

    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TimeCapsuleScreen(
                uiState = TimeCapsuleUiState(isLoading = false, timeCapsules = (unOpenedList + openedList).toItems(spaceId = 100).toImmutableList()),
                sideEffect = { flowOf() },
                permissionState = DefaultPermissionState(),
                requestPermission = {},
                onDeleteTimeCapsule = { _, _ -> },
                onNavigateToAdd = { },
                onNavigateToOpen = { _, _ -> },
                onNavigateToDetail = { _, _ -> },
                onNavigateToNotification = { },
                onNavigateToSetting = { },
                onNavigateToProfile = { },
                onNavigateToMore = { },
                showPopup = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingScreenPreview() {
    LoadingScreen()
}