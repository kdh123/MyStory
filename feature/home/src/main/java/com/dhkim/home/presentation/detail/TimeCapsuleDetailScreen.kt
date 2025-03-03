package com.dhkim.home.presentation.detail

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dhkim.designsystem.MyStoryTheme
import com.dhkim.home.R
import com.dhkim.story.domain.model.TimeCapsule
import com.dhkim.ui.DefaultBackground
import com.dhkim.ui.LoadingProgressBar
import com.dhkim.ui.Popup
import com.dhkim.ui.onStartCollect
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeCapsuleDetailScreen(
    uiState: TimeCapsuleDetailUiState,
    sideEffect: () -> Flow<TimeCapsuleDetailSideEffect>,
    onAction: (TimeCapsuleDetailAction) -> Unit,
    enableScroll: Boolean,
    onNavigateToImageDetail: (String, String) -> Unit,
    showPopup: (Popup) -> Unit,
    onBack: () -> Unit,
    naverMap: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    val scrollState = rememberScrollState()
    var showOption by rememberSaveable {
        mutableStateOf(false)
    }

    lifecycle.onStartCollect(sideEffect()) {
        when (it) {
            is TimeCapsuleDetailSideEffect.Completed -> {
                onBack()
            }

            is TimeCapsuleDetailSideEffect.Message -> {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        if (showOption) {
            ModalBottomSheet(
                modifier = Modifier
                    .padding(bottom = it.calculateBottomPadding()),
                onDismissRequest = {
                    showOption = false
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, bottom = 48.dp)
                        .fillMaxWidth()
                        .clickable {
                            showOption = false
                            val desc = if (uiState.timeCapsule.sharedFriends.isNotEmpty() && !uiState.timeCapsule.isReceived) {
                                "이 타임캡슐을 공유했던 친구들 디바이스에서도 삭제가 됩니다. 정말 삭제하겠습니까?"
                            } else {
                                "정말 삭제하겠습니까?"
                            }

                            showPopup(
                                Popup.Warning(
                                    title = "삭제",
                                    desc = desc,
                                    onPositiveClick = {
                                        onAction(TimeCapsuleDetailAction.DeleteTimeCapsule)
                                    }
                                )
                            )
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_delete_black),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 10.dp)
                    )
                    Text(
                        fontSize = 18.sp,
                        text = "삭제"
                    )
                }
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
            ) {
                LoadingProgressBar(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .verticalScroll(
                    scrollState,
                    enabled = enableScroll
                )
        ) {
            TimeCapsulePager(
                uiState = uiState,
                onNavigateToImageDetail = onNavigateToImageDetail,
                onOptionClick = {
                    showOption = it
                },
                onBack = onBack
            )

            val profileResId = when (uiState.timeCapsule.host.profileImage.toInt()) {
                0 -> R.drawable.ic_smile_blue
                1 -> R.drawable.ic_smile_green
                2 -> R.drawable.ic_smile_orange
                else -> R.drawable.ic_smile_violet
            }

            MenuItem(
                title = "작성자 : ${uiState.writer}",
                icon = {
                    Image(
                        painter = painterResource(id = profileResId),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .width(24.dp)
                            .height(24.dp)
                    )
                }
            )

            Divider(
                color = colorResource(id = R.color.light_gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(1.dp)
            )

            MenuItem(
                title = "작성자 : ${uiState.timeCapsule.date}",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calender_black),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .width(24.dp)
                            .height(24.dp)
                    )
                }
            )

            Divider(
                color = colorResource(id = R.color.light_gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(1.dp)
            )
            if (uiState.timeCapsule.sharedFriends.isNotEmpty()) {
                val sharedFriendsText = StringBuilder("공유 : ")

                uiState.timeCapsule.sharedFriends.forEachIndexed { index, s ->
                    if (index < uiState.timeCapsule.sharedFriends.size - 1) {
                        sharedFriendsText.append("$s, ")
                    } else {
                        sharedFriendsText.append(s)
                    }
                }

                MenuItem(
                    title = "작성자 : $sharedFriendsText",
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_people_black),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .width(24.dp)
                                .height(24.dp)
                        )
                    }
                )

                Divider(
                    color = colorResource(id = R.color.light_gray),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(1.dp)
                )
            }

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

            if (uiState.timeCapsule.checkLocation || !uiState.timeCapsule.isReceived) {
                MenuItem(
                    title = "작성자 : ${uiState.timeCapsule.address}",
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location_black),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .width(24.dp)
                                .height(24.dp)
                        )
                    }
                )

                naverMap()
            }
        }
    }
}

@Composable
fun MenuItem(title: String, icon: @Composable (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .padding(vertical = 30.dp, horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            icon?.invoke()
        }

        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
            fontSize = 18.sp
        )
    }
}

@Composable
fun TimeCapsulePager(
    uiState: TimeCapsuleDetailUiState,
    onNavigateToImageDetail: (String, String) -> Unit,
    onBack: () -> Unit,
    onOptionClick: (Boolean) -> Unit
) {
    val timeCapsule = uiState.timeCapsule
    val images: List<String> = timeCapsule.images
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
                    imageModel = { images[page] },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable {
                            val images = URLEncoder.encode(
                                uiState.timeCapsule.images.joinToString(separator = ","),
                                StandardCharsets.UTF_8.toString()
                            )
                            onNavigateToImageDetail("$page", images)
                        }
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
            DefaultBackground(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
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
        Box(
            modifier = Modifier
                .padding(10.dp)
                .clip(CircleShape)
                .width(38.dp)
                .height(38.dp)
                .background(color = Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back_black),
                contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .clickable { onBack() }
            )
        }

        Box(
            modifier = Modifier
                .padding(10.dp)
                .clip(CircleShape)
                .width(38.dp)
                .height(38.dp)
                .background(color = Color.White)
                .align(Alignment.TopEnd)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_option_black),
                contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .clickable {
                        onOptionClick(true)
                    }
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TimeCapsuleDetailScreenDarkPreview() {
    val timeCapsule = TimeCapsule(
        address = "서울시 강남구 압구정동",
        date = "2024-07-29",
        content = "안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 ",
        checkLocation = true
    )

    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TimeCapsuleDetailScreen(
                uiState = TimeCapsuleDetailUiState(isLoading = false, timeCapsule = timeCapsule),
                sideEffect = { flowOf() },
                onAction = {},
                enableScroll = true,
                onNavigateToImageDetail = { _, _ -> },
                showPopup = {},
                onBack = {},
                naverMap = {
                    Box(
                        modifier = Modifier
                            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                            .fillMaxWidth()
                            .aspectRatio(1.3f)
                            .background(color = Color.Blue)
                    )
                },
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun TimeCapsuleDetailScreenPreview() {
    val timeCapsule = TimeCapsule(
        address = "서울시 강남구 압구정동",
        date = "2024-07-29",
        content = "안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 ",
        checkLocation = true
    )

    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TimeCapsuleDetailScreen(
                uiState = TimeCapsuleDetailUiState(isLoading = false, timeCapsule = timeCapsule),
                sideEffect = { flowOf() },
                onAction = {},
                enableScroll = true,
                onNavigateToImageDetail = { _, _ -> },
                showPopup = {},
                onBack = {},
                naverMap = {
                    Box(
                        modifier = Modifier
                            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                            .fillMaxWidth()
                            .aspectRatio(1.3f)
                            .background(color = Color.Blue)
                    )
                },
            )
        }
    }
}