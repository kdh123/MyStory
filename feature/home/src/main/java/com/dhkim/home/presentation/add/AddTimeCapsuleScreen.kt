@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class
)

package com.dhkim.home.presentation.add

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dhkim.common.DateUtil
import com.dhkim.designsystem.MyStoryTheme
import com.dhkim.dhcamera.camera.DhCamera
import com.dhkim.dhcamera.model.BackgroundText
import com.dhkim.dhcamera.model.FontElement
import com.dhkim.home.R
import com.dhkim.home.presentation.LocationSearchScreen
import com.dhkim.story.domain.model.SharedFriend
import com.dhkim.ui.onStartCollect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MissingPermission")
@Composable
fun AddTimeCapsuleScreen(
    uiState: AddTimeCapsuleUiState,
    sideEffect: () -> Flow<AddTimeCapsuleSideEffect>,
    onAction: (AddTimeCapsuleAction) -> Unit,
    permissionState: PermissionState,
    requestLocationPermission: @Composable () -> Unit,
    imageUrl: String,
    friendId: String,
    onBack: () -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    var showSharedFriendsBottomSheet by remember { mutableStateOf(false) }
    var showImagePickBottomSheet by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var showLocationBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }
    var showDateDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val locationBottomSheetState = rememberModalBottomSheetState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, flag)
            }
            onAction(AddTimeCapsuleAction.AddImage(imageUrl = "$it"))
        }
    }
    var contentHeight by remember {
        mutableStateOf(0.dp)
    }
    var saveButtonHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current

    lifecycle.onStartCollect(sideEffect()) {
        when (it) {
            is AddTimeCapsuleSideEffect.Message -> {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }

            is AddTimeCapsuleSideEffect.Completed -> {
                if (it.isCompleted) {
                    onBack()
                }
            }

            is AddTimeCapsuleSideEffect.ShowPlaceBottomSheet -> {
                if (!it.show) {
                    showLocationBottomSheet = false
                }
            }
        }
    }

    if (!permissionState.status.isGranted && permissionState.status.shouldShowRationale) {
        // Show rationale if needed
    } else {
        requestLocationPermission()
    }

    BackHandler {
        if (showImagePickBottomSheet || showSharedFriendsBottomSheet) {
            showImagePickBottomSheet = false
            showSharedFriendsBottomSheet = false
        } else {
            onBack()
        }
    }

    LaunchedEffect(friendId) {
        if (friendId.isNotEmpty()) {
            onAction(AddTimeCapsuleAction.AddFriend(friendId))
        }
    }

    LaunchedEffect(imageUrl) {
        if (imageUrl.isNotEmpty()) {
            onAction(AddTimeCapsuleAction.AddImage(imageUrl = imageUrl))
        }
    }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_black),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable {
                                onBack()
                            }
                    )
                    Box(
                        modifier = Modifier
                            .width(0.dp)
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = "새 타임캡슐",
                            style = MyStoryTheme.typography.bodyLargeBold,
                            modifier = Modifier
                                .align(Alignment.Center),
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_done_primary),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .alpha(0f)
                    )
                }
            }
        }
    ) {
        if (showLocationBottomSheet) {
            focusManager.clearFocus()
            ModalBottomSheet(
                sheetState = locationBottomSheetState,
                onDismissRequest = {
                    showLocationBottomSheet = false
                },
                modifier = Modifier
                    .fillMaxHeight(0.9f)
            ) {
                LocationSearchScreen(
                    uiState = uiState,
                    onAction = onAction
                )
            }
        }

        if (showSharedFriendsBottomSheet) {
            focusManager.clearFocus()
            ModalBottomSheet(
                onDismissRequest = {
                    showSharedFriendsBottomSheet = false
                }
            ) {
                SharedFriendList(
                    modifier = Modifier.padding(bottom = 48.dp),
                    sharedFriends = uiState.sharedFriends.toImmutableList(),
                    onAction = onAction
                )
            }
        }

        if (showImagePickBottomSheet) {
            focusManager.clearFocus()
            ModalBottomSheet(
                onDismissRequest = {
                    showImagePickBottomSheet = false
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(bottom = 48.dp)
                ) {
                    BottomMenuItem(
                        resId = R.drawable.ic_camera_graphic,
                        title = "카메라",
                        onClick = {
                            showImagePickBottomSheet = false
                            startCamera(
                                context = context,
                                uiState = uiState,
                                onAction = onAction
                            )
                        }
                    )
                    BottomMenuItem(
                        resId = R.drawable.ic_picture_graphic,
                        title = "갤러리",
                        onClick = {
                            showImagePickBottomSheet = false
                            launcher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
                .onGloballyPositioned {
                    contentHeight = with(density) {
                        it.size.height.toDp()
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(contentHeight - saveButtonHeight)
                    .verticalScroll(scrollState)
            ) {
                if (showDateDialog) {
                    Calender(
                        onAction = onAction,
                        onDismiss = {
                            showDateDialog = false
                        }
                    )
                }
                ContentsView(
                    content = uiState.content,
                    onAction = onAction
                )
                ImageListView(
                    imageUrls = uiState.imageUrls,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 20.dp),
                    onSelectPicture = {
                        scope.launch {
                            onAction(AddTimeCapsuleAction.SetSelectImageIndex(it))
                            showImagePickBottomSheet = true
                        }
                    }
                )
                Column {
                    SwitchMenuItem(
                        resId = R.drawable.ic_location_primary,
                        iconTint = if (uiState.checkLocation) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        },
                        title = "위치 체크",
                        subTitle = "개봉할 수 있는 위치를 지정합니다.",
                        isChecked = uiState.checkLocation,
                        onClick = { onAction(AddTimeCapsuleAction.SetCheckLocation(it)) }
                    )
                    if (uiState.checkLocation) {
                        MenuItem(
                            resId = -1,
                            title = uiState.placeName.ifEmpty { "알 수 없음" },
                            subTitle = "지정한 위치 근처에서 개봉할 수 있습니다.",
                            modifier = Modifier
                                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp, top = 0.dp),
                            onClick = { showLocationBottomSheet = true }
                        )
                    }

                    Divider(
                        thickness = 1.dp,
                        color = colorResource(id = R.color.light_gray),
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                    )

                    MenuItem(
                        resId = R.drawable.ic_calender_primary,
                        iconTint = if (uiState.openDate.isNotEmpty()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        },
                        title = uiState.openDate.ifEmpty { "개봉 날짜" },
                        subTitle = "지정한 날짜 이후에 개봉이 가능합니다.",
                        modifier = Modifier
                            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp, top = 15.dp),
                    ) {
                        showDateDialog = true
                    }

                    Divider(
                        thickness = 1.dp,
                        color = colorResource(id = R.color.light_gray),
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                    )

                    SwitchMenuItem(
                        resId = R.drawable.ic_smile_blue,
                        iconTint = if (uiState.isShare) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        },
                        title = "친구와 공유하기",
                        subTitle = "사진은 친구에게 공유되지 않습니다.",
                        isChecked = uiState.isShare
                    ) {
                        onAction(AddTimeCapsuleAction.SetCheckShare(it))
                        if (uiState.checkLocation) {
                            scope.launch {
                                scrollState.animateScrollTo(200)
                            }
                        }
                    }
                    if (uiState.isShare) {
                        val title = StringBuilder()
                        val checkedSharedFriends = uiState.sharedFriends.filter { it.isChecked }
                        checkedSharedFriends.forEachIndexed { index, item ->
                            if (index < checkedSharedFriends.size - 1) {
                                title.append("${item.nickname}, ")
                            } else {
                                title.append(item.nickname)
                            }
                        }

                        MenuItem(
                            resId = -1,
                            title = title.toString().ifEmpty { title.append("친구 목록").toString() },
                            subTitle = "서로 승낙한 친구에게만 공유할 수 있습니다.",
                            modifier = Modifier
                                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp, top = 0.dp),
                        ) {
                            scope.launch {
                                showSharedFriendsBottomSheet = true
                            }
                        }
                    }
                }
            }

            SaveButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .onGloballyPositioned {
                        saveButtonHeight = with(density) {
                            it.size.height.toDp()
                        }
                    }
            ) {
                onAction(AddTimeCapsuleAction.SaveTimeCapsule)
            }
        }
    }
}

