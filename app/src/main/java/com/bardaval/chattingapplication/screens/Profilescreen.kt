



package com.bardaval.chattingapplication.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bardaval.chattingapplication.CommonDriver
import com.bardaval.chattingapplication.CommonImage
import com.bardaval.chattingapplication.CommonProgressBar
import com.bardaval.chattingapplication.DestinationScreen
import com.bardaval.chattingapplication.LivechatViewModel
import com.bardaval.chattingapplication.navigateTo

@Composable
fun Profilescreen(navController: NavController, viewmodel: LivechatViewModel) {
    val inProgress = viewmodel.inProgress.value
    if (inProgress) {
        CommonProgressBar()
    } else {
        val userData = viewmodel.userData.value
        var name by rememberSaveable { mutableStateOf(userData?.name ?: "") }
        var number by rememberSaveable { mutableStateOf(userData?.number ?: "") }

        Column {
            Profilecontent(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                viewmodel = viewmodel,
                name = name,
                number = number,
                onNameChange = { name = it },
                onNumberChange = { number = it },
                onSave = {
                    viewmodel.createOrUpdateProfile(
                        name = name,number=number
                    )
                },
                onBack = {
                    navigateTo(navController=navController, route = DestinationScreen.Chatlist.route)
                },
                onLogout = {
                    viewmodel.logout()
                    navigateTo(navController=navController, route = DestinationScreen.Login.route)
                }
            )
            Bottommenu(
                selectedItem = BottomnavigationItem.PROFILE, navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profilecontent(
    modifier: Modifier,
    viewmodel: LivechatViewModel,
    name: String,
    number: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onLogout: () -> Unit
) {
    val imageUrl = viewmodel.userData.value?.imageUrl

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Back", Modifier.clickable { onBack.invoke() })
            Text(text = "Save", Modifier.clickable { onSave.invoke() })
        }
        CommonDriver()
        ProfileImage(imageUrl = imageUrl, viewmodel = viewmodel)
        CommonDriver()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Name ", modifier = Modifier.width(100.dp))
            TextField(
                value = name,
                onValueChange = onNameChange,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Number ", modifier = Modifier.width(100.dp))
            TextField(
                value = number,
                onValueChange = onNumberChange,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )
            )
        }
        CommonDriver()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "LogOut", modifier = Modifier.clickable { onLogout.invoke() })
        }
    }
}

@Composable
fun ProfileImage(imageUrl: String?, viewmodel: LivechatViewModel) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewmodel.uploadProfileImage(uri)
        }
    }

    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)) {
        Column(
            modifier = Modifier
                .padding()
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {
                CommonImage(data = imageUrl)
            }
            Text(text = "Change Profile Picture")
        }
        val isLoding=viewmodel.inProgress.value
        if (isLoding) {
            CommonProgressBar()
        }
    }
}
