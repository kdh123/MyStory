package com.dhkim.home.presentation.more

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dhkim.home.R
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
                Image(
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
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            content = {
                items(items = uiState.timeCapsules, key = {
                    it.id
                }) {
                    if (it.images.isNotEmpty()) {
                        GlideImage(
                            imageModel = it.images[0],
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onNavigateToDetail(it.id, it.isReceived)
                                }
                        )
                    } else {
                        Box {
                            DefaultBackground(
                                modifier = Modifier
                                    .width(300.dp)
                                    .height(300.dp)
                                    .clickable {
                                        onNavigateToDetail(it.id, it.isReceived)
                                    }
                            ) {
                                Text(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    text = "사진이 존재하지 않습니다.",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
        )
    }
}