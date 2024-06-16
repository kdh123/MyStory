package com.dhkim.timecapsule.timecapsule.presentation.detail

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.common.presentation.profileImage
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsule
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

@OptIn(ExperimentalNaverMapApi::class, ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeCapsuleDetailScreen(
    timeCapsuleId: String,
    isReceived: Boolean,
    uiState: TimeCapsuleDetailUiState,
    init: (String, Boolean) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
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
    var enableScroll by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(uiState) {
        cameraPositionState.move(
            CameraUpdate.toCameraPosition(
                CameraPosition(
                    LatLng(uiState.timeCapsule.lat.toDouble(), uiState.timeCapsule.lng.toDouble()),
                    15.0
                )
            )
        )
        init(timeCapsuleId, isReceived)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(
                    scrollState,
                    enabled = enableScroll
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back_black),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 10.dp)
                    .background(color = Color.White)
                    .clickable {
                        onBack()
                    }
            )
            val writer = if (!isReceived) {
                "${uiState.timeCapsule.sender} (나)"
            } else {
                "${uiState.timeCapsule.sender} (친구)"
            }
            TimeCapsulePager(uiState = uiState)
            MenuItem(resId = uiState.timeCapsule.host.profileImage.profileImage(), title = "작성자 : $writer")
            Divider(
                color = colorResource(id = R.color.light_gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(1.dp)
            )
            MenuItem(resId = R.drawable.ic_calender_black, title = uiState.timeCapsule.date)
            Divider(
                color = colorResource(id = R.color.light_gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(1.dp)
            )
            Text(
                text = uiState.timeCapsule.content,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 30.dp)
            )

            Divider(
                color = colorResource(id = R.color.light_gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(1.dp)
            )

            LaunchedEffect(cameraPositionState.isMoving) {
                enableScroll = !cameraPositionState.isMoving
            }
            if (uiState.timeCapsule.checkLocation || !isReceived) {
                MenuItem(resId = R.drawable.ic_location_black, title = uiState.timeCapsule.address)
                NaverMap(
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = mapUiSettings,
                    modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                        .fillMaxWidth()
                        .aspectRatio(1.3f)
                        .padding(0.dp)
                        .pointerInteropFilter(
                            onTouchEvent = {
                                when (it.action) {
                                    MotionEvent.ACTION_DOWN -> {
                                        enableScroll = false // 터치 시작 시 화면 스크롤 비활성화
                                        false // 이벤트를 지도에 넘김
                                    }

                                    else -> true
                                }
                            }
                        )
                ) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                uiState.timeCapsule.lat.toDouble(),
                                uiState.timeCapsule.lng.toDouble()
                            )
                        ),
                        captionText = uiState.timeCapsule.address
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItem(resId: Int, title: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 30.dp, horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 10.dp)
                .width(24.dp)
                .height(24.dp)
        )
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
            fontSize = 18.sp
        )
    }
}

@Preview
@Composable
private fun TimeCapsuleDetailScreenPreview() {
    val timeCapsule = TimeCapsule(
        address = "서울시 강남구 압구정동",
        date = "2024-07-29",
        content = "안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 "
    )
    TimeCapsuleDetailScreen(
        timeCapsuleId = "",
        isReceived = false,
        uiState = TimeCapsuleDetailUiState(timeCapsule = timeCapsule),
        init = { _, _ ->

        },
        onBack = {

        }
    )
}

@Composable
fun TimeCapsulePager(uiState: TimeCapsuleDetailUiState) {
    val timeCapsule = uiState.timeCapsule
    val images: List<String> = timeCapsule.medias
    val pagerState = rememberPagerState(pageCount = {
        images.size
    })

    var currentPage by remember {
        mutableIntStateOf(1)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            currentPage = page + 1
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        if (images.isNotEmpty()) {
            HorizontalPager(
                state = pagerState
            ) { page ->
                GlideImage(
                    imageModel = images[page],
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
            Text(
                text = "$currentPage / ${images.size}",
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = colorResource(id = R.color.transparent_gray))
                    .padding(10.dp),
                color = colorResource(id = R.color.black)
            )
        } else {
            val brush = Brush.linearGradient(
                listOf(Color(0XFF3C5AFA), Color(0XFFF361DC))
            )
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
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
    }
}