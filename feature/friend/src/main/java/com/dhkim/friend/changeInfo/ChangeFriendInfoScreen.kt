package com.dhkim.friend.changeInfo

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dhkim.designsystem.MyStoryTheme
import com.dhkim.friend.R
import com.dhkim.ui.onStartCollect
import com.dhkim.user.domain.model.Friend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChangeFriendInfoScreen(
    friend: Friend,
    uiState: ChangeFriendInfoUiState,
    sideEffect: () -> Flow<ChangeFriendInfoSideEffect>,
    initInfo: (Friend) -> Unit,
    onEditNickname: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(friend) {
        initInfo(friend)
    }

    lifecycle.onStartCollect(sideEffect()) {
        when (it) {
            is ChangeFriendInfoSideEffect.Completed -> {
                onBack()
            }

            is ChangeFriendInfoSideEffect.Message -> {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
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

                Icon(
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
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                value = uiState.friend.nickname,
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
                text = "개인 코드 : ${uiState.friend.id}",
                modifier = Modifier
                    .padding(top = 10.dp)
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChangeFriendInfoScreenDarkPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ChangeFriendInfoScreen(
                friend = Friend(),
                uiState = ChangeFriendInfoUiState(
                    friend = Friend(id = "id00", nickname = "홍길동")
                ),
                sideEffect = { flowOf() },
                initInfo = {},
                onEditNickname = {},
                onSave = {},
                onBack = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChangeFriendInfoScreenPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ChangeFriendInfoScreen(
                friend = Friend(),
                uiState = ChangeFriendInfoUiState(
                    friend = Friend(id = "id00", nickname = "홍길동")
                ),
                sideEffect = { flowOf() },
                initInfo = {},
                onEditNickname = {},
                onSave = {},
                onBack = {}
            )
        }
    }
}