
package com.bardaval.chattingapplication.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bardaval.chattingapplication.CommonProgressBar
import com.bardaval.chattingapplication.LivechatViewModel
import com.bardaval.chattingapplication.TiteleText

@Composable
fun ChatListscreen(navController: NavHostController, viewmodel: LivechatViewModel) {
    val inProgress = viewmodel.inProcessChats

    if (inProgress.value) {
        CommonProgressBar()
    } else {
        val chats = viewmodel.chats.value
        val userData = viewmodel.userData.value

        val showDialog = remember {
            mutableStateOf(false)
        }

        val onFabClick: () -> Unit = { showDialog.value = true }

        val onDismiss: () -> Unit = { showDialog.value = false }

        val onAddChat: (String) -> Unit = {
            viewmodel.onAddChat(it)
            showDialog.value = false
        }

        Scaffold(
            floatingActionButton = {
                FAB(
                    showDialog = showDialog.value,
                    onFabClick = onFabClick,
                    onDismiss = onDismiss,
                    onAddChat = onAddChat
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    TiteleText(txt = "Chats")

                    if(chats.isEmpty()){
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                            ) {

                            Text(text = "No Chats Available ")
                        }
                    }
                    Bottommenu(
                        selectedItem = BottomnavigationItem.CHATLIST,
                        navController = navController
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAB(
    showDialog: Boolean,
    onFabClick: () -> Unit,
    onDismiss: () -> Unit,
    onAddChat: (String) -> Unit
) {
    val addChatNumber = remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismiss.invoke()
                addChatNumber.value = ""
            },
            confirmButton = {
                Button(onClick = { onAddChat(addChatNumber.value) }) {
                    Text(text = "Add Chat")
                }
            },
            title = { Text(text = "Add Chat") },
            text = {
                OutlinedTextField(
                    value = addChatNumber.value,
                    onValueChange = {
                        addChatNumber.value = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        )
    }

    FloatingActionButton(
        onClick = onFabClick,
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 40.dp)
    ) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color.Gray)
    }
}









