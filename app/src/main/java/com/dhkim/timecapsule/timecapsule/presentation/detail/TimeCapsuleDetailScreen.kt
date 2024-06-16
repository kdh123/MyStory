package com.dhkim.timecapsule.timecapsule.presentation.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.common.presentation.profileImage
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsule
import com.skydoves.landscapist.glide.GlideImage

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeCapsuleDetailScreen(
    timeCapsuleId: String,
    isReceived: Boolean,
    uiState: TimeCapsuleDetailUiState,
    init: (String, Boolean) -> Unit
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState) {
        init(timeCapsuleId, isReceived)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
        ) {
            val writer = if (!isReceived) {
                "${uiState.timeCapsule.sender} (나)"
            } else {
                "${uiState.timeCapsule.sender} (친구)"
            }
            if (uiState.timeCapsule.medias.isNotEmpty()) {
                TimeCapsulePager(uiState = uiState)
            }
            MenuItem(resId = R.drawable.ic_location_black, title = uiState.timeCapsule.address)
            Divider(
                color = colorResource(id = R.color.light_gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(1.dp)
            )
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
                    .padding(20.dp)
            )
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

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            HorizontalPager(
                state = pagerState
            ) { page ->
                GlideImage(
                    imageModel = images[page],
                    contentDescription = "pet image",
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
        }
    }
}