@Composable
private fun SharedFriendList(
    modifier: Modifier = Modifier,
    sharedFriends: ImmutableList<SharedFriend>,
    onAction: (AddTimeCapsuleAction) -> Unit
) {
    if (sharedFriends.isNotEmpty()) {
        LazyColumn(
            modifier = modifier
        ) {
            items(
                items = sharedFriends,
                key = {
                    it.userId
                }
            ) {
                SharedFriendItem(
                    sharedFriend = it,
                    onAction = onAction
                )
            }
        }
    } else {
        Text(
            text = "친구가 존재하지 않습니다.",
            style = MyStoryTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = modifier
                .padding(bottom = 50.dp)
                .fillMaxWidth()
        )
    }
}

private fun startCamera(
    context: Context,
    uiState: AddTimeCapsuleUiState,
    onAction: (AddTimeCapsuleAction) -> Unit,
) {
    val backgroundItems = listOf(
        BackgroundText.Builder(context)
            .text("${uiState.placeName}\n${DateUtil.currentTime()}")
            .textAlign(DhCamera.TEXT_START)
            .align(DhCamera.BOTTOM_START)
            .padding(start = 10, bottom = 10)
            .showTextBackground()
            .build(),
        BackgroundText.Builder(context)
            .text(DateUtil.currentTime())
            .textAlign(DhCamera.TEXT_START)
            .align(DhCamera.BOTTOM_START)
            .padding(start = 10, bottom = 10)
            .showTextBackground()
            .build(),
        BackgroundText.Builder(context)
            .text("${uiState.placeName}\n${DateUtil.currentTime()}")
            .font(R.font.bm_dohyun_font)
            .textAlign(DhCamera.TEXT_START)
            .align(DhCamera.BOTTOM_START)
            .padding(start = 10, bottom = 10)
            .showTextBackground()
            .build(),
        BackgroundText.Builder(context)
            .text(DateUtil.currentTime())
            .font(R.font.bm_dohyun_font)
            .textAlign(DhCamera.TEXT_START)
            .align(DhCamera.BOTTOM_START)
            .padding(start = 10, bottom = 10)
            .showTextBackground()
            .build(),
        BackgroundText.Builder(context)
            .text("${uiState.placeName}\n${DateUtil.currentTime()}")
            .font(R.font.bm_euljiro_font)
            .textAlign(DhCamera.TEXT_START)
            .align(DhCamera.BOTTOM_START)
            .padding(start = 10, bottom = 10)
            .showTextBackground()
            .build(),
        BackgroundText.Builder(context)
            .text(DateUtil.currentTime())
            .font(R.font.bm_euljiro_font)
            .textAlign(DhCamera.TEXT_START)
            .align(DhCamera.BOTTOM_START)
            .padding(start = 10, bottom = 10)
            .showTextBackground()
            .build(),
    )

    val fontsIds = listOf(
        R.font.bm_dohyun,
        R.font.bm_euljiro_10,
        R.font.bm_euljiro_orae,
        R.font.bm_hanna_10,
        R.font.bm_hanna_pro,
        R.font.bm_jiro,
        R.font.bm_jua,
        R.font.bm_kirang,
        R.font.bm_yeonsun
    )

    val fonts = mutableListOf<FontElement>().apply {
        fontsIds.forEachIndexed { index, font ->
            add(
                FontElement.Builder()
                    .text("font $index")
                    .font(font)
                    .build()
            )
        }
    }

    DhCamera.Builder(context)
        .backgroundItems(backgroundItems)
        .enableInputText(true)
        .fontElements(fonts)
        .enableAddGalleryImage(true)
        .onCompleted(isFinishCamera = true) { savedUrl ->
            onAction(AddTimeCapsuleAction.AddImage(savedUrl))
        }
        .start()
}

