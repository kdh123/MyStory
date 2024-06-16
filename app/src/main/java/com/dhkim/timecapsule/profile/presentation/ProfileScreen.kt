@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.dhkim.timecapsule.profile.presentation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.common.composable.LoadingProgressBar
import com.dhkim.timecapsule.common.composable.WarningDialog
import com.dhkim.timecapsule.common.presentation.profileImage
import com.dhkim.timecapsule.profile.domain.Friend
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableIntStateOf(0) }
    val titles = listOf("친구", "요청")
    val pagerState = rememberPagerState(pageCount = {
        2
    })
    val state = rememberStandardBottomSheetState(
        skipHiddenState = false
    )
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(state)
    val scope = rememberCoroutineScope()
    var showBottomSheet = false
    var selectedDeleteUserId by remember {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is ProfileSideEffect.Message -> {
                    Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
                }

                is ProfileSideEffect.ShowDialog -> {
                    if (!sideEffect.show) {
                        selectedDeleteUserId = ""
                    }
                }

                is ProfileSideEffect.ShowBottomSheet -> {
                    bottomSheetScaffoldState.bottomSheetState.hide()
                }

                is ProfileSideEffect.ShowKeyboard -> {
                    focusManager.clearFocus()
                }
            }
        }
    }

    BackHandler {
        if (showBottomSheet) {
            scope.launch {
                bottomSheetScaffoldState.bottomSheetState.hide()
            }
            showBottomSheet = false
        } else {
            onBack()
        }
    }

    if (selectedDeleteUserId.isNotEmpty()) {
        WarningDialog(
            onDismissRequest = { selectedDeleteUserId = "" },
            onConfirmation = {
                viewModel.deleteFriend(userId = selectedDeleteUserId)
            },
            dialogTitle = "삭제",
            dialogText = "삭제하면 상대방 친구 목록에도 내가 삭제됩니다. ${selectedDeleteUserId}님을 정말 삭제하시겠습니까?",
            iconResId = R.drawable.ic_warning_yellow
        )
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            BottomSheetScreen(
                uiState = uiState,
                onSearch = remember(viewModel) {
                    viewModel::searchUser
                },
                onQuery = remember(viewModel) {
                    viewModel::onQuery
                },
                onAddFriend = remember(viewModel) {
                    viewModel::addFriend
                },
                onDeleteClick = {
                    selectedDeleteUserId = it
                }
            )
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
                            text = "친구",
                            modifier = Modifier
                                .align(Alignment.Center),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    AddFriend(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable {
                                scope.launch {
                                    bottomSheetScaffoldState.bottomSheetState.expand()
                                    showBottomSheet = true
                                }
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
                            FriendScreen(
                                uiState = uiState,
                                onDeleteClick = {
                                    selectedDeleteUserId = it
                                }
                            )
                        }

                        else -> {
                            RequestScreen(uiState = uiState, onClick = remember(viewModel) {
                                viewModel::acceptFriend
                            })
                        }
                    }
                    currentTab = pos
                }
            }
        }
    }
}

