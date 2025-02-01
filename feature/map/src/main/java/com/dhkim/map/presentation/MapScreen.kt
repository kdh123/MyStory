@file:OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalNaverMapApi::class
)

package com.dhkim.map.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.util.Log
import android.view.Gravity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.dhkim.common.Constants
import com.dhkim.location.domain.model.Category
import com.dhkim.location.domain.model.Place
import com.dhkim.map.R
import com.dhkim.ui.LoadingProgressBar
import com.dhkim.ui.onStartCollect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.HttpException

@SuppressLint("MissingPermission")
@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapScreen(
    uiState: MapUiState,
    sideEffect: () -> Flow<MapSideEffect>,
    onAction: (MapAction) -> Unit,
    place: () -> Place?,
    onNavigateToSearch: (Double, Double) -> Unit,
    onNavigateToAddScreen: (Place) -> Unit,
    onHideBottomNav: (Place?) -> Unit,
    onInitSavedState: () -> Unit
) {
    val bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)
    val lifecycle = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val places = uiState.places.collectAsLazyPagingItems()
    val paddingValues = WindowInsets.navigationBars.asPaddingValues()
    val peekHeight = if (uiState.category != Category.None) 300.dp else 0.dp
    var currentLocation by remember { mutableStateOf(Constants.defaultLocation) }
    val cameraPositionState = rememberCameraPositionState()
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                maxZoom = 20.0,
                minZoom = 5.0,
                locationTrackingMode = LocationTrackingMode.NoFollow
            )
        )
    }
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                logoMargin = PaddingValues(bottom = 86.dp, end = 10.dp),
                logoGravity = Gravity.BOTTOM or Gravity.END,
                isLocationButtonEnabled = false
            )
        )
    }

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

    LaunchedEffect(places.itemSnapshotList, uiState.selectedPlace, currentLocation) {
        val latLng = when {
            places.itemCount > 0 -> {
                LatLng(places[0]?.lat?.toDouble() ?: 0.0, places[0]?.lng?.toDouble() ?: 0.0)
            }

            uiState.selectedPlace != null -> {
                LatLng(uiState.selectedPlace.lat.toDouble(), uiState.selectedPlace.lng.toDouble())
            }

            else -> {
                currentLocation
            }
        }

        cameraPositionState.move(
            CameraUpdate.toCameraPosition(
                CameraPosition(
                    latLng,
                    15.0
                )
            )
        )
    }

    LaunchedEffect(place()?.id) {
        place()?.let(MapAction::SelectPlace)
    }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    currentLocation = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
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

    val locationSource = rememberFusedLocationSource()

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
        containerColor = colorResource(id = R.color.white)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            Box(Modifier.fillMaxSize()) {
                NaverMap(
                    locationSource = locationSource,
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = mapUiSettings,
                    modifier = Modifier.padding()
                ) {
                    if (uiState.selectedPlace != null) {
                        Marker(
                            state = MarkerState(
                                position = LatLng(
                                    uiState.selectedPlace.lat.toDouble(),
                                    uiState.selectedPlace.lng.toDouble()
                                )
                            ),
                            onClick = {
                                place()?.let {
                                    onAction(MapAction.SelectPlace(it))
                                }
                                true
                            },
                            captionText = uiState.selectedPlace.name
                        )
                    } else {
                        uiState.places.collectAsLazyPagingItems().itemSnapshotList.items.forEach { place ->
                            Marker(
                                state = MarkerState(
                                    position = LatLng(
                                        place.lat.toDouble(),
                                        place.lng.toDouble()
                                    )
                                ),
                                onClick = {
                                    onAction(MapAction.SelectPlace(place))
                                    true
                                },
                                captionText = place.name
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.wrapContentWidth()) {
                SearchBar(
                    query = uiState.query,
                    lat = currentLocation.latitude,
                    lng = currentLocation.longitude,
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
                                        lat = "${currentLocation.latitude}",
                                        lng = "${currentLocation.longitude}"
                                    )
                                )
                            } else {
                                onAction(
                                    MapAction.SearchPlacesByKeyword(
                                        query = category.type,
                                        lat = "${currentLocation.latitude}",
                                        lng = "${currentLocation.longitude}"
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
                        .background(color = colorResource(id = R.color.white))
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
                    color = colorResource(id = R.color.primary),
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
    BottomPlace(place = place, modifier = Modifier) {

    }
}

@Preview(showBackground = true)
@Composable
private fun SearchBarColorsPreview() {
    SearchBar(
        query = "관광명소",
        lat = 34.4,
        lng = 35.5,
        showClose = false,
        onAction = { _ -> },
        onNavigateToSearch = { _, _ -> },
        onInitSavedState = {}
    )
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

@Preview(showBackground = true)
@Composable
private fun CategoryChipPreview() {
    CategoryChip(category = Category.Cafe, isSelected = true) {}
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
                color = colorResource(id = R.color.primary),
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}
