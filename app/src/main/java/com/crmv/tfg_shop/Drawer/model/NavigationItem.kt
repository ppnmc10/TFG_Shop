package com.crmv.tfg_shop.Drawer.model

import androidx.navigation.NavController
import com.crmv.tfg_shop.R
import com.crmv.tfg_shop.navigation.AppScreen

enum class NavigationItem(
    val title: String,
    val icon: Int,
    val route: String
) {
    Home(
        icon = R.drawable.baseline_home_filled_24,
        title = "Home",
        route = AppScreen.HomeScreen.route
    ),
    Profile(
        icon = R.drawable.baseline_person_24,
        title = "Profile",
        route = AppScreen.Profilecreen.route

    ),
    Premium(
        icon = R.drawable.baseline_diamond_24,
        title = "Cart",
        route = AppScreen.CarritoScreen.route

    ),
    SignOut(
        icon = R.drawable.baseline_settings_24,
        title = "Sign Out",
        route = AppScreen.HomeScreen.route
    )
}