package com.dhkim.home.presentation.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dhkim.home.R
import com.dhkim.story.domain.model.TimeCapsule
import com.dhkim.ui.drawAnimatedBorder
import com.dhkim.ui.onStartCollect
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
    uiState: TimeCapsuleDetailUiState,
    onNavigateToDetail: (String, Boolean) -> Unit
) {
    var state: UiState by remember {
        mutableStateOf(UiState.Loading)
    }
    var countDownNumber by rememberSaveable {
        mutableIntStateOf(3)
    }
    val countDownFlow = flow {
        repeat(3) {
            delay(1_000)
            emit(countDownNumber)
        }
    }

    LocalLifecycleOwner.current.onStartCollect(countDownFlow) {
        countDownNumber--
    }

    LaunchedEffect(countDownNumber) {
        if (countDownNumber <= 0) {
            state = UiState.Loaded
        }
    }

    AnimatedContent(
        targetState = state,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(3_000)
            ) togetherWith fadeOut(animationSpec = tween(3_000))
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

            UiState.Error -> {}
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimeCapsuleOpenScreenPreview() {
    TimeCapsuleOpenScreen(
        uiState = TimeCapsuleDetailUiState(),
        onNavigateToDetail = { _, _ -> }
    )
}

@Composable
fun LoadedScreen(uiState: TimeCapsuleDetailUiState, onNavigateToDetail: (String, Boolean) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val images = uiState.timeCapsule.images
    var imageUrl by remember {
        mutableStateOf("")
    }

    val imageFlow = remember {
        flow {
            repeat(images.size) {
                val index = it / images.size
                delay(1000L)
                emit(images[index])
            }
        }
    }

    LaunchedEffect(true) {
        imageFlow.collect {
            imageUrl = it
        }
    }

    if (uiState.timeCapsule.images.isNotEmpty()) {
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

@Composable
private fun TimeCapsuleSlideImages(uiState: TimeCapsuleDetailUiState) {
    val interactionSource = remember { MutableInteractionSource() }
    val imageUrls = uiState.timeCapsule.images
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
        imageModel = { imageUrl },
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
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
        images = listOf("")
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