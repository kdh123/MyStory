@file:OptIn(ExperimentalFoundationApi::class)

package com.dhkim.friend

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dhkim.designsystem.MyStoryTheme
import com.dhkim.domain.model.Friend
import com.dhkim.domain.model.User
import com.dhkim.ui.LoadingProgressBar
import com.dhkim.ui.Popup
import com.dhkim.ui.onStartCollect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreen(
    uiState: FriendUiState,
    sideEffect: () -> Flow<FriendSideEffect>,
    onAction: (FriendAction) -> Unit,
    onNavigateToAddTimeCapsule: (friendId: String) -> Unit,
    onNavigateToChangeInfo: (Friend) -> Unit,
    onBack: () -> Unit,
    showPopup: (Popup?) -> Unit,
    modifier: Modifier = Modifier
) {
    val lifecycle = LocalLifecycleOwner.current
    val context = LocalContext.current
    var currentTab by rememberSaveable { mutableIntStateOf(0) }
    val titles = listOf(stringResource(R.string.friend_tab_friends), stringResource(R.string.friend_tab_requests))
    val strDelete = stringResource(R.string.friend_delete)
    val strNoticeTitle = stringResource(R.string.friend_notice)
    val strNoticeDesc = stringResource(R.string.friend_create_code_privacy)
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    var selectedFriend: Friend? by remember { mutableStateOf(null) }
    val focusManager = LocalFocusManager.current
    var showFriendMenuDialog by rememberSaveable { mutableStateOf(false) }
    var showPendingFriendMenuDialog by rememberSaveable { mutableStateOf(false) }
    var showInfoBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showFriendsBottomSheet by rememberSaveable { mutableStateOf(false) }
    val infoBottomSheetState = rememberModalBottomSheetState()

    lifecycle.onStartCollect(sideEffect()) {
        when (it) {
            is FriendSideEffect.None -> {}

            is FriendSideEffect.Message -> {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }

            is FriendSideEffect.ShowDialog -> {
                if (!it.show) {
                    showPopup(null)
                    showFriendMenuDialog = false
                    showPendingFriendMenuDialog = false
                    selectedFriend = null
                }
            }

            is FriendSideEffect.ShowBottomSheet -> {
                showFriendsBottomSheet = false
            }

            is FriendSideEffect.ShowKeyboard -> {
                focusManager.clearFocus()
            }
        }
    }

    BackHandler {
        if (showFriendsBottomSheet) {
            showFriendsBottomSheet = false
        } else {
            onBack()
        }
    }

    if (showFriendMenuDialog && selectedFriend != null) {
        val strDeleteDesc = stringResource(R.string.friend_delete_desc, selectedFriend!!.nickname)
        Dialog(
            onDismissRequest = {
                selectedFriend = Friend()
                showFriendMenuDialog = false
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
                        text = selectedFriend!!.nickname,
                        style = MyStoryTheme.typography.bodyLargeBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.friend_delete),
                        style = MyStoryTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showFriendMenuDialog = false
                                showPopup(
                                    Popup.Warning(
                                        title = strDelete,
                                        desc = strDeleteDesc,
                                        onPositiveClick = {
                                            onAction(FriendAction.DeleteFriend(selectedFriend!!.id))
                                        },
                                        onNegativeClick = {
                                            selectedFriend = null
                                        },
                                        onDismissRequest = {
                                            selectedFriend = null
                                        }
                                    )
                                )
                            }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.friend_info_change),
                        style = MyStoryTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showFriendMenuDialog = false
                                onNavigateToChangeInfo(selectedFriend!!)
                            }
                    )
                }
            }
        }
    }

    if (showPendingFriendMenuDialog && selectedFriend != null) {
        val strDeleteDesc = stringResource(R.string.friend_delete_desc, selectedFriend!!.nickname)
        Dialog(
            onDismissRequest = {
                selectedFriend = Friend()
                showPendingFriendMenuDialog = false
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
                        text = stringResource(R.string.friend_menu),
                        style = MyStoryTheme.typography.bodyLargeBold,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.friend_delete),
                        style = MyStoryTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showPendingFriendMenuDialog = false
                                showPopup(
                                    Popup.Warning(
                                        title = strDelete,
                                        desc = strDeleteDesc,
                                        onPositiveClick = {
                                            onAction(FriendAction.DeleteFriend(selectedFriend!!.id))
                                        },
                                        onNegativeClick = {
                                            selectedFriend = null
                                        },
                                        onDismissRequest = {
                                            selectedFriend = null
                                        }
                                    )
                                )
                            }
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier
    ) {
        if (uiState.myInfo.id.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
            ) {
                if (uiState.isLoading) {
                    LoadingProgressBar(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                } else {
                    val interactionSource = remember { MutableInteractionSource() }

                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                    ) {
                        ShareTimeCapsuleAnim()

                        Text(
                            text = stringResource(R.string.friend_create_code_promo),
                            style = MyStoryTheme.typography.headlineSmallBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp, bottom = 20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                color = if (uiState.isCreatingCode) {
                                    colorResource(id = R.color.light_gray)
                                } else {
                                    colorResource(id = R.color.primary)
                                }
                            )
                            .align(Alignment.BottomCenter)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                if (!uiState.isCreatingCode) {
                                    showPopup(
                                        Popup.Warning(
                                            title = strNoticeTitle,
                                            desc = strNoticeDesc,
                                            onPositiveClick = {
                                                onAction(FriendAction.CreateFriendCode)
                                            }
                                        )
                                    )
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .height(48.dp)
                                .align(Alignment.Center)
                        ) {
                            if (uiState.isCreatingCode) {
                                CircularProgressIndicator(
                                    color = colorResource(id = R.color.gray),
                                    trackColor = Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(1f)
                                        .align(Alignment.CenterVertically)
                                )
                            }

                            Text(
                                text = if (uiState.isCreatingCode) {
                                    stringResource(R.string.friend_creating_code)
                                } else {
                                    stringResource(R.string.friend_create_code)
                                },
                                style = if (uiState.isCreatingCode) {
                                    MyStoryTheme.typography.bodyLargeGrayBold
                                } else {
                                    MyStoryTheme.typography.bodyLargeWhiteBold
                                },
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) {
            TabRow(
                selectedTabIndex = currentTab,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[currentTab]),
                        color = MaterialTheme.colorScheme.primary
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
                            Text(
                                text = title,
                                style = MyStoryTheme.typography.bodyMediumBold,
                                color = if (currentTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
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
                            FriendListScreen(
                                uiState = uiState,
                                showInfoBottomSheet = {
                                    selectedFriend = it
                                    showInfoBottomSheet = true
                                },
                                showAddFriendBottomSheet = {
                                    showFriendsBottomSheet = true
                                },
                                onFriendLongClick = {
                                    selectedFriend = it
                                    showFriendMenuDialog = true
                                },
                                onPendingFriendLongClick = {
                                    selectedFriend = it
                                    showPendingFriendMenuDialog = true
                                }
                            )
                        }

                        else -> {
                            RequestScreen(
                                uiState = uiState,
                                onClick = {
                                    onAction(FriendAction.AcceptFriend(friend = it))
                                }
                            )
                        }
                    }
                    currentTab = pos
                }
            }

            if (showInfoBottomSheet && selectedFriend != null) {
                ModalBottomSheet(
                    sheetState = infoBottomSheetState,
                    onDismissRequest = {
                        showInfoBottomSheet = false
                    },
                    modifier = Modifier
                        .padding(bottom = it.calculateBottomPadding())
                ) {
                    Column(
                        modifier = Modifier
                            .padding(bottom = 48.dp)
                    ) {
                        Text(
                            text = selectedFriend!!.nickname,
                            style = MyStoryTheme.typography.bodyLargeBold,
                            modifier = Modifier
                                .padding(10.dp)
                        )
                        MenuItem(
                            resId = R.drawable.ic_time_primary,
                            title = stringResource(R.string.friend_share_time_capsule),
                            onClick = {
                                onNavigateToAddTimeCapsule(selectedFriend!!.id)
                                showInfoBottomSheet = false
                            })
                        MenuItem(
                            resId = R.drawable.ic_smile_blue,
                            title = stringResource(R.string.friend_info_change),
                            onClick = {
                                onNavigateToChangeInfo(selectedFriend!!)
                                showInfoBottomSheet = false
                            }
                        )
                    }
                }
            }

            if (showFriendsBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showFriendsBottomSheet = false
                    },
                    modifier = Modifier
                        .padding(bottom = it.calculateBottomPadding())
                ) {
                    BottomSheetScreen(
                        uiState = uiState,
                        onAction = onAction
                    )
                }
            }
        }
    }
}

