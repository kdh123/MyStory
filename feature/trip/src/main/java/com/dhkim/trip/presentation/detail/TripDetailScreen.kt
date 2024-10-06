package com.dhkim.trip.presentation.detail

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dhkim.common.DateUtil
import com.dhkim.trip.R
import com.dhkim.trip.domain.model.TripImage
import com.dhkim.trip.domain.model.TripType
import com.dhkim.ui.ShimmerBrush
import com.dhkim.ui.WarningDialog
import com.dhkim.ui.noRippleClick
import com.dhkim.ui.onStartCollect
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TripDetailScreen(
    tripId: String,
    uiState: TripDetailUiState,
    sideEffect: Flow<TripDetailSideEffect>,
    onAction: (TripDetailAction) -> Unit,
    onNavigateToImageDetail: (String) -> Unit,
    onNavigateToSchedule: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    var showPermissionDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showMenuBottom by rememberSaveable {
        mutableStateOf(false)
    }
    var showDeletePopup by rememberSaveable {
        mutableStateOf(false)
    }
    var showImageMenuPopup by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedImageId by rememberSaveable {
        mutableStateOf("")
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { state ->
        state.keys.forEach {
            if (state[it] == false) {
                showPermissionDialog = true
            } else {
                if (it == Manifest.permission.READ_MEDIA_IMAGES) {
                    if (!uiState.isInit) {
                        onAction(TripDetailAction.InitTrip(tripId = tripId))
                    }
                }
            }
        }
    }

    if (showImageMenuPopup) {
        Dialog(
            onDismissRequest = {
                showImageMenuPopup = false
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
                        text = "메뉴",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "삭제",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAction(
                                    TripDetailAction.DeleteImage(
                                        tripId = tripId,
                                        imageId = selectedImageId
                                    )
                                )
                                selectedImageId = ""
                                showImageMenuPopup = false
                            }
                    )
                }
            }
        }
    }

    if (showPermissionDialog) {
        WarningDialog(
            dialogTitle = "저장소 권한 요청",
            dialogText = "이미지를 불러오기 위해서 저장소 권한을 허용해주세요.",
            onConfirmation = {
                showPermissionDialog = false
                val uri = Uri.fromParts("package", context.packageName, null)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    data = uri
                }
                context.startActivity(intent)
            },
            onDismissRequest = {
                showPermissionDialog = false
            }
        )
    }

    if (showMenuBottom) {
        ModalBottomSheet(
            onDismissRequest = {
                showMenuBottom = false
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = 48.dp)
            ) {
                MenuItem(
                    resId = R.drawable.ic_edit_black,
                    title = "수정",
                    onClick = {
                        onNavigateToSchedule(tripId)
                        showMenuBottom = false
                    }
                )

                MenuItem(
                    resId = R.drawable.ic_delete_black,
                    title = "삭제",
                    onClick = {
                        showMenuBottom = false
                        showDeletePopup = true
                    }
                )
            }
        }
    }

    if (showDeletePopup) {
        WarningDialog(
            dialogTitle = "삭제",
            dialogText = "정말 삭제하겠습니까?",
            onConfirmation = {
                onAction(TripDetailAction.DeleteTrip(tripId))
                onBack()
            },
            onDismissRequest = {
                showDeletePopup = false
            }
        )
    }

    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            } else {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        )
    }

    lifecycle.onStartCollect(sideEffect) {
        when (it) {
            is TripDetailSideEffect.LoadImages -> {
                val images = getImagesFromDateRange(
                    context = context,
                    startDate = "${it.startDate} 00:00:00",
                    endDate = "${it.endDate} 23:59:59"
                )
                onAction(TripDetailAction.LoadImages(tripId = tripId, images = images))
            }
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back_black),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterStart)
                        .clickable {
                            onBack()
                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_option_black),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterEnd)
                        .clickable {
                            showMenuBottom = true
                        }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
        ) {
            TripInfo(uiState = uiState)
            DateHeader(
                uiState = uiState,
                onAction = onAction
            )
            if (uiState.isLoading) {
                ImageLoadingScreen()
            } else {
                TripDetails(
                    uiState = uiState,
                    onShowImageMenuPopup = {
                        selectedImageId = it
                        showImageMenuPopup = true
                    },
                    onNavigateToImageDetail = onNavigateToImageDetail
                )
            }
        }
    }
}

