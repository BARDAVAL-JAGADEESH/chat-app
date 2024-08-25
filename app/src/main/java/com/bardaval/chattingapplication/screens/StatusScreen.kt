package com.bardaval.chattingapplication.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bardaval.chattingapplication.CommonDriver
import com.bardaval.chattingapplication.CommonProgressBar
import com.bardaval.chattingapplication.CommonRow
import com.bardaval.chattingapplication.DestinationScreen
import com.bardaval.chattingapplication.LivechatViewModel
import com.bardaval.chattingapplication.TiteleText
import com.bardaval.chattingapplication.navigateTo

@Composable
fun StatusScreen(navController: NavController, viewModel: LivechatViewModel) {
    val inProgress = viewModel.inProgressStatus.value

    if (inProgress) {
        CommonProgressBar()
    } else {
        val statuses = viewModel.status.value
        val userData = viewModel.userData.value

        val myStatuses = statuses.filter {
            it.user.userId == userData?.userId
        }
        val otherStatuses = statuses.filter {
            it.user.userId != userData?.userId
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                viewModel.uploadStatus(uri)
            }
        }

        Scaffold(
            floatingActionButton = {
                FAB {
                    launcher.launch("image/*")
                }
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    TiteleText(txt = "Status")

                    if (statuses.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "No status available")
                        }
                    } else {
                        if (myStatuses.isNotEmpty()) {
                            CommonRow(
                                imageUrl = myStatuses[0].user.imageUrl,
                                name = myStatuses[0].user.name
                            ) {
                                navigateTo(
                                    navController = navController,
                                    DestinationScreen.SingleStatus.createRoute(myStatuses[0].user.userId!!)
                                )
                            }
                            CommonDriver()
                            val uniqueUsers = otherStatuses.map { it.user }.toSet().toList()
                            LazyColumn(modifier = Modifier.weight(1f)) {
                                items(uniqueUsers) { user ->
                                    CommonRow(imageUrl = user.imageUrl, name = user.name) {
                                        navigateTo(
                                            navController = navController,
                                            DestinationScreen.SingleStatus.createRoute(user.userId!!)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Bottommenu(
                    selectedItem = BottomnavigationItem.STATUSLIST,
                    navController = navController
                )
            }
        )
    }
}

@Composable
fun FAB(onFabClick: () -> Unit) {
    FloatingActionButton(
        onClick = onFabClick,
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = "Add Status",
            tint = Color.White
        )
    }
}
