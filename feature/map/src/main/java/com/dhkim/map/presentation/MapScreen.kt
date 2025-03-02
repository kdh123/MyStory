@file:OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class,
)

package com.dhkim.map.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.dhkim.designsystem.MyStoryTheme
import com.dhkim.location.domain.model.Category
import com.dhkim.location.domain.model.Place
import com.dhkim.map.R
import com.dhkim.ui.LoadingProgressBar
import com.dhkim.ui.onStartCollect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import retrofit2.HttpException

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    uiState: MapUiState,
    sideEffect: () -> Flow<MapSideEffect>,
    onAction: (MapAction) -> Unit,
    locationPermissionState: PermissionState,
    onNavigateToSearch: (Double, Double) -> Unit,
    onNavigateToAddScreen: (Place) -> Unit,
    onHideBottomNav: (Place?) -> Unit,
    onInitSavedState: () -> Unit,
    naverMap: @Composable () -> Unit,
    requestPermission: @Composable () -> Unit
) {
    val bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)
    val lifecycle = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val places = uiState.places.collectAsLazyPagingItems()
    val paddingValues = WindowInsets.navigationBars.asPaddingValues()
    val peekHeight = if (uiState.category != Category.None) 300.dp else 0.dp

    lifecycle.onStartCollect(sideEffect()) {
        when (it) {
            is MapSideEffect.BottomSheet -> {
                scope.launch {
                    if (it.isHide) {
                        scaffoldState.bottomSheetState.hide()
                    } else {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            }
        }
    }

    BackHandler {
        if (uiState.query.isNotEmpty()) {
            onInitSavedState()
            onAction(MapAction.CloseSearch(isPlaceSelected = false))
        } else {
            (context as? Activity)?.finish()
        }
    }

    LaunchedEffect(uiState.selectedPlace) {
        onHideBottomNav(uiState.selectedPlace)
    }

    if (!locationPermissionState.status.isGranted && locationPermissionState.status.shouldShowRationale) {
        // Show rationale if needed
    } else {
        requestPermission()
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = peekHeight,
        sheetContent = {
            if (places.itemCount == 0) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    LoadingProgressBar(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            } else {
                PlaceList(
                    uiState = uiState,
                    onAction = onAction,
                    onHide = {}
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            Box(Modifier.fillMaxSize()) {
                naverMap()
            }

            Column(modifier = Modifier.wrapContentWidth()) {
                SearchBar(
                    query = uiState.query,
                    lat = uiState.currentLocation.latitude,
                    lng = uiState.currentLocation.longitude,
                    showClose = uiState.category != Category.None || uiState.selectedPlace != null,
                    onAction = onAction,
                    onNavigateToSearch = onNavigateToSearch,
                    onInitSavedState = onInitSavedState
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    modifier = Modifier
                        .wrapContentWidth()
                ) {
                    items(
                        Category.entries.filter { it != Category.None },
                        key = {
                            it.code
                        }) {
                        CategoryChip(
                            category = it, isSelected = it == uiState.category
                        ) { category ->
                            if (isCategory(category)) {
                                onAction(
                                    MapAction.SearchPlacesByCategory(
                                        category = it,
                                        lat = "${uiState.currentLocation.latitude}",
                                        lng = "${uiState.currentLocation.longitude}"
                                    )
                                )
                            } else {
                                onAction(
                                    MapAction.SearchPlacesByKeyword(
                                        query = category.type,
                                        lat = "${uiState.currentLocation.latitude}",
                                        lng = "${uiState.currentLocation.longitude}"
                                    )
                                )
                            }
                        }
                    }
                }
            }
            uiState.selectedPlace?.let {
                BottomPlace(
                    place = it,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .align(Alignment.BottomCenter),
                    onTimeCapsuleClick = { place ->
                        onNavigateToAddScreen(place)
                    }
                )
            }
        }
    }
}

private fun isCategory(category: Category): Boolean {
    val categoryPlaces = listOf(
        Category.Restaurant,
        Category.Cafe,
        Category.Attraction,
        Category.Resort,
        Category.Culture
    )

    return categoryPlaces.contains(category)
}

@Composable
fun BottomPlace(
    place: Place,
    modifier: Modifier,
    onTimeCapsuleClick: (Place) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .padding(vertical = 5.dp)
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = place.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = place.category,
                    color = Color.Gray,
                    fontSize = 12.sp,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = place.distance,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = place.address,
                    color = Color.Gray,
                    fontSize = 12.sp,
                )
            }
            if (place.phone.isNotEmpty()) {
                Text(
                    text = place.phone,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .align(Alignment.CenterVertically)
                .background(color = colorResource(id = R.color.primary))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    onTimeCapsuleClick(place)
                }
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_send_time_white),
                contentDescription = null,
                modifier = Modifier
                    .width(52.dp)
                    .height(52.dp)
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    lat: Double,
    lng: Double,
    showClose: Boolean,
    onAction: (MapAction) -> Unit,
    onNavigateToSearch: (Double, Double) -> Unit,
    onInitSavedState: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        border = BorderStroke(
            width = 1.dp,
            color = colorResource(id = R.color.primary)
        ),
        colors = CardDefaults.cardColors(colorResource(id = R.color.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.primary),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .background(color = colorResource(id = R.color.white))
                    .padding(10.dp)
            ) {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = query.ifEmpty { "추억을 남기려는 장소를 검색하세요" },
                    fontSize = 16.sp,
                    color = if (query.isEmpty()) {
                        colorResource(id = R.color.gray)
                    } else {
                        colorResource(id = R.color.black)
                    },
                    modifier = Modifier
                        .padding(vertical = 3.dp)
                        .width(0.dp)
                        .weight(1f)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onNavigateToSearch(lat, lng)
                        }
                        .align(Alignment.CenterVertically)
                )
                if (showClose) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_close_gray),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable {
                                onInitSavedState()
                                onAction(MapAction.CloseSearch(isPlaceSelected = false))
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(category: Category, isSelected: Boolean, onClick: (Category) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val color = if (isSelected) {
        colorResource(id = R.color.primary)
    } else {
        colorResource(id = R.color.white)
    }

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white),
        ),
        border = BorderStroke(1.dp, color = color),
        modifier = Modifier
            .height(45.dp)
            .clip(
                shape = RoundedCornerShape(10.dp)
            )
            .padding(bottom = 5.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick(category)
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
            Icon(
                tint = Color.Unspecified,
                painter = painterResource(id = category.resId()),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = category.type,
                modifier = Modifier
                    .padding(top = 3.dp, bottom = 3.dp, start = 3.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

fun Category.resId(): Int {
    return when (code) {
        "Popular" -> com.dhkim.location.R.drawable.ic_star_red
        "FD6" -> com.dhkim.location.R.drawable.ic_restaurant_gray
        "CE7" -> com.dhkim.location.R.drawable.ic_cafe_orange
        "EC1" -> com.dhkim.location.R.drawable.ic_run_primary
        "BG2" -> com.dhkim.location.R.drawable.ic_board_purple
        "PC3" -> com.dhkim.location.R.drawable.ic_computer_orange
        "CT5" -> com.dhkim.location.R.drawable.ic_book_skyblue
        "AT4" -> com.dhkim.location.R.drawable.ic_camera_green
        "CT1" -> com.dhkim.location.R.drawable.ic_attraction_sky_blue
        "DOG" -> com.dhkim.location.R.mipmap.ic_dog_orange
        "AD5" -> com.dhkim.location.R.drawable.ic_hotel_purple
        else -> com.dhkim.location.R.drawable.ic_time_primary
    }
}



@Composable
fun PlaceList(
    uiState: MapUiState,
    onAction: (MapAction) -> Unit,
    onHide: () -> Unit
) {
    val places = uiState.places.collectAsLazyPagingItems()
    val state = places.loadState.refresh
    if (state is LoadState.Error) {
        if ((state.error) is HttpException) {

        }
        Log.e("errr", "err")
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(0.9f)
            .padding(bottom = 80.dp)
    ) {
        items(
            count = places.itemCount,
            key = places.itemKey(key = { it.id }),
            contentType = places.itemContentType()
        ) { index ->
            val item = places[index]
            if (item != null) {
                PlaceItem(place = item, onAction = onAction) {
                    onHide()
                }
            }
        }
    }
}

@Composable
fun PlaceItem(place: Place, onAction: ((MapAction) -> Unit)? = null, onHide: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {
                onAction?.invoke(MapAction.SelectPlace(place))
                onHide()
            },
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = place.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Text(
                maxLines = 1,
                text = place.category,
                overflow = TextOverflow.Ellipsis,
                color = Color.Gray,
                fontSize = 12.sp,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = place.distance,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Text(
                text = place.address,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        if (place.phone.isNotEmpty()) {
            Text(
                text = place.phone,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
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

@Preview(showBackground = true)
@Composable
private fun MapScreenPreView() {
    MapScreen(
        uiState = MapUiState(),
        sideEffect = { flowOf() },
        onAction = {},
        locationPermissionState = DefaultPermissionState(),
        onNavigateToSearch = { _, _ -> },
        onNavigateToAddScreen = {},
        onHideBottomNav = {},
        onInitSavedState = {},
        requestPermission = {},
        naverMap = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.LightGray)
            ) {

            }
        }
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BottomPlaceDarkPreview() {
    val place = Place(
        id = "",
        name = "스타벅스",
        lat = "",
        lng = "",
        address = "인천시 미추홀구 주안동 인천시 미추홀구 주안동 인천시 미추홀구 주안동",
        category = "카페",
        distance = "300",
        phone = "010-1234-1234",
        url = "https://www.naver.com"
    )
    MyStoryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            BottomPlace(place = place, modifier = Modifier) {

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomPlacePreview() {
    val place = Place(
        id = "",
        name = "스타벅스",
        lat = "",
        lng = "",
        address = "인천시 미추홀구 주안동 인천시 미추홀구 주안동 인천시 미추홀구 주안동",
        category = "카페",
        distance = "300",
        phone = "010-1234-1234",
        url = "https://www.naver.com"
    )

    MyStoryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            BottomPlace(place = place, modifier = Modifier) {

            }
        }
    }
}