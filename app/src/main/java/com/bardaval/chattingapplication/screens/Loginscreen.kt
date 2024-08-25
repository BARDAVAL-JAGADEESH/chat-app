package com.bardaval.chattingapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bardaval.chattingapplication.CheckSignedIn
import com.bardaval.chattingapplication.CommonProgressBar
import com.bardaval.chattingapplication.DestinationScreen
import com.bardaval.chattingapplication.LivechatViewModel
import com.bardaval.chattingapplication.R
import com.bardaval.chattingapplication.navigateTo

@Composable
fun Loginscreen(viewmodel: LivechatViewModel, navController: NavHostController) {

    CheckSignedIn(viewModel = viewmodel, navController = navController)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.login), // replace with your background image resource
            contentDescription = null,
            contentScale = ContentScale.Crop, // Ensures the image scales to fill the Box
            modifier = Modifier.fillMaxSize() // Fills the entire Box
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState())
                .padding(16.dp), // Added padding so content doesn't touch edges
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val emailstate = remember {
                mutableStateOf(TextFieldValue())
            }
            val passwordstate = remember {
                mutableStateOf(TextFieldValue())
            }

            val focus = LocalFocusManager.current

            Image(
                painter = painterResource(id = R.drawable.chat1),
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .padding(top = 16.dp)
            )

            Text(
                text = "Sign In",
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

            OutlinedTextField(
                value = emailstate.value,
                onValueChange = {
                    emailstate.value = it
                },
                label = { Text("Email") },
                modifier = Modifier.padding(8.dp)
            )

            OutlinedTextField(
                value = passwordstate.value,
                onValueChange = {
                    passwordstate.value = it
                },
                label = { Text("Password") },
                modifier = Modifier.padding(8.dp)
            )
            Button(
                onClick = {
                    viewmodel.loginIn(emailstate.value.text, passwordstate.value.text)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "SIGN IN")
            }
            Text(
                text = "New user account? Go to sign up",

                color = Color.White,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTo(navController, DestinationScreen.Signup.route)
                    }
            )
        }
    }

    if (viewmodel.inProgress.value) {
        CommonProgressBar()
    }
}
