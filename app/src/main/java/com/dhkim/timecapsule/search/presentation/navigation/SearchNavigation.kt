package com.dhkim.timecapsule.search.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.search.domain.Place
import com.dhkim.timecapsule.search.presentation.SearchScreen
import com.naver.maps.geometry.LatLng
/*import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dhkim.timecapsule.home.HomeScreen
import com.dhkim.timecapsule.main.Screen
import com.dhkim.timecapsule.search.presentation.SearchScreen
import com.naver.maps.geometry.LatLng*/
import kotlinx.serialization.Serializable

fun NavGraphBuilder.searchNavigation(onBack: (Place) -> Unit) {
    /*composable<SearchScreen> {
        val args = it.toRoute<SearchScreen>()
        SearchScreen(latLng = LatLng(args.lat.toDouble(), args.lng.toDouble()))
    }*/
    composable("search/{lat}/{lng}") { backStackEntry ->
        val lat = (backStackEntry.arguments?.getString("lat") ?: "0.0").toDouble()
        val lng = (backStackEntry.arguments?.getString("lng") ?: "0.0").toDouble()

        SearchScreen(latLng = LatLng(lat, lng), onBack = onBack)
    }
}

@Serializable
data class SearchScreen(
    val lat: String,
    val lng: String
)