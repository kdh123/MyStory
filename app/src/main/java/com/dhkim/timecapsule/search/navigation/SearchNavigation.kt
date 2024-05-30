package com.dhkim.timecapsule.search.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.search.presentation.SearchScreen
import com.naver.maps.geometry.LatLng
/*import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dhkim.timecapsule.home.HomeScreen
import com.dhkim.timecapsule.main.Screen
import com.dhkim.timecapsule.search.presentation.SearchScreen
import com.naver.maps.geometry.LatLng*/
import kotlinx.serialization.Serializable

fun NavGraphBuilder.searchNavigation() {
    /*composable<SearchScreen> {
        val args = it.toRoute<SearchScreen>()
        SearchScreen(latLng = LatLng(args.lat.toDouble(), args.lng.toDouble()))
    }*/
    composable("search/{lat}/{lng}") { backStackEntry ->
        val lat = (backStackEntry.arguments?.getString("lat") ?: "0.0").toDouble()
        val lng = (backStackEntry.arguments?.getString("lng") ?: "0.0").toDouble()

        SearchScreen(latLng = LatLng(lat, lng))

    }

    /*composable("diaryDetail/{diaryId}") { backStackEntry ->
        val viewModel = backStackEntry.sharedViewModel<DiaryDetailViewModel>(navController = navController)

        DiaryDetailScreen(
            diaryId = backStackEntry.arguments?.getString("diaryId") ?: "",
            viewModel = viewModel,
            onNavigateToWrite = onNavigateToEdit,
            onDelete = onCompleted
        )
    }*/
}

@Serializable
data class SearchScreen(
    val lat: String,
    val lng: String
)