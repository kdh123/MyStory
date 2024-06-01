package com.dhkim.timecapsule.timecapsule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.timecapsule.domain.MyTimeCapsule
import com.dhkim.timecapsule.timecapsule.presentation.TimeCapsuleUiState
import com.dhkim.timecapsule.timecapsule.presentation.TimeCapsuleViewModel
import kotlinx.coroutines.launch

@Composable
fun TimeCapsuleScreen(viewModel: TimeCapsuleViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableIntStateOf(0) }
    val titles = listOf("My", "수신", "발신")
    val pagerState = rememberPagerState(pageCount = {
        3
    })
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            currentTab = page
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = currentTab,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[currentTab]),
                    color = colorResource(id = R.color.primary)
                )
            }
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    icon = {
                        when (index) {
                            0 -> R.drawable.ic_my_black
                            1 -> R.drawable.ic_box_black
                            else -> R.drawable.ic_send_black
                        }.let {
                            Icon(painter = painterResource(id = it), contentDescription = null)
                        }
                    },
                    selectedContentColor = colorResource(id = R.color.primary),
                    unselectedContentColor = colorResource(id = R.color.black),
                    selected = currentTab == index,
                    onClick = {
                        currentTab = index
                        scope.launch {
                            pagerState.scrollToPage(currentTab)
                        }
                    },
                    text = {
                        if (currentTab == index) {
                            Text(text = title, fontWeight = FontWeight.Bold)
                        } else {
                            Text(text = title)
                        }
                    },
                )
            }
        }
        HorizontalPager(state = pagerState) { pos ->
            when (pos) {
                0 -> MyTimeCapsuleScreen(uiState = uiState)
                1 -> MyTimeCapsuleScreen(uiState = uiState)
                else -> MyTimeCapsuleScreen(uiState = uiState)
            }
        }
    }
}

@Composable
fun MyTimeCapsuleScreen(uiState: TimeCapsuleUiState) {
    val timeCapsules = uiState.myTimeCapsules

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(
            items = timeCapsules, key = { _, item ->
                item.id
            }
        ) { index, item ->
            if (index == timeCapsules.size - 1) {
                MyTimeCapsuleItem(timeCapsule = item)
            } else {
                MyTimeCapsuleItem(timeCapsule = item)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTimeCapsuleItem(timeCapsule: MyTimeCapsule) {
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
            Text(
                text = timeCapsule.openDate,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(text = timeCapsule.address)
        }

        Card(
            border = BorderStroke(
                width = 1.dp,
                color = colorResource(id = R.color.primary)
            ),
            colors = CardDefaults.cardColors(Color.White),
            elevation = CardDefaults.cardElevation(10.dp),
            onClick = {

            }
        ) {
            Text(
                text = "열기",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTimeCapsulePreview() {
    val timeCapsule = MyTimeCapsule(
        id = "id1",
        date = "2024-06-01",
        openDate = "2024-10-15",
        lat = "13.14",
        lng = "13.14",
        address = "서울시 강남구 강남동",
        content = "내 타임캡슐",
        medias = listOf(),
        checkLocation = true,
        isOpened = false
    )

    MyTimeCapsuleItem(timeCapsule = timeCapsule)
}

@Preview(showBackground = true)
@Composable
private fun TimeCapsuleScreenPreview() {
    TimeCapsuleScreen()
}