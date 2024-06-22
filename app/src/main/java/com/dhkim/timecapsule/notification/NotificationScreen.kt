package com.dhkim.timecapsule.notification

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.common.presentation.profileImage
import com.dhkim.timecapsule.timecapsule.domain.ReceivedTimeCapsule

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NotificationScreen(
    uiState: NotificationUiState,
    onNavigateToTimeCapsule: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold {
        Column {
            Image(
                painter = painterResource(id = R.drawable.ic_back_black),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp)
                    .clickable {
                        onBack()
                    }
            )
            if (uiState.timeCapsules.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp),
                    modifier = Modifier
                        .padding(bottom = it.calculateBottomPadding())
                ) {
                    items(items = uiState.timeCapsules, key = {
                        it.id
                    }) {
                        NotificationItem(timeCapsule = it, onNavigateToTimeCapsule = onNavigateToTimeCapsule)
                    }
                }
            } else {
                Text(
                    textAlign = TextAlign.Center,
                    text = "알림이 존재하지 않습니다.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun NotificationItem(
    timeCapsule: ReceivedTimeCapsule,
    onNavigateToTimeCapsule: () -> Unit
) {
    val profileImage = timeCapsule.profileImage.profileImage()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onNavigateToTimeCapsule()
            }
            .padding(horizontal = 10.dp)
    ) {
        Image(
            painter = painterResource(id = profileImage),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Column(
            modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(start = 5.dp)
        ) {
            Row {
                Text(
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    text = "${timeCapsule.sender}님이 타임캡슐을 공유하였습니다.",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
            Text(
                color = colorResource(id = R.color.gray),
                text = timeCapsule.date,
                modifier = Modifier
                    .padding(top = 5.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun NotificationScreenPreview() {
    val receivedList = mutableListOf<ReceivedTimeCapsule>()
    repeat(10) {
        val receivedTimeCapsule = ReceivedTimeCapsule(
            id = "$it",
            sender = "dhkim",
            date = "2024-07-08"
        )
        receivedList.add(receivedTimeCapsule)
    }

    val uiState = NotificationUiState(
        timeCapsules = receivedList
    )

    NotificationScreen(
        uiState = uiState,
        onNavigateToTimeCapsule = {

        },
        onBack = {

        }
    )
}

@Preview(showBackground = true)
@Composable
private fun NotificationItemPreview() {
    val timeCapsule = ReceivedTimeCapsule(
        sender = "kdh123"
    )
    NotificationItem(timeCapsule = timeCapsule, onNavigateToTimeCapsule = {})
}


