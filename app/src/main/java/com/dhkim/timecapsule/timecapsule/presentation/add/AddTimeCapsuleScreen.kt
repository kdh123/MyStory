@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.dhkim.timecapsule.timecapsule.presentation.add

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.common.Constants
import com.dhkim.timecapsule.common.DateUtil
import com.dhkim.timecapsule.search.domain.Place
import com.dhkim.timecapsule.timecapsule.domain.BaseTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.MyTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.SendTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.SharedFriend
import com.dhkim.timecapsule.timecapsule.presentation.LocationSearchScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MissingPermission")
@Composable
fun AddTimeCapsuleScreen(
    imageUrl: String,
    place: Place,
    onNavigateToCamera: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddTimeCapsuleViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val state = rememberStandardBottomSheetState(
        skipHiddenState = false
    )
    val sharedFriendsBottomSheetState = rememberModalBottomSheetState()
    var showSharedFriendsBottomSheet by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val scaffoldState = rememberBottomSheetScaffoldState(state)
    var showBottomMenu = false
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, flag)
            }
            viewModel.addImage(imageUrl = it.toString())
        }
    }
    var showLocationBottomSheet by remember {
        mutableStateOf(false)
    }
    var showDateDialog by remember {
        mutableStateOf(false)
    }
    var currentLocation by remember {
        mutableStateOf(Constants.defaultLocation)
    }
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val locationBottomSheetState = rememberModalBottomSheetState()
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (place.lat == "" || place.lng == "") {
                        currentLocation = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                        viewModel.searchAddress("${currentLocation.latitude}", "${currentLocation.longitude}")
                    } else {
                        viewModel.initPlace(place)
                    }
                }
        } else {
            // Handle permission denial
        }
    }
    var contentHeight by remember {
        mutableStateOf(0.dp)
    }
    var saveButtonHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { type ->
            when (type) {
                is AddTimeCapsuleSideEffect.Message -> {
                    Toast.makeText(context, type.message, Toast.LENGTH_SHORT).show()
                }

                is AddTimeCapsuleSideEffect.Completed -> {
                    if (type.isCompleted) {
                        onBack()
                    }
                }

                is AddTimeCapsuleSideEffect.ShowPlaceBottomSheet -> {
                    if (!type.show) {
                        showLocationBottomSheet = false
                    }
                }
            }
        }
    }

    LaunchedEffect(locationPermissionState) {
        if (!locationPermissionState.status.isGranted && locationPermissionState.status.shouldShowRationale) {
            // Show rationale if needed
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    BackHandler {
        if (showBottomMenu) {
            scope.launch {
                scaffoldState.bottomSheetState.hide()
                showBottomMenu = false
            }
        } else {
            onBack()
        }
    }

    LaunchedEffect(imageUrl) {
        if (imageUrl.isNotEmpty()) {
            viewModel.addImage(imageUrl)
        }
    }

    if (showLocationBottomSheet) {
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
                onQuery = viewModel::onQuery,
                onClick = viewModel::onPlaceClick
            )
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                BottomMenuItem(
                    resId = R.drawable.ic_camera_graphic,
                    title = "카메라",
                    onClick = {
                        scope.launch {
                            scaffoldState.bottomSheetState.hide()
                            showBottomMenu = false
                            onNavigateToCamera()
                        }
                    }
                )
                BottomMenuItem(
                    resId = R.drawable.ic_picture_graphic,
                    title = "갤러리",
                    onClick = {
                        scope.launch {
                            scaffoldState.bottomSheetState.hide()
                            showBottomMenu = false
                            launcher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    }
                )
            }
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
                            modifier = Modifier
                                .align(Alignment.Center),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
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
                Divider(
                    thickness = 1.dp,
                    color = colorResource(id = R.color.light_gray)
                )
            }
        }
    ) {
        if (showSharedFriendsBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showSharedFriendsBottomSheet = false
                },
                sheetState = sharedFriendsBottomSheetState
            ) {
                SharedFriendList(
                    modifier = Modifier.padding(bottom = 50.dp),
                    sharedFriends = uiState.sharedFriends,
                    onClickCheckBox = viewModel::checkSharedFriend
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
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
                        onSave = viewModel::setOpenDate,
                        onDismiss = {
                            showDateDialog = false
                        }
                    )
                }
                ContentsView(
                    content = uiState.content,
                    onType = viewModel::typing
                )
                ImageListView(
                    imageUrls = uiState.imageUrls,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 20.dp),
                    onSelectPicture = {
                        scope.launch {
                            viewModel.setSelectImageIndex(index = it)
                            focusManager.clearFocus()
                            scaffoldState.bottomSheetState.expand()
                            showBottomMenu = true
                        }
                    }
                )
                Column {
                    SwitchMenuItem(
                        resId = if (uiState.checkLocation) {
                            R.drawable.ic_location_primary
                        } else {
                            R.drawable.ic_location_black
                        },
                        title = "위치 체크",
                        subTitle = "개봉할 수 있는 위치를 지정합니다.",
                        isChecked = uiState.checkLocation
                    ) {
                        viewModel.setCheckLocation(isChecked = it)
                    }
                    if (uiState.checkLocation) {
                        MenuItem(
                            resId = -1,
                            title = uiState.placeName.ifEmpty { "알 수 없음" },
                            subTitle = "지정한 위치 근처에서 개봉할 수 있습니다.",
                            modifier = Modifier
                                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp, top = 0.dp),
                            onClick = {
                                showLocationBottomSheet = true
                            }
                        )
                    }

                    Divider(
                        thickness = 1.dp,
                        color = colorResource(id = R.color.light_gray)
                    )
                    MenuItem(
                        resId = if (uiState.openDate.isNotEmpty()) {
                            R.drawable.ic_calender_primary
                        } else {
                            R.drawable.ic_calender_black
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
                        color = colorResource(id = R.color.light_gray)
                    )

                    SwitchMenuItem(
                        resId = if (uiState.isShare) {
                            R.drawable.ic_smile_blue
                        } else {
                            R.drawable.ic_face_black
                        },
                        title = "친구와 공유하기",
                        subTitle = "사진은 친구에게 공유되지 않습니다.",
                        isChecked = uiState.isShare
                    ) {
                        viewModel.setCheckSend(isChecked = it)
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
                                title.append("${item.userId}, ")
                            } else {
                                title.append(item.userId)
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
                viewModel.saveTimeCapsule()
            }
        }
    }
}

