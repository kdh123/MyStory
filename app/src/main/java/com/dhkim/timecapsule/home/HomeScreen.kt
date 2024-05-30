@file:OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class, ExperimentalNaverMapApi::class)

package com.dhkim.timecapsule.home

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import android.view.Gravity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.home.domain.Category
import com.dhkim.timecapsule.home.presentation.HomeViewModel
import com.dhkim.timecapsule.search.domain.Place
import com.dhkim.timecapsule.search.presentation.Place
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.location.FusedLocationSource
import retrofit2.HttpException


@SuppressLint("MissingPermission")
@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun HomeScreen(
    onNavigateToSearch: (Double, Double) -> Unit,
    onCategorySelected: (Boolean) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val places = uiState.places.collectAsLazyPagingItems()
    val peekHeight = if (uiState.category == Category.None) {
        onCategorySelected(false)
        0.dp
    } else {
        onCategorySelected(true)
        180.dp
    }
    var currentLocation by remember {
        mutableStateOf(LatLng(37.572389, 126.9769117))
    }
    val cameraPositionState = rememberCameraPositionState()
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(maxZoom = 20.0, minZoom = 5.0, locationTrackingMode = LocationTrackingMode.Follow)
        )
    }
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                logoMargin = PaddingValues(bottom = 138.dp, end = 10.dp),
                logoGravity = Gravity.BOTTOM or Gravity.END,
                isLocationButtonEnabled = false
            )
        )
    }
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    currentLocation = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)

                    cameraPositionState.move(
                        CameraUpdate.toCameraPosition(CameraPosition(LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0), 15.0))
                    )
                }
        } else {
            // Handle permission denial
        }
    }

    LaunchedEffect(locationPermissionState) {
        if (!locationPermissionState.status.isGranted && locationPermissionState.status.shouldShowRationale) {
            // Show rationale if needed
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /*val multiplePermission = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )*/

    val locationSource = object : FusedLocationSource(context) {
        override fun hasPermissions(): Boolean {
            return locationPermissionState.status.isGranted
        }

        override fun onPermissionRequest() {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = peekHeight,
        sheetContent = {
            if (uiState.category != Category.None) {
                PlaceList(places = places)
            }
        }
    ) { padding ->
        Map(
            places = places,
            locationSource = locationSource,
            cameraPositionState = cameraPositionState,
            mapProperties = mapProperties,
            mapUiSettings = mapUiSettings
        )

        Column(modifier = Modifier.wrapContentWidth()) {
            SearchBar(
                query = uiState.query,
                currentLocation = currentLocation,
                showClose = uiState.category != Category.None,
                onClose = viewModel::closeSearch,
                onNavigateToSearch = onNavigateToSearch
            )

            LazyRow(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(start = 10.dp)
            ) {
                items(Category.entries.filter { it != Category.None },
                    key = {
                        it.code
                    }) {
                    CategoryChip(category = it, isSelected = it == uiState.category) {
                        viewModel.searchPlacesByCategory(
                            category = it,
                            lat = currentLocation.latitude.toString(),
                            lng = currentLocation.longitude.toString()
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchBarColorsPreview() {
    SearchBar("관광명소", LatLng(34.4, 34.3), false, {}) { _, _ ->

    }
}

@Composable
fun SearchBar(
    query: String,
    currentLocation: LatLng,
    showClose: Boolean,
    onClose: () -> Unit,
    onNavigateToSearch: (Double, Double) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
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
                text = query.ifEmpty { "추억을 남기려는 장소를 검색하세요" },
                fontSize = 18.sp,
                color = if (query.isEmpty()) {
                    colorResource(id = R.color.gray)
                } else {
                    colorResource(id = R.color.black)
                },
                modifier = Modifier
                    .width(0.dp)
                    .weight(1f)
                    .clickable {
                        onNavigateToSearch(currentLocation.latitude, currentLocation.longitude)
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
                            onClose()
                        }
                )
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
            .clip(
                shape = RoundedCornerShape(10.dp)
            )
            .padding(end = 10.dp, bottom = 5.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
    ) {
        Text(
            text = category.type,
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 3.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    onClick(category)
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryChipPreview() {
    CategoryChip(category = Category.Cafe, isSelected = true) {}
}

@Composable
fun PlaceList(places: LazyPagingItems<Place>) {
    val state = places.loadState.refresh
    if (state is LoadState.Error) {
        if ((state.error) is HttpException) {

        }
        Log.e("errr", "err")
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            count = places.itemCount,
            key = places.itemKey(key = {
                it.id
            }),
            contentType = places.itemContentType()
        ) { index ->
            val item = places[index]
            if (item != null) {
                Place(place = item)
            }
        }
    }
}

@Composable
fun Map(
    places: LazyPagingItems<Place>,
    locationSource: FusedLocationSource,
    cameraPositionState: CameraPositionState,
    mapProperties: MapProperties,
    mapUiSettings: MapUiSettings,
) {
    Box(Modifier.fillMaxSize()) {
        NaverMap(
            locationSource = locationSource,
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings
        ) {
            places.itemSnapshotList.items.forEach { place ->
                Marker(
                    state = MarkerState(
                        position = LatLng(
                            place.lat.toDouble(),
                            place.lng.toDouble()
                        )
                    ),
                    onClick = {
                        true
                    },
                    captionText = place.name
                )
            }

        }
    }
}
