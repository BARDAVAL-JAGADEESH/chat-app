package com.bardaval.chattingapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bardaval.chattingapplication.DestinationScreen
import com.bardaval.chattingapplication.R
import com.bardaval.chattingapplication.navigateTo

enum class BottomnavigationItem(val icon: Int, val navDestination: DestinationScreen) {
    CHATLIST(R.drawable.chat, DestinationScreen.Chatlist),
    STATUSLIST(R.drawable.status, DestinationScreen.StatusList),
    PROFILE(R.drawable.profile, DestinationScreen.Profile)
}

@Composable
fun Bottommenu(
    selectedItem: BottomnavigationItem,
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.weight(1f))  // Pushes the Row to the bottom

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 4.dp)
                .background(Color.White),
        ) {
            for (item in BottomnavigationItem.values()) {
                Image(
                    painter = painterResource(id = item.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .weight(1f)
                        .clickable {
                            navigateTo(navController, item.navDestination.route)
                        },
                    colorFilter = if (item == selectedItem)
                        ColorFilter.tint(color = Color.Blue)
                    else
                        ColorFilter.tint(Color.Gray)
                )
            }
        }
    }
}




/*package com.bardaval.chattingapplication.screens

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bardaval.chattingapplication.DestinationScreen
import com.bardaval.chattingapplication.R
import com.bardaval.chattingapplication.navigateTo


enum class BottomnavigationItem(val icon: Int,val navDestination:DestinationScreen)
{

    CHATLIST(R.drawable.chat,DestinationScreen.Chatlist),
    STATUSLIST(R.drawable.status,DestinationScreen.StatusList),
    PROFILE(R.drawable.profile,DestinationScreen.Profile)
}
@Composable
fun Bottommenu (

    selectedItem: BottomnavigationItem,
    navController: NavController
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 4.dp)
            .background(Color.White),

    ){
 for (item in BottomnavigationItem.values())
 {
     Image(painter = painterResource(id = item.icon),
         contentDescription = null
             , modifier = Modifier.size(40.dp).padding(4.dp).weight(1f).clickable {
//making navigate when clicking --calling here navigateTo function
                 navigateTo(navController,item.navDestination.route)
         },
         colorFilter = if(item==selectedItem)
         ColorFilter.tint(color = Color.Blue)
         else
         ColorFilter.tint(Color.Gray)

     )


 }
    }


*/