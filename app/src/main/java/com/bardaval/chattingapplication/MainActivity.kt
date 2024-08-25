package com.bardaval.chattingapplication


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import  androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.bardaval.chattingapplication.screens.ChatListscreen

import com.bardaval.chattingapplication.screens.Loginscreen
import com.bardaval.chattingapplication.screens.Profilescreen
import com.bardaval.chattingapplication.screens.Signupscreen
import com.bardaval.chattingapplication.screens.Singlestatusscreen
import com.bardaval.chattingapplication.screens.StatusScreen

import com.bardaval.chattingapplication.screens.singlechatscreen
import com.bardaval.chattingapplication.ui.theme.ChattingApplicationTheme
import dagger.hilt.android.AndroidEntryPoint


// Creating routing
sealed class DestinationScreen(val route: String) {
    object Signup : DestinationScreen("signup")
    object Login : DestinationScreen("login")
    object Profile : DestinationScreen("profile")
    object Chatlist : DestinationScreen("chats")
    object SingleChat : DestinationScreen("singlechat/{chatId}") {
        fun createRoute(id: String) = "singlechat/$id"
    }
    object StatusList : DestinationScreen("status")
    object SingleStatus : DestinationScreen("singlestatus/{userId}") {
        fun createRoute(userId: String) = "singlestatus/$userId"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChattingApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background

                ) {
                    ChatAppNavigation()
                }
            }
        }
    }


    @Composable
    fun ChatAppNavigation() {
        val navController = rememberNavController()
        var viewmodel = hiltViewModel<LivechatViewModel>()

        NavHost(navController = navController, startDestination = DestinationScreen.Signup.route) {
            composable(DestinationScreen.Signup.route) {
                Signupscreen(navController, viewmodel)
            }
            composable(DestinationScreen.Login.route) {
                Loginscreen(navController=navController, viewmodel=viewmodel)
            }
            composable(DestinationScreen.Chatlist.route) {
                ChatListscreen(navController=navController,viewmodel=viewmodel)
            }
            composable(DestinationScreen.SingleChat.route) {

                val chatId=it.arguments?.getString("chatId")
                chatId?.let {
                    singlechatscreen(navController = navController, viewmodel = viewmodel, chatId =chatId )

                }

            }
            composable(DestinationScreen.StatusList.route) {
                StatusScreen(navController=navController,viewModel=viewmodel)
            }

            composable(DestinationScreen.Profile.route) {
                Profilescreen(navController=navController,viewmodel=viewmodel)
            }
            composable(DestinationScreen.SingleStatus.route) {
                val userId= it.arguments?.getString("userId")
                userId?.let {
                    Singlestatusscreen(navController=navController,viewmodel=viewmodel, userId = it)
                }

            }
        }
    }
}