@Composable
private fun SharedFriendList(
    modifier: Modifier = Modifier,
    sharedFriends: List<SharedFriend>,
    onClickCheckBox: (String) -> Unit
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
                    onClickCheckBox = onClickCheckBox
                )
            }
        }
    } else {
        Text(
            textAlign = TextAlign.Center,
            text = "친구가 존재하지 않습니다.",
            modifier = modifier
                .padding(bottom = 50.dp)
                .fillMaxWidth()
        )
    }
}


@Composable
private fun SharedFriendItem(
    sharedFriend: SharedFriend,
    onClickCheckBox: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                onClickCheckBox(sharedFriend.userId)
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
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            text = sharedFriend.userId,
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SharedFriendItemPreview() {
    val sharedFriend = SharedFriend(
        userId = "hihi",
        isChecked = true,
        uuid = "12345"
    )
    SharedFriendItem(sharedFriend) {

    }
}

@Composable
private fun SaveButton(modifier: Modifier, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.primary)),
        shape = RoundedCornerShape(50.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 20.dp),
        onClick = {
            onClick()
        }
    ) {
        Text(
            text = "저장",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.CenterHorizontally)

        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SaveButtonPreview() {
    SaveButton(Modifier) {

    }
}

@Composable
fun BottomMenuItem(resId: Int, title: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .clickable {
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
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomMenuItemPreview() {
    BottomMenuItem(resId = R.drawable.ic_camera_graphic, title = "카메라") {

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calender(
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val startDate = DateUtil.dateAfterMonths(3)

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = DateUtil.dateToMills(DateUtil.dateAfterDays(1)),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= DateUtil.dateToMills(DateUtil.todayDate())
                //return utcTimeMillis >= DateUtil.dateToMills(startDate)
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
                        onSave(date)
                        onDismiss()
                    }
                }
            ) {
                Text(text = "확인")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "취소")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@Composable
private fun SwitchMenuItem(
    resId: Int,
    title: String,
    subTitle: String = "",
    isChecked: Boolean,
    onClick: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
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
                fontSize = 18.sp,
            )
            if (subTitle.isNotEmpty()) {
                Text(
                    text = subTitle,
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.gray)
                )
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = {
                onClick(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(id = R.color.primary),
                checkedTrackColor = colorResource(id = com.dhkim.camera.R.color.teal_200),
                uncheckedTrackColor = colorResource(id = R.color.gray),
                uncheckedBorderColor = colorResource(id = R.color.gray),
            ),
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SwitchMenuItemPreview() {
    SwitchMenuItem(
        resId = R.drawable.ic_map_black,
        isChecked = true,
        title = "위치 체크",
        subTitle = "이 위치 근처에서 타임캡슐을 개봉할 수 있습니다."
    ) {

    }
}

@Composable
private fun MenuItem(
    resId: Int,
    title: String,
    subTitle: String = "",
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(
                id = if (resId == -1) {
                    R.drawable.ic_map_black
                } else {
                    resId
                }
            ),
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
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subTitle.isNotEmpty()) {
                Text(
                    text = subTitle,
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.gray)
                )
            }
        }
        Image(
            painter = painterResource(id = R.drawable.ic_right_primary),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clickable {
                    onClick()
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MenuItemPreview() {
    MenuItem(resId = R.drawable.ic_calender_black, "개봉 날짜") {

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

@Preview
@Composable
private fun ImageListViewPreview() {
    ImageListView(imageUrls = listOf("1", "2")) {

    }
}

@Composable
private fun ImageView(imageUrl: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white),
            contentColor = colorResource(id = R.color.light_gray)
        ),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        onClick = onClick,
        modifier = modifier
    ) {
        GlideImage(
            imageModel = imageUrl,
            placeHolder = painterResource(id = R.drawable.ic_add_gray),
            modifier = Modifier
                .width(98.dp)
                .height(98.dp),
            previewPlaceholder = R.drawable.ic_add_gray,
            error = painterResource(id = R.drawable.ic_add_gray)
        )
    }
}

@Preview
@Composable
private fun ImageViewPreview() {
    ImageView(imageUrl = "") {

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentsView(
    modifier: Modifier = Modifier,
    content: String,
    onType: (String) -> Unit
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
            .background(color = colorResource(id = R.color.white))
    ) {
        TextField(
            label = {
                Text(text = "내용을 입력해주세요.")
            },
            colors = textFieldColors(
                containerColor = colorResource(id = R.color.white),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            value = content,
            onValueChange = onType,
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize()
        )
    }
}

@Preview
@Composable
private fun ContentViewPreview() {
    ContentsView(content = "안녕하세요안녕하세요안녕하세요안녕하세요") {

    }
}

@Preview(showBackground = true)
@Composable
private fun AddTimeCapsuleScreenPreview() {
    AddTimeCapsuleScreen(
        imageUrl = "imageUrl22",
        place = Place(),
        onNavigateToCamera = {},
        onBack = {}
    )
}

sealed class TimeCapsuleType(val timeCapsule: BaseTimeCapsule) {
    data class My(val myTimeCapsule: MyTimeCapsule) : TimeCapsuleType(myTimeCapsule)
    data class Send(val sendTimeCapsule: SendTimeCapsule) : TimeCapsuleType(sendTimeCapsule)
}