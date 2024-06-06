package com.dhkim.timecapsule.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.dhkim.timecapsule.common.composable.LoadingProgressBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableIntStateOf(0) }
    val titles = listOf("친구", "요청")
    val pagerState = rememberPagerState(pageCount = {
        2
    })
    val state = rememberStandardBottomSheetState(
        skipHiddenState = true
    )
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(state)
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {

        },
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back_black),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .alpha(0f)
                    )
                    Box(
                        modifier = Modifier
                            .width(0.dp)
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = "프로필",
                            modifier = Modifier
                                .align(Alignment.Center),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_friend_add_black),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable {

                            }
                    )
                }
                Divider(
                    thickness = 1.dp,
                    color = colorResource(id = R.color.light_gray)
                )
            }
        }
    ) { innerPadding ->
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

            if (uiState.isLoading) {
                LoadingProgressBar(
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                HorizontalPager(state = pagerState) { pos ->
                    when (pos) {
                        0 -> {
                            FriendScreen(uiState = uiState)
                        }

                        else -> {
                            RequestScreen(uiState = uiState)
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    /*ProfileScreen {

    }*/
}

@Composable
fun RequestScreen(uiState: ProfileUiState) {
    val requests = uiState.requests

    if (requests.isNotEmpty()) {
        FriendList(
            friends = requests,
            title = "나에게 친구 요청한 사용자를 표시합니다.",
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Text(text = "요청 받은 친구가 없습니다.")
    }
}

@Composable
fun FriendScreen(uiState: ProfileUiState) {
    val friends = uiState.friends.filter { !it.isPending }.map { it.id }
    val requests = uiState.friends.filter { it.isPending }.map { it.id }

    Column {
        FriendList(friends = friends, title = "서로 승낙한 친구", modifier = Modifier.fillMaxWidth())
        FriendList(friends = requests, title = "요청한 친구", modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun FriendList(friends: List<UserId>, title: String, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(10.dp)
        )
        LazyColumn(modifier = modifier) {
            itemsIndexed(
                items = friends, key = { _, item ->
                    item
                }
            ) { index, item ->
                if (index == friends.size - 1) {
                    FriendItem(userId = item)
                } else {
                    FriendItem(userId = item)
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
fun FriendItem(userId: String) {
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
        Text(
            text = userId,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(start = 5.dp)
        )

        Card(
            border = BorderStroke(
                width = 1.dp,
                color = colorResource(id = R.color.primary)
            ),
            colors = CardDefaults.cardColors(colorResource(id = R.color.primary)),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
            onClick = {

            }
        ) {
            Text(
                text = "삭제",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendItemPreview() {

    FriendItem(userId = "kdh123")
}