@Composable
private fun SharedFriendItem(
    sharedFriend: SharedFriend,
    onAction: (AddTimeCapsuleAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                onAction(AddTimeCapsuleAction.CheckSharedFriend(sharedFriend.userId))
            }
    ) {
        Image(
            painter = if (sharedFriend.isChecked) {
                painterResource(id = R.drawable.ic_check_primary)
            } else {
                painterResource(id = R.drawable.ic_check_gray)
            },
            contentDescription = null,
            modifier = Modifier
                .padding(end = 10.dp)
                .align(Alignment.CenterVertically)
        )

        Text(
            text = sharedFriend.nickname,
            style = MyStoryTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun SaveButton(modifier: Modifier, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.primary)),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 20.dp),
        onClick = {
            onClick()
        }
    ) {
        Text(
            text = "저장",
            style = MyStoryTheme.typography.bodyLargeWhiteBold,
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.CenterHorizontally)

        )
    }
}

@Composable
fun BottomMenuItem(resId: Int, title: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 5.dp)
                .width(56.dp)
                .height(56.dp)
        )
        Text(
            text = title,
            style = MyStoryTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calender(
    onAction: (AddTimeCapsuleAction) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = DateUtil.dateToMills(DateUtil.dateAfterDays(1)),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= DateUtil.dateToMills(DateUtil.todayDate())
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = DateUtil.millsToDate(it)
                        onAction(AddTimeCapsuleAction.SetOpenDate(date))
                        onDismiss()
                    }
                }
            ) {
                Text(
                    text = "확인",
                    style = MyStoryTheme.typography.bodyMedium
                )
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(
                    text = "취소",
                    style = MyStoryTheme.typography.bodyMedium
                )
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun SwitchMenuItem(
    resId: Int,
    title: String,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    subTitle: String = "",
    isChecked: Boolean,
    onClick: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(id = resId),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .padding(top = 3.dp)
        )
        Column(
            modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = title,
                style = MyStoryTheme.typography.bodyLarge
            )
            if (subTitle.isNotEmpty()) {
                Text(
                    text = subTitle,
                    style = MyStoryTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.gray)
                )
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = { onClick(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(id = R.color.primary),
                checkedTrackColor = colorResource(id = R.color.teal_200),
                uncheckedTrackColor = colorResource(id = R.color.gray),
                uncheckedBorderColor = colorResource(id = R.color.gray),
            ),
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun MenuItem(
    resId: Int,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    title: String,
    subTitle: String = "",
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(
                id = if (resId == -1) {
                    R.drawable.ic_map_black
                } else {
                    resId
                }
            ),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 3.dp)
                .alpha(
                    if (resId == -1) {
                        0f
                    } else {
                        1f
                    }
                )
        )
        Column(
            modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = title,
                style = MyStoryTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subTitle.isNotEmpty()) {
                Text(
                    text = subTitle,
                    style = MyStoryTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.gray)
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_right_primary),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    onClick()
                }
        )
    }
}