@Composable
fun AddFriend(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_friend_add_black),
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
fun BottomSheetScreen(
    uiState: ProfileUiState,
    onQuery: (String) -> Unit,
    onSearch: () -> Unit,
    onAddFriend: () -> Unit,
    onDeleteClick: (userId: String) -> Unit
) {
    val userId = uiState.searchResult.userId
    val friendsIds = uiState.user.friends.map { it.id }
    val requestIds = uiState.user.requests.map { it.id }

    val isMyFriend = if (friendsIds.contains(userId)
        || requestIds.contains(userId)
    ) {
        true
    } else {
        false
    }

    Column(
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(48.dp)
        ) {
            OutlinedCard(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.white),
                ),
                border = BorderStroke(1.dp, color = colorResource(id = R.color.primary)),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                modifier = Modifier
                    .padding(end = 10.dp)
                    .width(0.dp)
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                TextField(
                    singleLine = true,
                    label = {
                        Text(text = "아이디 검색")
                    },
                    value = uiState.searchResult.query,
                    onValueChange = {
                        onQuery(it)
                    },
                    modifier = Modifier
                        .fillMaxSize(),
                    colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                        containerColor = Color.White
                    )
                )
            }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.primary)
                ),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                onClick = {
                    onSearch()
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_search_white),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize()
                )
            }
        }

        uiState.searchResult.run {
            if (uiState.searchResult.userId == null) {
                Text(
                    text = "사용자를 찾을 수 없습니다.",
                    modifier = Modifier
                        .padding(10.dp)
                )
                return@run
            }

            if (userId!!.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Text(
                        text = userId,
                        modifier = Modifier
                            .width(0.dp)
                            .weight(1f)
                            .padding(10.dp)
                            .align(Alignment.CenterVertically)
                    )
                    if (!uiState.searchResult.isMe) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = colorResource(id = R.color.primary)
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            onClick = {
                                if (isMyFriend) {
                                    onDeleteClick(userId)
                                } else {
                                    onAddFriend()
                                }
                            }
                        ) {
                            Image(
                                painter = if (isMyFriend) {
                                    painterResource(id = R.drawable.ic_delete_white)
                                } else {
                                    painterResource(id = R.drawable.ic_person_add_white)
                                },
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    BottomSheetScreen(ProfileUiState(), {}, {}, {}) {

    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    /*ProfileScreen {

    }*/
}

@Composable
fun RequestScreen(uiState: ProfileUiState, onClick: (Friend) -> Unit) {
    val requests = uiState.user.requests
    if (requests.isNotEmpty()) {
        RequestList(
            friends = requests,
            title = "나에게 친구 요청한 사용자를 노출합니다.",
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Text(
            text = "요청 받은 친구가 없습니다.",
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FriendScreen(uiState: ProfileUiState, onDeleteClick: (userId: String) -> Unit) {
    Column {
        Column {
            Text(
                text = "나",
                color = colorResource(id = R.color.gray),
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            )
            FriendItem(
                userId = uiState.user.id,
                isMe = true,
                profileImage = uiState.user.profileImage.profileImage(),
                onDeleteClick = onDeleteClick
            )
        }
        FriendList(
            uiState = uiState,
            isFriend = true,
            title = "서로 승낙한 친구",
            modifier = Modifier.fillMaxWidth(),
            onDeleteClick = onDeleteClick
        )
        FriendList(
            uiState = uiState,
            isFriend = false,
            title = "내가 요청한 친구",
            modifier = Modifier.fillMaxSize(),
            onDeleteClick = onDeleteClick
        )
    }
}

@Composable
fun RequestList(
    friends: List<Friend>,
    title: String,
    onClick: (Friend) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = title,
            color = colorResource(id = R.color.gray),
            modifier = Modifier
                .padding(10.dp)
        )
        LazyColumn(modifier = modifier) {
            itemsIndexed(
                items = friends, key = { _, item ->
                    item.id
                }
            ) { index, item ->
                if (index == friends.size - 1) {
                    RequestItem(friend = item, onClick = onClick)
                } else {
                    RequestItem(friend = item, onClick = onClick)
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
fun FriendList(
    uiState: ProfileUiState,
    title: String,
    isFriend: Boolean,
    onDeleteClick: (userId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val friends = if (isFriend) {
        uiState.user.friends.filter { !it.isPending }
    } else {
        uiState.user.friends.filter { it.isPending }
    }
    Column {
        Text(
            text = title,
            color = colorResource(id = R.color.gray),
            modifier = Modifier
                .padding(horizontal = 10.dp)
        )
        LazyColumn(modifier = modifier) {
            itemsIndexed(
                items = friends, key = { _, item ->
                    item.id
                }
            ) { index, item ->
                if (index == friends.size - 1) {
                    FriendItem(
                        userId = item.id,
                        profileImage = item.profileImage.profileImage(),
                        onDeleteClick = onDeleteClick
                    )
                } else {
                    FriendItem(
                        userId = item.id,
                        profileImage = item.profileImage.profileImage(),
                        onDeleteClick = onDeleteClick
                    )
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
fun RequestItem(friend: Friend, onClick: (Friend) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Image(
            painter = painterResource(id = friend.profileImage.profileImage()),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
            text = friend.id,
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
                onClick(friend)
            }
        ) {
            Image(
                painter =
                painterResource(id = R.drawable.ic_person_add_white),
                contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
            )
        }
    }
}

@Composable
fun FriendItem(userId: String, profileImage: Int, isMe: Boolean = false, onDeleteClick: (userId: String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Image(
            painter = painterResource(id = profileImage),
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
            onClick = {
                onDeleteClick(userId)
            }
        ) {
            if (!isMe) {
                Image(
                    painter =
                    painterResource(id = R.drawable.ic_delete_white),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendItemPreview() {
    FriendItem(userId = "", 0) {

    }
}