package com.bardaval.chattingapplication.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bardaval.chattingapplication.CommonImage
import com.bardaval.chattingapplication.LivechatViewModel
import com.bardaval.chattingapplication.data.Message


@Composable
fun singlechatscreen(navController: NavController,viewmodel:LivechatViewModel,chatId:String) {

    var reply by rememberSaveable {
        mutableStateOf("")
    }
    val onSendreply = {
        viewmodel.onSendReply(chatId, reply)
        reply = ""
    }
    val chatMessage=viewmodel.chatMessages
    val myuser=viewmodel.userData.value
    var currenChat=viewmodel.chats.value.first{it.chatId==chatId}
    val chatUser= if (myuser?.userId == currenChat.user1.userId)
        currenChat.user2
    else
        currenChat.user1

 LaunchedEffect(key1 = Unit) {
viewmodel.populateMessages(chatId)

 }
    BackHandler {
        viewmodel.depopulateMessage()

    }
    Column {

chatHeader(name = chatUser.name?:"", imageUrl = chatUser.imageUrl?:"") {
    navController.popBackStack()
    viewmodel.depopulateMessage()


      }
        MessageBox(modifier = Modifier.weight(1f), chatMessages =chatMessage.value , currentUserId =myuser?.userId?:"" )
        ReplayBox(reply = reply, onReplyChange = { reply = it }, onSendreply = onSendreply)

    }
}
@Composable
fun MessageBox(modifier: Modifier,chatMessages: List<Message>,currentUserId:String)
{
LazyRow (modifier=Modifier){
    items(chatMessages){

        msg->
        val alignment= if (msg.sendby == currentUserId)  Alignment.End else Alignment.Start

        val color= if (msg.sendby == currentUserId) Color(0xFF68C400) else Color(0xFF0F3B5F)




        Column(modifier=Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = alignment
            ) {
            Text(text = msg.message?:"",modifier=Modifier.clip(RoundedCornerShape(8.dp)).background(color).padding(12.dp),

                color=Color.White,
                fontWeight = FontWeight.Bold
        )
        }
    }

}
}
@Composable
fun chatHeader(name:String,imageUrl:String,onBackClicked:()->Unit)
{//lambda funtion type is unit

    Row (modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically

    ){
        Icon(Icons.Rounded.ArrowBack, contentDescription =null, modifier = Modifier
            .clickable {
                onBackClicked.invoke()

            }
            .padding(8.dp)
        )
        CommonImage(
            data = imageUrl,
            modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
        )

        Text(text = name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))


    }
}
@Composable
fun ReplayBox(reply:String,onReplyChange:(String)->Unit,onSendreply:()->Unit){

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            TextField(value = reply, onValueChange =onReplyChange, maxLines = 3 )
            Button(onClick =  onSendreply) {

                Text(text = "Send")

            }

        }
    }

}