@Composable
private fun ImageLoadingScreen() {
    val dummyList = mutableListOf<Int>().apply {
        repeat(21) {
            add(it)
        }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(items = dummyList, key = {
            it
        }) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(
                        brush = ShimmerBrush(targetValue = 1300f)
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingScreenPreview() {
    ImageLoadingScreen()
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TripInfo(uiState: TripDetailUiState) {
    Column(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
    ) {
        Row(

        ) {
            Text(
                text = uiState.title ?: "",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                modifier = Modifier
                    .padding(bottom = 5.dp)
            )
        }

        Text(
            text = "${uiState.startDate} - ${uiState.endDate}",
            color = colorResource(id = R.color.gray),
            modifier = Modifier
                .padding(bottom = 5.dp)
        )
        Text(
            text = uiState.type,
            color = colorResource(id = R.color.gray),
            modifier = Modifier
        )
    }
}

@Composable
private fun DateHeader(
    uiState: TripDetailUiState,
    onAction: (TripDetailAction) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .padding(vertical = 20.dp)
    ) {
        itemsIndexed(items = uiState.tripDates, key = { index, _ ->
            index
        }) { index, date ->
            Card(
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .width(84.dp)
                    .height(120.dp)
                    .noRippleClick {
                        onAction(TripDetailAction.SelectDate(selectedIndex = index))
                    }
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxSize()
                        .background(
                            color = if (index == uiState.selectedIndex) {
                                colorResource(id = R.color.primary)
                            } else {
                                colorResource(id = R.color.white)
                            }
                        )
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = date.date.third,
                        fontSize = 48.sp,
                        color = if (index == uiState.selectedIndex) {
                            colorResource(id = R.color.white)
                        } else {
                            colorResource(id = R.color.gray)
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )

                    Text(
                        textAlign = TextAlign.Center,
                        text = date.date.second.toInt().toMonth(),
                        fontSize = 20.sp,
                        color = if (index == uiState.selectedIndex) {
                            colorResource(id = R.color.white)
                        } else {
                            colorResource(id = R.color.gray)
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TripDetails(
    uiState: TripDetailUiState,
    onShowImageMenuPopup: (String) -> Unit,
    onNavigateToImageDetail: (String) -> Unit
) {
    if (!uiState.endDate.isNullOrEmpty() && DateUtil.isBefore(uiState.endDate)) {
        Text(
            text = "여행이 끝난 후 여행 기간 중에 찍었던 사진이 노출됩니다.",
            color = colorResource(id = R.color.gray),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )

        return
    }

    if (uiState.images == null) {
        return
    }

    if (uiState.images.isEmpty()) {
        Text(
            text = "이 날 찍었던 사진이 존재하지 않습니다.",
            color = colorResource(id = R.color.gray),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )

        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(items = uiState.images, key = {
            it.id
        }) {
            GlideImage(
                previewPlaceholder = painterResource(id = R.drawable.ic_launcher_background),
                imageModel = { it.imageUrl },
                modifier = Modifier
                    .aspectRatio(1f)
                    .combinedClickable(
                        onLongClick = {
                            onShowImageMenuPopup(it.id)
                        },
                        onClick = {
                            val imageUrl = URLEncoder.encode(
                                it.imageUrl,
                                StandardCharsets.UTF_8.toString()
                            )
                            onNavigateToImageDetail(imageUrl)
                        }
                    )
            )
        }
    }
}

suspend fun getImagesFromDateRange(
    context: Context,
    startDate: String,
    endDate: String
): List<TripImage> {
    val tripDetails = mutableListOf<TripImage>()
    val startMillis = DateUtil.dateToMills2(startDate)
    val endMillis = DateUtil.dateToMills2(endDate)
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.LATITUDE,
        MediaStore.Images.Media.LONGITUDE
    )
    val selection =
        "${MediaStore.Images.Media.DATE_TAKEN} >= ? AND ${MediaStore.Images.Media.DATE_TAKEN} <= ?"
    val selectionArgs = arrayOf(startMillis.toString(), endMillis.toString())
    val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} ASC"
    val query = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )

    withContext(Dispatchers.IO) {
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val dateTaken = cursor.getLong(dateTakenColumn)
                val date = DateUtil.millsToDate(dateTaken)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                val location = getImageLocation(context, uri)
                tripDetails.add(
                    TripImage(
                        id = "${System.currentTimeMillis()}",
                        date = date,
                        lat = location?.first ?: 0.0,
                        lng = location?.second ?: 0.0,
                        imageUrl = uri.toString(),
                        address = getAddress(
                            context,
                            location?.first ?: 0.0,
                            location?.second ?: 0.0
                        )
                    )
                )
            }
        }
    }

    return tripDetails
}

fun getImageLocation(context: Context, imageUri: Uri): Pair<Double, Double>? {
    val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
    return if (inputStream != null) {
        val exif = ExifInterface(inputStream)
        val latLong = FloatArray(2)
        if (exif.getLatLong(latLong)) {
            Pair(latLong[0].toDouble(), latLong[1].toDouble())
        } else {
            null
        }
    } else {
        null
    }
}

private fun getAddress(context: Context, lat: Double, lng: Double): String {
    val geocoder = Geocoder(context, Locale.KOREA)

    val getAddress: Address? = try {
        geocoder.getFromLocation(lat, lng, 1)?.get(0)
    } catch (_: Exception) {
        null
    }

    return getAddress?.run {
        val adminArea = adminArea ?: ""
        val subLocal = subLocality ?: ""
        val thoroughfare = thoroughfare ?: ""

        "$adminArea $subLocal $thoroughfare"
    } ?: ""
}

@Preview(showBackground = true)
@Composable
private fun TripDetailScreenPreview() {
    val images = mutableListOf<TripImage>().apply {
        repeat(10) {
            add(
                TripImage(
                    id = "$it"
                )
            )
        }
    }

    val uiState = TripDetailUiState(
        title = "서울, 경기 여행",
        type = TripType.Family.desc,
        startDate = "2024-03-03",
        endDate = "2024-03-10",
        images = images.toImmutableList()
    )

    TripDetailScreen(
        tripId = "",
        uiState = uiState,
        sideEffect = flowOf(),
        onAction = {},
        onNavigateToImageDetail = { },
        onNavigateToSchedule = {},
        onBack = {}
    )
}

fun Int.toMonth(): String {
    return when (this) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "April"
        5 -> "May"
        6 -> "Jun"
        7 -> "July"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        else -> "Dec"
    }
}