@Composable
private fun ImageListView(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
    onSelectPicture: (Int) -> Unit
) {
    if (imageUrls.isEmpty()) {
        Box(modifier = modifier) {
            ImageView(modifier = Modifier.padding(start = 5.dp), imageUrl = "") {
                onSelectPicture(-1)
            }
        }
    } else {
        LazyRow(
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(items = imageUrls, key = { index, item ->
                "${item}${index}"
            }) { index, item ->
                ImageView(imageUrl = item) {
                    onSelectPicture(index)
                }
            }
            item {
                ImageView(imageUrl = "") {
                    onSelectPicture(-1)
                }
            }
        }
    }
}

@Composable
private fun ImageView(imageUrl: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.surfaceContainer
            } else {
                Color.White
            },
            contentColor = colorResource(id = R.color.light_gray)
        ),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        onClick = onClick,
        modifier = modifier
    ) {
        GlideImage(
            imageModel = {
                imageUrl.ifEmpty {
                    R.drawable.ic_add_gray
                }
            },
            modifier = Modifier
                .width(98.dp)
                .height(98.dp),
            previewPlaceholder = painterResource(id = R.drawable.ic_add_gray),
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentsView(
    content: String,
    onAction: (AddTimeCapsuleAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(15.dp)
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.gray),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        TextField(
            label = {
                Text(
                    text = "내용을 입력해주세요.",
                    style = MyStoryTheme.typography.labelSmall
                )
            },
            colors = textFieldColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            value = content,
            onValueChange = { onAction(AddTimeCapsuleAction.Typing(it)) },
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
class DefaultPermissionState : PermissionState {
    override val permission: String
        get() = ""
    override val status: PermissionStatus
        get() = PermissionStatus.Granted

    override fun launchPermissionRequest() {

    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AddTimeCapsuleScreenDarkPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddTimeCapsuleScreen(
                uiState = AddTimeCapsuleUiState(
                    placeName = "강남",
                    checkLocation = true,
                    openDate = "2025-08-12"
                ),
                sideEffect = { flowOf() },
                onAction = {},
                imageUrl = "imageUrl2",
                friendId = "",
                onBack = { },
                requestLocationPermission = {},
                permissionState = DefaultPermissionState()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTimeCapsuleScreenPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddTimeCapsuleScreen(
                uiState = AddTimeCapsuleUiState(
                    placeName = "강남",
                    checkLocation = true,
                    openDate = "2025-08-12"
                ),
                sideEffect = { flowOf() },
                onAction = {},
                imageUrl = "imageUrl2",
                friendId = "",
                onBack = { },
                requestLocationPermission = {},
                permissionState = DefaultPermissionState()
            )
        }
    }
}