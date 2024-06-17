package com.dhkim.timecapsule.timecapsule

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.common.Constants
import com.dhkim.timecapsule.common.DateUtil
import com.dhkim.timecapsule.common.composable.drawAnimatedBorder
import com.dhkim.timecapsule.common.presentation.DistanceManager
import com.dhkim.timecapsule.timecapsule.domain.Host
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsule
import com.dhkim.timecapsule.timecapsule.presentation.TimeCapsuleSideEffect
import com.dhkim.timecapsule.timecapsule.presentation.TimeCapsuleUiState
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

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TimeCapsuleScreen(
    uiState: TimeCapsuleUiState,
    sideEffect: TimeCapsuleSideEffect,
    modifier: Modifier = Modifier,
    onNavigateToAdd: () -> Unit,
    onNavigateToOpen: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToNotification: () -> Unit
) {
    var currentLocation by remember {
        mutableStateOf(Constants.defaultLocation)
    }
    var selectedTimeCapsule by remember {
        mutableStateOf(TimeCapsule())
    }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showLocationDialog by remember {
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
            // Permission granted
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    currentLocation = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                }
        } else {
            // Handle permission denial
        }
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

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
    ) {
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
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                    .width(32.dp)
                    .height(32.dp)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        onNavigateToNotification()
                    }
            )
        }

        OpenableTimeCapsules(
            uiState = uiState,
            currentLat = currentLocation.latitude,
            currentLng = currentLocation.longitude,
            onShowLocationDialog = {
                selectedTimeCapsule = it
                showLocationDialog = true
            },
            onNavigateToOpen = onNavigateToOpen
        )
        UnopenedTimeCapsules(uiState = uiState, onNavigateToAdd = onNavigateToAdd, onShowDetailBottom = { })
        OpenedTimeCapsules(uiState = uiState, onNavigateToDetail = onNavigateToDetail)
        if (uiState.openedTimeCapsules.isEmpty()) {
            GuideItem()
        }
    }
}

@Composable
private fun GuideItem() {
    val brush = Brush.linearGradient(
        listOf(Color(0XFF3C5AFA), Color(0XFFF361DC))
    )
    Box {
        Canvas(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(color = colorResource(id = R.color.primary))
                .width(240.dp)
                .height(360.dp),
            onDraw = {
                drawRect(brush)
            }
        )
        Text(
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            text = "타임캡슐 가이드",
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun OpenedTimeCapsules(
    uiState: TimeCapsuleUiState,
    onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit
) {
    Text(
        text = "오픈한 타임캡슐",
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, top = 10.dp),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )

    if (uiState.openedTimeCapsules.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
        ) {
            items(items = uiState.openedTimeCapsules, key = {
                it.id
            }) {
                OpenedBox(
                    timeCapsule = it,
                    onClick = {
                        onNavigateToDetail(it.id, it.isReceived)
                    }
                )
            }
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

    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(10.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "${timeCapsule.address} 근처에 있어야 오픈할 수 있습니다.",
                    modifier = Modifier.padding(16.dp),
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
                        captionText = timeCapsule.address
                    )
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
    uiState: TimeCapsuleUiState,
    currentLat: Double,
    currentLng: Double,
    onShowLocationDialog: ((TimeCapsule) -> Unit),
    onNavigateToOpen: (timeCapsuleId: String, isReceived: Boolean) -> Unit
) {
    if (uiState.openableTimeCapsules.isEmpty()) {
        return
    }

    Text(
        text = "오늘 오픈할 수 있는 타임캡슐",
        modifier = Modifier
            .padding(horizontal = 10.dp),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        items(items = uiState.openableTimeCapsules, key = {
            it.id
        }) {
            LockTimeCapsule(
                timeCapsule = it,
                currentLat = currentLat,
                currentLng = currentLng,
                onShowLocationDialog = onShowLocationDialog,
                onNavigateToOpen = onNavigateToOpen
            )
        }
    }
}

@Composable
private fun UnopenedTimeCapsules(uiState: TimeCapsuleUiState, onNavigateToAdd: () -> Unit, onShowDetailBottom: ((TimeCapsule) -> Unit)) {
    Text(
        text = "미개봉 타임캡슐",
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, top = 10.dp),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )

    if (uiState.unOpenedTimeCapsules.isEmpty()) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .width(150.dp)
                .height(150.dp)
                .background(color = colorResource(id = R.color.light_gray))
                .clickable {
                    onNavigateToAdd()
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_add_primary),
                contentDescription = null,
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .align(Alignment.Center)
            )
        }
        return
    }

    if (uiState.unOpenedTimeCapsules.any { !it.checkLocation }) {
        Text(
            text = "아무 장소에서나 열 수 있는 타임캡슐이에요",
            modifier = Modifier
                .padding(top = 10.dp, start = 10.dp, end = 10.dp),
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            items(items = uiState.unOpenedTimeCapsules.filter { !it.checkLocation }, key = {
                it.id
            }) {
                LockTimeCapsule(timeCapsule = it, onShowDetailBottom = onShowDetailBottom)
            }
        }
    }

    if (uiState.unOpenedTimeCapsules.any { it.checkLocation }) {
        Text(
            text = "특정 장소에서만 열 수 있는 타임캡슐이에요",
            modifier = Modifier
                .padding(top = 10.dp, start = 10.dp, end = 10.dp),
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            items(items = uiState.unOpenedTimeCapsules.filter { it.checkLocation }, key = {
                it.id
            }) {
                LockTimeCapsule(timeCapsule = it)
            }
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
        uiState = TimeCapsuleUiState(
            openedTimeCapsules = openedList,
            unOpenedTimeCapsules = unOpenedList.toList()
        ),
        sideEffect = TimeCapsuleSideEffect.None,
        modifier = Modifier,
        onNavigateToAdd = { },
        onNavigateToOpen = { _, _ -> },
        onNavigateToDetail = { _, _ -> },
        onNavigateToNotification = { }
    )
}

