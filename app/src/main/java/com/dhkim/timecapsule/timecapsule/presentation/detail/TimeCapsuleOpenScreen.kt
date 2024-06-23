@file:OptIn(ExperimentalAnimationApi::class, ExperimentalAnimationApi::class, ExperimentalAnimationApi::class)

package com.dhkim.timecapsule.timecapsule.presentation.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.common.ui.drawAnimatedBorder
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsule
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

sealed interface UiState {
    data object Loading : UiState
    data object Loaded : UiState
    data object Error : UiState
}

@Composable
fun TimeCapsuleOpenScreen(
    timeCapsuleId: String,
    isReceived: Boolean,
    uiState: TimeCapsuleDetailUiState,
    init: (String, Boolean) -> Unit,
    onNavigateToDetail: (String, Boolean) -> Unit
) {

    var state: UiState by remember {
        mutableStateOf(UiState.Loading)
    }
    var countDownNumber by remember {
        mutableIntStateOf(3)
    }
    val countDownFlow = flow {
        repeat(3) {
            delay(1000L)
            emit(countDownNumber)
        }
    }

    LaunchedEffect(true) {
        countDownFlow.collect {
            countDownNumber--
        }
    }

    LaunchedEffect(uiState) {
        init(timeCapsuleId, isReceived)
    }

    LaunchedEffect(countDownNumber) {
        if (countDownNumber <= 0) {
            state = UiState.Loaded
        }
    }

    AnimatedContent(
        state,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(3000)
            ) togetherWith fadeOut(animationSpec = tween(3000))
        },
        label = "Animated Content"
    ) { targetState ->
        when (targetState) {
            UiState.Loading -> {
                LoadingScreen(countDownNumber)
            }

            UiState.Loaded -> {
                LoadedScreen(
                    uiState = uiState,
                    onNavigateToDetail = { id, isReceived ->
                        onNavigateToDetail(id, isReceived)
                    }
                )
            }

            UiState.Error -> {

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimeCapsuleOpenScreenPreview() {
    TimeCapsuleOpenScreen(
        timeCapsuleId = "",
        isReceived = false,
        uiState = TimeCapsuleDetailUiState(),
        onNavigateToDetail = { _, _ ->

        },
        init = { _, _ -> }
    )
}

@Composable
fun LoadedScreen(uiState: TimeCapsuleDetailUiState, onNavigateToDetail: (String, Boolean) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val medias = uiState.timeCapsule.medias
    var imageUrl by remember {
        mutableStateOf("")
    }

    val imageFlow = remember {
        flow {
            repeat(medias.size) {
                val index = it / medias.size
                delay(1000L)
                emit(medias[index])
            }
        }
    }

    LaunchedEffect(true) {
        imageFlow.collect {
            imageUrl = it
        }
    }

    if (uiState.timeCapsule.medias.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TimeCapsuleSlideImages(uiState)
            Text(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                text = "건너뛰기",
                modifier = Modifier
                    .padding(20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color = colorResource(id = R.color.transparent_gray))
                    .align(Alignment.BottomEnd)
                    .padding(15.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        onNavigateToDetail(uiState.timeCapsule.id, uiState.timeCapsule.isReceived)
                    }
            )
        }
    } else {
        onNavigateToDetail(uiState.timeCapsule.id, uiState.timeCapsule.isReceived)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TimeCapsuleContent(content: String) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        onClick = { expanded = !expanded },
        modifier = Modifier
            .padding(10.dp)
    ) {
        AnimatedContent(
            targetState = expanded,
            transitionSpec = {
                fadeIn(animationSpec = tween(150, 150)) with
                        fadeOut(animationSpec = tween(150)) using
                        SizeTransform { initialSize, targetSize ->
                            if (targetState) {
                                keyframes {
                                    // Expand horizontally first.
                                    IntSize(targetSize.width, initialSize.height) at 150
                                    durationMillis = 300
                                }
                            } else {
                                keyframes {
                                    // Shrink vertically first.
                                    IntSize(initialSize.width, targetSize.height) at 150
                                    durationMillis = 300
                                }
                            }
                        }
            },
            label = ""
        ) { targetExpanded ->
            if (targetExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 300.dp)
                        .border(
                            width = 2.dp,
                            color = colorResource(id = R.color.gray),
                            shape = RoundedCornerShape(20.dp)
                        )

                ) {
                    Text(
                        text = content,
                        modifier = Modifier
                            .padding(10.dp)
                    )
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(colorResource(id = R.color.primary)),
                    elevation = CardDefaults.cardElevation(10.dp),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .padding(end = 10.dp, bottom = 10.dp)
                        .width(80.dp)
                        .height(80.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_letter_white),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(25.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Test() {
    Card(
        colors = CardDefaults.cardColors(colorResource(id = R.color.primary)),
        elevation = CardDefaults.cardElevation(100.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(end = 10.dp, bottom = 10.dp)
            .width(80.dp)
            .height(80.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_letter_white),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp)
        )
    }
}

@Composable
private fun TimeCapsuleSlideImages(uiState: TimeCapsuleDetailUiState) {
    val interactionSource = remember { MutableInteractionSource() }
    val imageUrls = uiState.timeCapsule.medias
    var index by remember {
        mutableIntStateOf(0)
    }
    val imageUrl = imageUrls[index]
    AnimatedContent(
        imageUrl,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(1500)
            ) togetherWith fadeOut(animationSpec = tween(1500))
        },
        label = "",
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                index = (index + 1) % imageUrls.size
            }
    ) {
        if (it.isNotEmpty()) {
            TimeCapsuleImage(imageUrl = it)
            LaunchedEffect(index) {
                delay(3000L)
                index = (index + 1) % imageUrls.size
            }
        }
    }
}

@Composable
private fun TimeCapsuleImage(imageUrl: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(10000), RepeatMode.Reverse),
        label = "scale"
    )

    GlideImage(
        imageModel = imageUrl,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            ),
        previewPlaceholder = R.drawable.ic_launcher_background
    )
}

