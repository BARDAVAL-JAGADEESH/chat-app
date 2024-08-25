package com.bardaval.chattingapplication.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bardaval.chattingapplication.CommonImage
import com.bardaval.chattingapplication.LivechatViewModel

enum class Status {
    INITIAL, ACTIVE, COMPLETED
}

@Composable
fun Singlestatusscreen(navController: NavController, viewmodel: LivechatViewModel, userId: String) {

    val statuses = viewmodel.status.value.filter {
        it.user.userId == userId
    }

    if (statuses.isNotEmpty()) {
        val currentStatus = remember { mutableStateOf(0) }

        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)) {

            CommonImage(
                data = statuses[currentStatus.value].imageUrl,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            Row(modifier = Modifier.fillMaxSize()) {
                statuses.forEachIndexed { index, status ->
                    CustomProgressIndicator(
                        modifier = Modifier.weight(1f)
                            .height(7.dp)
                            .padding(1.dp),
                        status = when {
                            currentStatus.value < index -> Status.INITIAL
                            currentStatus.value == index -> Status.ACTIVE
                            else -> Status.COMPLETED
                        },
                        onComplete = {
                            if (currentStatus.value < statuses.size - 1) {
                                currentStatus.value++
                            } else {
                                navController.popBackStack()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomProgressIndicator(modifier: Modifier, status: Status, onComplete: () -> Unit) {
    var progress by remember { mutableStateOf(0f) }

    if (status == Status.ACTIVE) {
        val animatedProgress by animateFloatAsState(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5000),
            finishedListener = { onComplete.invoke() }
        )
        progress = animatedProgress
    } else {
        progress = if (status == Status.COMPLETED) 1f else 0f
    }

    LinearProgressIndicator(
        modifier = modifier,
        color = Color.Red,
        progress = progress
    )
}
