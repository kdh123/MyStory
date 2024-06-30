@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.dhkim.timecapsule.friend.presentation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dhkim.timecapsule.R
import com.dhkim.ui.LoadingProgressBar
import com.dhkim.ui.WarningDialog
import com.dhkim.common.profileImage
import com.dhkim.timecapsule.user.domain.Friend
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    sideEffect: FriendSideEffect,
    onQuery: (String) -> Unit,
    onSearchUser: () -> Unit,
    onAddFriend: () -> Unit,
    onAcceptFriend: (Friend) -> Unit,
    onDeleteFriend: (String) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
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
    var selectedUserId by remember {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current
    var showMenuDialog by remember {
        mutableStateOf(false)
    }
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(sideEffect) {
        when (sideEffect) {
            is FriendSideEffect.None -> {}

            is FriendSideEffect.Message -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is FriendSideEffect.ShowDialog -> {
                if (!sideEffect.show) {
                    selectedUserId = ""
                }
            }

            is FriendSideEffect.ShowBottomSheet -> {
                bottomSheetScaffoldState.bottomSheetState.hide()
            }

            is FriendSideEffect.ShowKeyboard -> {
                focusManager.clearFocus()
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

    if (showMenuDialog) {
        Dialog(
            onDismissRequest = {
                selectedUserId = ""
                showMenuDialog = false
            }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text(
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = selectedUserId,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "삭제",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showMenuDialog = false
                                showDeleteDialog = true
                            }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        WarningDialog(
            onDismissRequest = {
                showDeleteDialog = false
                selectedUserId = ""
            },
            onConfirmation = {
                onDeleteFriend(selectedUserId)
            },
            dialogTitle = "삭제",
            dialogText = "삭제하면 상대방 친구 목록에도 내가 삭제됩니다. ${selectedUserId}님을 정말 삭제하시겠습니까?",
            iconResId = R.drawable.ic_warning_yellow
        )
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            BottomSheetScreen(
                uiState = uiState,
                onSearch = onSearchUser,
                onQuery = onQuery,
                onAddFriend = onAddFriend
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
                                onLongClick = {
                                    selectedUserId = it
                                    showMenuDialog = true
                                }
                            )
                        }

                        else -> {
                            RequestScreen(uiState = uiState, onClick = onAcceptFriend)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetScreen(
    uiState: ProfileUiState,
    onQuery: (String) -> Unit,
    onSearch: () -> Unit,
    onAddFriend: () -> Unit
) {
    val userId = uiState.searchResult.userId
    val friendsIds = uiState.user.friends.map { it.id }
    val requestIds = uiState.user.requests.map { it.id }

    val isInMyFriendsOrRequests = friendsIds.contains(userId) || requestIds.contains(userId)
    val isMyFriend = uiState.user.friends.firstOrNull()?.isPending == false

    val friendMetaInfoText = when {
        uiState.searchResult.isMe -> {
            "나입니다."
        }

        isMyFriend -> {
            "친구로 등록된 사용자입니다."
        }

        uiState.user.friends.firstOrNull()?.isPending == true && isInMyFriendsOrRequests -> {
            "내가 친구 요청을 한 사용자입니다."
        }

        requestIds.contains(userId) -> {
            "나에게 친구 요청을 한 사용자입니다."
        }

        else -> {
            ""
        }
    }

    Column(
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 10.dp)
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
                    textStyle = TextStyle(fontSize = 12.sp),
                    singleLine = true,
                    value = uiState.searchResult.query,
                    label = {
                        Text(text = "아이디 검색")
                    },
                    onValueChange = {
                        onQuery(it)
                    },
                    modifier = Modifier
                        .fillMaxSize(),
                    colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
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
                    Column(
                        modifier = Modifier
                            .width(0.dp)
                            .weight(1f)
                            .padding(10.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(text = "$userId")
                        if (friendMetaInfoText.isNotEmpty()) {
                            Text(
                                text = friendMetaInfoText,
                                fontSize = 12.sp,
                                color = colorResource(id = R.color.gray)
                            )
                        }
                    }

                    if (!uiState.searchResult.isMe && !isInMyFriendsOrRequests) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = colorResource(id = R.color.primary)
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            onClick = {
                                onAddFriend()
                            }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_person_add_white),
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
    BottomSheetScreen(ProfileUiState(), {}, {}, {})
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
fun FriendScreen(uiState: ProfileUiState, onLongClick: (userId: String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
                onLongClick = onLongClick
            )
        }
        FriendList(
            uiState = uiState,
            isFriend = true,
            title = "서로 승낙한 친구",
            modifier = Modifier.fillMaxWidth(),
            onDeleteClick = onLongClick
        )
        FriendList(
            uiState = uiState,
            isFriend = false,
            title = "내가 요청한 친구",
            modifier = Modifier.fillMaxSize(),
            onDeleteClick = onLongClick
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
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
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

    if (friends.isNotEmpty()) {
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
                            onLongClick = onDeleteClick
                        )
                    } else {
                        FriendItem(
                            userId = item.id,
                            profileImage = item.profileImage.profileImage(),
                            onLongClick = onDeleteClick
                        )
                    }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FriendItem(userId: String, profileImage: Int, isMe: Boolean = false, onLongClick: (userId: String) -> Unit) {
    Box(
        modifier = Modifier
            .combinedClickable(
                onClick = { },
                onLongClick = {
                    if (!isMe) {
                        onLongClick(userId)
                    }
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Image(
                painter = painterResource(id = profileImage),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = userId,
                modifier = Modifier
                    .width(0.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 5.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendItemPreview() {
    FriendItem(userId = "", 0) {

    }
}