package com.dhkim.timecapsule.timecapsule.presentation.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.common.composable.drawAnimatedBorder
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsule
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

sealed interface UiState {
    data object Loading: UiState
    data object Loaded: UiState
    data object Error: UiState
}

@Composable
fun TimeCapsuleOpenScreen(
    timeCapsuleId: String,
    isReceived: Boolean,
    uiState: TimeCapsuleDetailUiState,
    init: (String, Boolean) -> Unit
) {
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
        countDownFlow.collect{
            countDownNumber--
        }
    }

    LaunchedEffect(uiState) {
        init(timeCapsuleId, isReceived)
    }

    var state: UiState by remember {
        mutableStateOf(UiState.Loading)
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
                LoadedScreen(uiState)
            }
            UiState.Error -> {

            }
        }
    }

}

@Composable
fun LoadedScreen(uiState: TimeCapsuleDetailUiState) {

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FilmLayout(uiState = uiState, onClick = { _, _ ->

        })

        Text(
            text = uiState.timeCapsule.content,
            modifier = Modifier
                .padding(10.dp)
        )
    }
}

@Composable
fun LoadingScreen(countDownNumber: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .align(Alignment.Center)
                .drawAnimatedBorder(
                    strokeWidth = 5.dp,
                    shape = CircleShape,
                    durationMillis = 1000
                )
        ) {
            Text(
                text = "$countDownNumber",
                fontSize = 18.sp,
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(10.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center
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

@Preview(showBackground = true)
@Composable
private fun TimeCapsuleOpenScreenPreview() {

}