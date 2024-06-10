package com.dhkim.timecapsule.timecapsule

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhkim.timecapsule.BuildConfig
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.common.DateUtil
import com.dhkim.timecapsule.common.composable.drawAnimatedBorder
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsule
import com.dhkim.timecapsule.timecapsule.presentation.TimeCapsuleSideEffect
import com.dhkim.timecapsule.timecapsule.presentation.TimeCapsuleUiState
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun TimeCapsuleScreen(
    uiState: TimeCapsuleUiState,
    sideEffect: TimeCapsuleSideEffect,
    modifier: Modifier = Modifier,
    openTimeCapsule: (TimeCapsule) -> Unit,
    onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    shareTimeCapsule: (
        friends: List<String>,
        openDate: String,
        content: String,
        lat: String,
        lng: String,
        address: String,
        checkLocation: Boolean
    ) -> Unit,
) {
    val context = LocalContext.current
    var myTabSelect by remember {
        mutableStateOf(true)
    }
    var receivedTabSelect by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(sideEffect) {
        when (sideEffect) {
            is TimeCapsuleSideEffect.None -> {}

            is TimeCapsuleSideEffect.Message -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is TimeCapsuleSideEffect.NavigateToDetail -> {
                onNavigateToDetail(sideEffect.id, sideEffect.isReceived)
            }
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (false && BuildConfig.DEBUG) {
            Text(
                color = Color.White,
                text = "FCM",
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .background(color = colorResource(id = R.color.primary))
                    .clickable {
                        shareTimeCapsule(
                            listOf("3176197071"),
                            "2024-09-10",
                            "마지막11",
                            "23.233",
                            "23.4555",
                            "부산시 동래구 온천동",
                            true
                        )
                    })
        }

        if (uiState.openedTimeCapsules.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
            ) {
                items(items = uiState.openedTimeCapsules, key = {
                    it.id
                }) {
                    if (it.isOpened) {
                        OpenedBox(
                            timeCapsule = it,
                            onClick = openTimeCapsule
                        )
                    } else {
                        OpenableBox(
                            timeCapsule = it,
                            onClick = openTimeCapsule
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
        ) {
            TabBox(
                isSelected = myTabSelect,
                title = "나",
                modifier = Modifier
                    .width(0.dp)
                    .weight(1f)
                    .padding(end = 10.dp),
                onClick = {
                    myTabSelect = it
                    receivedTabSelect = !it
                }
            )

            TabBox(
                isSelected = receivedTabSelect,
                title = "수신",
                modifier = Modifier
                    .width(0.dp)
                    .weight(1f),
                onClick = {
                    receivedTabSelect = it
                    myTabSelect = !it
                }
            )
        }

        if (myTabSelect) {
            MyTimeCapsuleScreen(uiState = uiState)
        } else {
            ReceivedTimeCapsuleScreen(uiState = uiState)
        }
    }
}

@Composable
fun OpenedBox(timeCapsule: TimeCapsule, onClick: (TimeCapsule) -> Unit) {
    Box(
        modifier = Modifier
            .clickable {
                onClick(timeCapsule)
            }
    ) {
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .border(
                    width = 5.dp,
                    shape = CircleShape,
                    color = colorResource(id = R.color.primary)
                )
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = colorResource(id = R.color.white))
            ) {
                if (timeCapsule.medias.isNotEmpty()) {
                    GlideImage(
                        imageModel = timeCapsule.medias[0],
                        placeHolder = painterResource(R.mipmap.ic_box),
                        previewPlaceholder = R.mipmap.ic_box,
                        error = painterResource(id = R.mipmap.ic_box),
                        modifier = Modifier
                            .clip(CircleShape)
                            .width(78.dp)
                            .height(78.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_box),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .width(78.dp)
                            .height(78.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(color = colorResource(id = R.color.white))
                .align(Alignment.BottomEnd)

        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_check_primary),
                contentDescription = null,
                modifier = Modifier
                    .width(30.dp)
                    .height(30.dp)
                    .border(
                        width = 1.dp,
                        shape = CircleShape,
                        color = Color.White
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OpenedBoxPreview() {
    val timeCapsule = TimeCapsule(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        listOf(),
        false,
        true,
        listOf(),
        false,
        ""
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

@Preview(showBackground = true)
@Composable
private fun OpenableBoxPreview() {
    OpenableBox(
        timeCapsule = TimeCapsule(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            listOf(),
            false,
            false,
            listOf(),
            false,
            ""
        ),
        onClick = { _ ->

        }
    )
}

@Preview
@Composable
private fun OpenedBoxListPreview() {

}

@Composable
fun OpenedBoxList(uiState: TimeCapsuleUiState) {

}

@Composable
fun TabBox(isSelected: Boolean, title: String, modifier: Modifier = Modifier, onClick: (Boolean) -> Unit) {
    val fontWeight = if (isSelected) {
        FontWeight.Bold
    } else {
        FontWeight.Normal
    }

    val fontColor = if (isSelected) {
        Color.White
    } else {
        colorResource(id = R.color.black)
    }

    val background = if (isSelected) {
        colorResource(id = R.color.primary)
    } else {
        colorResource(id = R.color.light_gray)
    }

    Text(
        textAlign = TextAlign.Center,
        fontWeight = fontWeight,
        color = fontColor,
        text = title,
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(background)
            .clickable {
                onClick(!isSelected)
            }
            .padding(vertical = 10.dp),
    )
}

@Composable
fun ReceivedTimeCapsuleScreen(uiState: TimeCapsuleUiState) {
    val timeCapsules = uiState.unOpenedReceivedTimeCapsules

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        itemsIndexed(
            items = timeCapsules, key = { _, item ->
                item.id
            }
        ) { index, item ->
            if (DateUtil.getDateGap(newDate = item.openDate) > 0) {
                if (index == timeCapsules.size - 1) {
                    ReceivedTimeCapsuleItem(timeCapsule = item)
                } else {
                    ReceivedTimeCapsuleItem(timeCapsule = item)
                    Divider(
                        color = colorResource(id = R.color.light_gray),
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ReceivedTimeCapsuleItem(timeCapsule: TimeCapsule) {
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
                        Text(
                            text = "후에 오픈할 수 있습니다.",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                    }
                    Text(
                        text = "${timeCapsule.sender}님에 의해 공유되었습니다.",
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

@Composable
fun MyTimeCapsuleScreen(uiState: TimeCapsuleUiState) {
    val unOpenedTimeCapsules = uiState.unOpenedMyTimeCapsules

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        itemsIndexed(
            items = unOpenedTimeCapsules, key = { _, item ->
                item.id
            }
        ) { index, item ->
            if (DateUtil.getDateGap(newDate = item.openDate) > 0) {
                if (index == unOpenedTimeCapsules.size - 1) {
                    TimeCapsuleItem(timeCapsule = item)
                } else {
                    TimeCapsuleItem(timeCapsule = item)
                    Divider(
                        color = colorResource(id = R.color.light_gray),
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
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
                        Text(
                            text = "후에 오픈할 수 있습니다.",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
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

@Preview(showBackground = true)
@Composable
private fun TimeCapsuleScreenPreview() {
    TimeCapsuleScreen(TimeCapsuleUiState(), TimeCapsuleSideEffect.None, modifier = Modifier, {}, {_, _ ->}) { _, _, _, _, _, _, _ ->

    }
}