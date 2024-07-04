package com.dhkim.friend.presentation.changeInfo

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhkim.friend.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChangeFriendInfoScreen(
    userId: String,
    uiState: ChangeFriendInfoUiState,
    sideEffect: ChangeFriendInfoSideEffect,
    onInit: (String) -> Unit,
    onEditNickname: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(userId) {
        onInit(userId)
    }

    LaunchedEffect(sideEffect) {
        when (sideEffect) {
            is ChangeFriendInfoSideEffect.Completed -> {
                onBack()
            }

            is ChangeFriendInfoSideEffect.Message -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is ChangeFriendInfoSideEffect.None -> {}
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back_black),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            onBack()
                        }
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_done_primary),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterEnd)
                        .clickable {
                            onSave()
                        }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = it.calculateTopPadding())
        ) {
            Text(
                text = "닉네임",
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(bottom = 10.dp)
            )

            BasicTextField(
                maxLines = 1,
                singleLine = true,
                value = uiState.nickname,
                onValueChange = {
                    onEditNickname(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Divider(
                modifier = Modifier
                    .padding(top = 5.dp)
            )
            Text(
                text = "원래 닉네임 : ${uiState.id}",
                modifier = Modifier
                    .padding(top = 10.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChangeFriendInfoScreenPreview() {
    ChangeFriendInfoScreen(
        userId = "",
        uiState = ChangeFriendInfoUiState(
            id = "ABCdef",
            nickname = "홍길동"
        ),
        sideEffect = ChangeFriendInfoSideEffect.None,
        onInit = {},
        onEditNickname = {},
        onSave = {},
        onBack = {}
    )
}