package com.crmv.tfg_shop.navigation

sealed class AppScreen(val route: String) {
    object LoginScreen: AppScreen("LoginScreen")
    object HomeScreen: AppScreen("HomeScreen")
    object RegisterScreen: AppScreen("RegisterScreen")
    object Profilecreen: AppScreen("Profilecreen")
    object ChatScreen: AppScreen("ChatScreen")
    object DashboardScreen: AppScreen("DashboardScreen")
    object MainScreen: AppScreen("MainScreen")
    object CarritoScreen: AppScreen("CarritoScreen")
    object ProductDetailScreen: AppScreen("ProductDetailScreen")
    object MyComposable: AppScreen("MyComposable")
    object HombreScreen: AppScreen("HombreScreen")
    object MujerScreen: AppScreen("MujerScreen")

}