@Composable
fun OpenedBox(timeCapsule: TimeCapsule, onClick: (TimeCapsule) -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .width(240.dp)
            .height(360.dp)
    ) {
        if (timeCapsule.medias.isNotEmpty()) {
            GlideImage(
                imageModel = timeCapsule.medias[0],
                previewPlaceholder = R.drawable.ic_launcher_background,
                error = painterResource(id = R.drawable.ic_launcher_background),
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onClick(timeCapsule)
                    }
            )
        } else {
            val brush = Brush.linearGradient(
                listOf(Color(0XFF3C5AFA), Color(0XFFF361DC))
            )
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        onClick(timeCapsule)
                    },
                onDraw = {
                    drawRect(brush)
                }
            )
            Text(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                text = "사진이 존재하지 않습니다.",
                modifier = Modifier
                    .align(Alignment.Center)
            )
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

    OpenedBox(timeCapsule) {

    }
}

@Composable
fun OpenableBox(timeCapsule: TimeCapsule, onClick: (TimeCapsule) -> Unit) {
    Box(
        modifier = Modifier
            .padding(start = 10.dp)
            .drawAnimatedBorder(
                strokeWidth = 5.dp,
                shape = CircleShape,
                durationMillis = 2000
            )
            .clickable {
                onClick(timeCapsule)
            }
    ) {
        Box(
            modifier = Modifier
                .padding(7.dp)
                .clip(CircleShape)
                .width(78.dp)
                .height(78.dp)
                .background(color = colorResource(id = R.color.white))
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_box),
                contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun LockTimeCapsule(
    timeCapsule: TimeCapsule,
    currentLat: Double = 0.0,
    currentLng: Double = 0.0,
    onNavigateToOpen: ((timeCapsuleId: String, isReceived: Boolean) -> Unit)? = null,
    onShowLocationDialog: ((TimeCapsule) -> Unit)? = null,
    onShowDetailBottom: ((TimeCapsule) -> Unit)? = null
) {
    val checkLocation = timeCapsule.checkLocation
    val isNear = DistanceManager.getDistance(currentLat, currentLng, timeCapsule.lat.toDouble(), timeCapsule.lng.toDouble()) <= 100
    val canOpen = DateUtil.getDateGap(timeCapsule.openDate) <= 0

    Box(
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                if (canOpen) {
                    if (checkLocation) {
                        if (isNear) {
                            onNavigateToOpen?.invoke(timeCapsule.id, timeCapsule.isReceived)
                        } else {
                            onShowLocationDialog?.invoke(timeCapsule)
                        }
                    } else {
                        onNavigateToOpen?.invoke(timeCapsule.id, timeCapsule.isReceived)
                    }
                } else {
                    onShowDetailBottom?.invoke(timeCapsule)
                }
            }
    ) {
        val modifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Modifier
                .fillMaxSize()
                .blur(18.dp)
        } else {
            Modifier
                .fillMaxSize()
        }

        Box(
            modifier = modifier
        ) {
            GlideImage(
                imageModel = if (timeCapsule.medias.isNotEmpty()) {
                    timeCapsule.medias[0]
                } else {
                    R.drawable.ic_launcher_background
                },
                previewPlaceholder = R.drawable.ic_launcher_background,
                error = painterResource(id = R.drawable.ic_launcher_background),
                modifier = Modifier
                    .fillMaxSize()
            )

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colorResource(id = R.color.transparent_black))
                ) {

                }
            }
        }

        if (canOpen) {
            Text(
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                text = "오픈 하기",
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
                text = "D + ${DateUtil.getDateGap(timeCapsule.openDate)}",
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
                text = timeCapsule.address,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                    .align(Alignment.BottomCenter)
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

    LockTimeCapsule(timeCapsule = timeCapsule)
}

@Preview(showBackground = true)
@Composable
private fun OpenableBoxPreview() {
    OpenableBox(
        timeCapsule = TimeCapsule(
            id = "",
            host = Host(),
            date = "",
            openDate = "",
            lat = "",
            lng = "",
            address = "",
            content = "",
            medias = listOf(),
            checkLocation = false,
            isOpened = false,
            sharedFriends = listOf(),
            isReceived = false,
            sender = ""
        ),
        onClick = { _ ->

        }
    )
}

@Composable
fun TimeCapsuleItem(timeCapsule: TimeCapsule) {
    val leftTime = DateUtil.getDateGap(newDate = timeCapsule.openDate)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_box),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Column(
            modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(start = 5.dp)
        ) {
            if (leftTime <= 0) {
                Text(
                    text = "오픈 하기",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.primary)
                )
            } else {
                Column {
                    Row {
                        Text(
                            text = "${leftTime}일 ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        if (timeCapsule.checkLocation && timeCapsule.address.isNotEmpty()) {
                            Text(
                                text = "후에 ${timeCapsule.address}에서 오픈할 수 있습니다",
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .clickable {

                                    }
                            )
                        } else {
                            Text(
                                text = "후에 오픈할 수 있습니다.",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                    if (timeCapsule.sharedFriends.isNotEmpty()) {
                        val count = timeCapsule.sharedFriends.size
                        val sharedText = if (count == 1) {
                            "${timeCapsule.sharedFriends[0]}님에게 공유하였습니다."
                        } else {
                            "${timeCapsule.sharedFriends[0]}님 외 ${count - 1}명에게 공유하였습니다."
                        }
                        Text(
                            text = sharedText,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable {

                                }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTimeCapsulePreview() {
    val timeCapsule = TimeCapsule(
        id = "id1",
        date = "2024-06-01",
        openDate = "2024-10-15",
        lat = "13.14",
        lng = "13.14",
        address = "서울시 강남구 강남동",
        content = "내 타임캡슐",
        medias = listOf(),
        checkLocation = true,
        isOpened = false,
        sharedFriends = listOf(),
        isReceived = false,
        sender = ""
    )

    TimeCapsuleItem(timeCapsule = timeCapsule)
}