@Preview
@Composable
private fun TimeCapsuleImagePreview() {
    TimeCapsuleImage(imageUrl = "")
}

@Preview(showBackground = true)
@Composable
private fun LoadedScreenPreview() {
    val timeCapsule = TimeCapsule(
        id = "id1",
        content = "안녕하세요",
        medias = listOf("")
    )
    val uiState = TimeCapsuleDetailUiState(
        timeCapsule = timeCapsule
    )

    LoadedScreen(
        uiState = uiState,
        onNavigateToDetail = { _, _ ->

        }
    )
}

@Composable
fun LoadingScreen(countDownNumber: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .align(Alignment.CenterHorizontally)
                    .drawAnimatedBorder(
                        strokeWidth = 5.dp,
                        shape = CircleShape,
                        durationMillis = 1000
                    )
            ) {
                Text(
                    text = "$countDownNumber",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .padding(10.dp)
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "과거로 돌아가는 중",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingScreenPreview() {
    LoadingScreen(3)
}

@Composable
fun FilmLayout(uiState: TimeCapsuleDetailUiState, onClick: (String, Boolean) -> Unit) {
    val state = rememberScrollState()
    val imageUrls = uiState.timeCapsule.medias

    Row(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
            .horizontalScroll(state)
            .background(color = Color.Black)

    ) {
        Box(
            modifier = Modifier
                .background(color = Color.White)
                .width(10.dp)
                .height(425.dp)
        )

        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .background(color = Color.Black)
                    .padding(vertical = 10.dp)
            ) {
                repeat(imageUrls.size * 12 - 1) {
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .background(color = Color.White)
                            .width(12.dp)
                            .height(12.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
            ) {
                repeat(imageUrls.size) {
                    GlideImage(
                        contentScale = ContentScale.FillBounds,
                        imageModel = imageUrls[it],
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .width(240.dp)
                            .height(360.dp)
                            .clickable {
                            },
                        previewPlaceholder = R.drawable.ic_launcher_background
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .background(color = Color.Black)
                    .padding(vertical = 10.dp)
            ) {
                repeat(imageUrls.size * 12 - 1) {
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .background(color = Color.White)
                            .width(12.dp)
                            .height(12.dp)
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .background(color = Color.White)
                .width(10.dp)
                .height(425.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FilmLayoutPreview() {
    val timeCapsuleDetailUiState = TimeCapsuleDetailUiState().copy(
        timeCapsule = TimeCapsule(medias = listOf("", "", "", ""))
    )

    FilmLayout(timeCapsuleDetailUiState, onClick = { _, _ ->

    })

}

