package com.crmv.tfg_shop.navigation

import HomeScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.crmv.tfg_shop.screen.CarritoScreen
import com.crmv.tfg_shop.screen.ChatScreen
import com.crmv.tfg_shop.screen.HombreScreen
import com.crmv.tfg_shop.screen.LoginScreen
import com.crmv.tfg_shop.screen.MujerScreen
import com.crmv.tfg_shop.screen.MyComposable
import com.crmv.tfg_shop.screen.ProfileScreen
import com.crmv.tfg_shop.screen.RegisterScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.rpc.context.AttributeContext.Auth

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreen.LoginScreen.route)
    {
        composable(route = "LoginScreen"){
            LoginScreen(navController = navController)
        }
        composable(route = "MyComposable"){
            MyComposable()
        }
        composable(route = "HomeScreen"){
            HomeScreen(navController = navController)
        }
        composable(route = "RegisterScreen"){
            RegisterScreen(navController = navController)
        }
        composable(route = "Profilecreen"){
            ProfileScreen(navController)
        }
        composable(route = "HombreScreen"){
            HombreScreen(navController)
        }
        composable(route = "CarritoScreen"){
            CarritoScreen(navController = navController)
        }

        composable(route = "MujerScreen"){
            MujerScreen(navController = navController)
        }
        composable(
            route = "ChatScreen/{otherUserId}",
            arguments = listOf(navArgument("otherUserId") { type = NavType.StringType })
        ) { backStackEntry ->
            val otherUserId = backStackEntry.arguments?.getString("otherUserId")
            if (otherUserId != null) {
                ChatScreen(otherUserId = otherUserId )
            }
        }
    }
}
