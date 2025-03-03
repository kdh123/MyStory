package com.dhkim.home.presentation.more

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dhkim.designsystem.MyStoryTheme
import com.dhkim.home.R
import com.dhkim.story.domain.model.TimeCapsule
import com.dhkim.ui.DefaultBackground
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun MoreTimeCapsuleScreen(
    uiState: MoreTimeCapsuleUiState,
    onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_black),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            onBack()
                        }
                )
            }
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
        ) {
            items(items = uiState.timeCapsules, key = {
                it.id
            }) {
                if (it.images.isNotEmpty()) {
                    GlideImage(
                        imageModel = { it.images[0] },
                        previewPlaceholder = painterResource(id = R.drawable.ic_launcher_background),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clickable {
                                onNavigateToDetail(it.id, it.isReceived)
                            }
                    )
                } else {
                    Box {
                        DefaultBackground(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clickable {
                                    onNavigateToDetail(it.id, it.isReceived)
                                }
                        ) {
                            Text(
                                text = "사진이 존재하지 않습니다.",
                                style = MyStoryTheme.typography.bodyMediumWhiteBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MoreTimeCapsuleScreenDarkPreview() {
    val timeCapsules = mutableListOf<TimeCapsule>().apply {
        repeat(10) {
            add(
                TimeCapsule(
                    id = "id$it",
                    images = listOf("https://picsum.photos/200/300")
                )
            )
        }
    }

    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MoreTimeCapsuleScreen(
                uiState = MoreTimeCapsuleUiState(timeCapsules = timeCapsules),
                onNavigateToDetail = { _, _ -> },
                onBack = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoreTimeCapsuleScreenPreview() {
    val timeCapsules = mutableListOf<TimeCapsule>().apply {
        repeat(10) {
            add(
                TimeCapsule(
                    id = "id$it",
                    images = listOf("https://picsum.photos/200/300")
                )
            )
        }
    }

    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MoreTimeCapsuleScreen(
                uiState = MoreTimeCapsuleUiState(timeCapsules = timeCapsules),
                onNavigateToDetail = { _, _ -> },
                onBack = {}
            )
        }
    }
}