@Composable
private fun ShareTimeCapsuleAnim() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val preloaderLottieComposition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(
                R.raw.share_anim
            )
        )

        val preloaderProgress by animateLottieCompositionAsState(
            preloaderLottieComposition,
            iterations = LottieConstants.IterateForever,
            isPlaying = true
        )


        LottieAnimation(
            composition = preloaderLottieComposition,
            progress = preloaderProgress,
            modifier = Modifier
                .width(300.dp)
                .aspectRatio(1f)
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun MenuItem(
    resId: Int,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = modifier
                .padding(10.dp)
        ) {
            Image(
                painter = painterResource(
                    id = resId
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 10.dp)
            )
            Text(
                text = title,
                style = MyStoryTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetScreen(
    uiState: FriendUiState,
    onAction: (FriendAction) -> Unit
) {
    val userId = uiState.searchResult.userId
    val friendsIds = uiState.myInfo.friends.map { it.id }
    val requestIds = uiState.myInfo.requests.map { it.id }

    val isInMyFriendsOrRequests = friendsIds.contains(userId) || requestIds.contains(userId)
    val isMyFriend = uiState.myInfo.friends.map { it.id }.contains(userId)

    val friendMetaInfoText = when {
        uiState.searchResult.isMe -> {
            stringResource(R.string.friend_meta_me)
        }

        isMyFriend -> {
            stringResource(R.string.friend_meta_already_friend)
        }

        uiState.myInfo.friends.firstOrNull()?.isPending == true && isInMyFriendsOrRequests -> {
            stringResource(R.string.friend_meta_request_sent)
        }

        requestIds.contains(userId) -> {
            stringResource(R.string.friend_meta_request_received)
        }

        else -> {
            ""
        }
    }

    Column(
        modifier = Modifier.fillMaxHeight(0.8f)
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
                BasicTextField(
                    value = uiState.searchResult.query,
                    onValueChange = {
                        onAction(FriendAction.Query(it))
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (uiState.searchResult.query.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.friend_code_hint),
                                    style = MyStoryTheme.typography.bodyMedium
                                )
                            }
                            innerTextField()
                        }
                    }
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
                    onAction(FriendAction.SearchUser)
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
                    text = stringResource(R.string.friend_user_not_found),
                    style = MyStoryTheme.typography.bodyMedium,
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
                        Text(
                            text = "$userId",
                            style = MyStoryTheme.typography.bodyLarge
                        )
                        if (friendMetaInfoText.isNotEmpty()) {
                            Text(
                                text = friendMetaInfoText,
                                style = MyStoryTheme.typography.bodySmallGray,
                            )
                        }
                    }

                    if (!uiState.searchResult.isMe && !isInMyFriendsOrRequests) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = colorResource(id = R.color.primary)
                            ),
                            modifier = Modifier
                                .height(48.dp)
                                .align(Alignment.CenterVertically),
                            onClick = {
                                onAction(FriendAction.AddFriend)
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

@Composable
fun RequestScreen(uiState: FriendUiState, onClick: (Friend) -> Unit) {
    val requests = uiState.myInfo.requests
    if (requests.isNotEmpty()) {
        RequestList(
            friends = requests,
            title = stringResource(R.string.friend_request_users_info),
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Text(
            text = stringResource(R.string.friend_no_requests),
            style = MyStoryTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FriendListScreen(
    uiState: FriendUiState,
    showInfoBottomSheet: (Friend) -> Unit,
    showAddFriendBottomSheet: () -> Unit,
    onFriendLongClick: (Friend) -> Unit,
    onPendingFriendLongClick: (Friend) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            Text(
                text = stringResource(R.string.friend_me),
                style = MyStoryTheme.typography.bodyMediumGray,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            )
            FriendItem(
                friend = Friend(id = uiState.myInfo.id),
                isMe = true,
                profileImage = uiState.myInfo.profileImage.toInt(),
                onClick = { showInfoBottomSheet(it) },
                onLongClick = onFriendLongClick
            )
        }

        Box(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .height(48.dp)
                .background(color = colorResource(id = R.color.primary))
                .clickable {
                    showAddFriendBottomSheet()
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_person_add_white),
                contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.Center)
            )
        }

        FriendList(
            uiState = uiState,
            isFriend = true,
            title = stringResource(R.string.friend_mutual_friends),
            modifier = Modifier.fillMaxWidth(),
            onClick = showInfoBottomSheet,
            onLongClick = onFriendLongClick
        )
        FriendList(
            uiState = uiState,
            isFriend = false,
            title = stringResource(R.string.friend_pending_friends),
            modifier = Modifier.fillMaxSize(),
            onLongClick = onPendingFriendLongClick
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
            style = MyStoryTheme.typography.bodyMediumGray,
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
    uiState: FriendUiState,
    title: String,
    isFriend: Boolean,
    onClick: ((Friend) -> Unit)? = null,
    onLongClick: (Friend) -> Unit,
    modifier: Modifier = Modifier
) {
    val friends = if (isFriend) {
        uiState.myInfo.friends.filter { !it.isPending }
    } else {
        uiState.myInfo.friends.filter { it.isPending }
    }

    if (friends.isNotEmpty()) {
        Column {
            Text(
                text = title,
                style = MyStoryTheme.typography.bodyMediumGray,
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
                            friend = item,
                            profileImage = item.profileImage.toInt(),
                            onClick = {
                                onClick?.invoke(item)
                            },
                            onLongClick = onLongClick
                        )
                    } else {
                        FriendItem(
                            friend = item,
                            profileImage = item.profileImage.toInt(),
                            onClick = {
                                onClick?.invoke(item)
                            },
                            onLongClick = onLongClick
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
            painter = painterResource(id = R.drawable.ic_smile_blue),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
            text = friend.id,
            style = MyStoryTheme.typography.bodyLarge,
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
fun FriendItem(
    friend: Friend,
    profileImage: Int,
    isMe: Boolean = false,
    onClick: (Friend) -> Unit,
    onLongClick: (Friend) -> Unit
) {
    Box(
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    if (!isMe) {
                        onClick(friend)
                    }
                },
                onLongClick = {
                    if (!isMe) {
                        onLongClick(friend)
                    }
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            val profileResId = when (profileImage) {
                0 -> R.drawable.ic_smile_blue
                1 -> R.drawable.ic_smile_green
                2 -> R.drawable.ic_smile_orange
                else -> R.drawable.ic_smile_violet
            }

            Image(
                painter = painterResource(id = profileResId),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = friend.nickname,
                style = MyStoryTheme.typography.bodyMedium,
                modifier = Modifier
                    .width(0.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 5.dp)
            )
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FriendScreenDarkPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            FriendScreen(
                uiState = FriendUiState(
                    isLoading = false,
                    myInfo = User(
                        id = "홍길동",
                        profileImage = "0"
                    )
                ),
                sideEffect = { flowOf() },
                onAction = {},
                onNavigateToAddTimeCapsule = {},
                onNavigateToChangeInfo = {},
                onBack = { },
                showPopup = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendScreenPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            FriendScreen(
                uiState = FriendUiState(
                    isLoading = false,
                    myInfo = User(
                        id = "홍길동",
                        profileImage = "0"
                    )
                ),
                sideEffect = { flowOf() },
                onAction = {},
                onNavigateToAddTimeCapsule = {},
                onNavigateToChangeInfo = {},
                onBack = {},
                showPopup = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SearchScreenDarkPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BottomSheetScreen(FriendUiState(), onAction = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BottomSheetScreen(FriendUiState(), onAction = {})
        }
    }
}