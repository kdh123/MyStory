package com.dhkim.timecapsule.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.home.presentation.navigation.homeNavigation
import com.dhkim.timecapsule.search.navigation.searchNavigation
import com.dhkim.timecapsule.timecapsule.presentation.navigation.timeCapsuleNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.TimeCapsule)
    var isCategorySelected by remember {
        mutableStateOf(true)
    }
    val isBottomNavShow = navController.currentBackStackEntryAsState().value?.destination?.route in listOf(Screen.Home.route, Screen.TimeCapsule.route)
            && isCategorySelected

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomNavShow,
                enter = fadeIn() + slideIn { IntOffset(0, it.height) },
                exit = fadeOut() + slideOut { IntOffset(0, it.height) }
            ) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.Bottom),
                    containerColor = colorResource(id = R.color.white),
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    items.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                        NavigationBarItem(
                            icon = {
                                if (isSelected) {
                                    Icon(painterResource(id = screen.selected), contentDescription = null, tint = Color.Unspecified)
                                } else {
                                    Icon(painterResource(id = screen.unSelected), contentDescription = null, tint = Color.Unspecified)
                                }
                            },
                            label = {
                                if (isSelected) {
                                    Text(
                                        screen.title,
                                        color = colorResource(id = R.color.primary),
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(screen.title)
                                }
                            },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPdding ->
        NavHost(
            modifier = Modifier
                .fillMaxSize(),
            navController = navController,
            startDestination = "home"
        ) {
            homeNavigation(
                scaffoldState = scaffoldState,
                onCategorySelected = {
                    isCategorySelected = it
                },
                onNavigateToSearch = { lat, lng ->
                    navController.navigate("search/$lat/$lng")
                },
                onInitSavedState = {
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("place", null)
                }
            )
            timeCapsuleNavigation()
            searchNavigation {
                navController.popBackStack()
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("place", it)
            }
        }

    }
}

sealed class Screen(
    val title: String, val selected: Int, val unSelected: Int, val route: String
) {
    object Home : Screen("홈", R.drawable.ic_home_primary, R.drawable.ic_home_black, "home")
    object TimeCapsule : Screen("타임캡슐", R.drawable.ic_time_primary, R.drawable.ic_time_black, "timeCapsule")
}