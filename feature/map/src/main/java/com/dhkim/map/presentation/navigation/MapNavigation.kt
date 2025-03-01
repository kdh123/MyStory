package com.dhkim.map.presentation.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.view.Gravity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.dhkim.location.domain.model.Place
import com.dhkim.map.presentation.MapAction
import com.dhkim.map.presentation.MapScreen
import com.dhkim.map.presentation.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
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

const val MAP_ROUTE = "map"

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalNaverMapApi::class)
fun NavGraphBuilder.mapScreen(
    onNavigateToSearch: (Double, Double) -> Unit,
    onNavigateToAdd: (Place) -> Unit,
    onHideBottomNav: (Place?) -> Unit,
    onInitSavedState: () -> Unit
) {
    composable(MAP_ROUTE) {
        val context = LocalContext.current
        val viewModel = hiltViewModel<MapViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect = remember { viewModel.sideEffect }
        val place = it.savedStateHandle.get<Place>("place")
        val places = uiState.places.collectAsLazyPagingItems()
        val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
        val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        val currentLocation = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                        viewModel.onAction(MapAction.UpdateCurrentLocation(currentLocation))
                    }
            }
        }
        val locationSource = rememberFusedLocationSource()
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
        val latLng = when {
            places.itemCount > 0 -> {
                LatLng(places[0]?.lat?.toDouble() ?: 0.0, places[0]?.lng?.toDouble() ?: 0.0)
            }

            uiState.selectedPlace != null -> {
                LatLng(uiState.selectedPlace!!.lat.toDouble(), uiState.selectedPlace!!.lng.toDouble())
            }

            else -> {
                uiState.currentLocation
            }
        }

        LaunchedEffect(places.itemSnapshotList, uiState.selectedPlace, uiState.currentLocation) {
            cameraPositionState.move(
                CameraUpdate.toCameraPosition(
                    CameraPosition(
                        latLng,
                        15.0
                    )
                )
            )
        }

        MapScreen(
            uiState = uiState,
            sideEffect = { sideEffect },
            onAction = remember(viewModel) { viewModel::onAction },
            locationPermissionState = locationPermissionState,
            place = { place },
            onNavigateToSearch = onNavigateToSearch,
            onHideBottomNav = onHideBottomNav,
            onInitSavedState = onInitSavedState,
            onNavigateToAddScreen = onNavigateToAdd,
            requestPermission = {
                LaunchedEffect(locationPermissionState) {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            naverMap = {
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
                                    uiState.selectedPlace!!.lat.toDouble(),
                                    uiState.selectedPlace!!.lng.toDouble()
                                )
                            ),
                            onClick = {
                                place?.let(MapAction::SelectPlace)
                                true
                            },
                            captionText = uiState.selectedPlace!!.name
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
                                    viewModel.onAction(MapAction.SelectPlace(place))
                                    true
                                },
                                captionText = place.name
                            )
                        }
                    }
                }
            }
        )
    }
}