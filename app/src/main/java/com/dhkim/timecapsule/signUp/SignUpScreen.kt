@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.dhkim.timecapsule.signUp

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhkim.timecapsule.main.MainActivity
import com.dhkim.timecapsule.R
import com.dhkim.ui.LoadingProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    uiState: SignUpUiState,
    sideEffect: SignUpSideEffect,
    onQuery: (String) -> Unit,
    onProfileSelect: (Int) -> Unit,
    onSignUp: () -> Unit
) {
    val context = LocalContext.current
    val profileImages = listOf(
        R.drawable.ic_smile_blue,
        R.drawable.ic_smile_violet,
        R.drawable.ic_smile_green,
        R.drawable.ic_smile_orange
    )
    val selectedProfile = uiState.profileImage

    LaunchedEffect(sideEffect) {
        when (sideEffect) {
            SignUpSideEffect.None -> {}

            is SignUpSideEffect.Message -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is SignUpSideEffect.Completed -> {
                Intent(context, MainActivity::class.java).run {
                    context.startActivity(this)
                    (context as? Activity)?.finish()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {
            Text(
                text = "프로필 선택",
                modifier = Modifier
                    .padding(10.dp),
                fontSize = 18.sp
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(items = profileImages, key = { index, item ->
                    item
                }) { index, item ->
                    val modifier = if (index == selectedProfile) {
                        Modifier
                            .aspectRatio(1f)
                            .border(
                                width = 3.dp,
                                color = colorResource(id = R.color.primary),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .background(color = colorResource(id = R.color.light_gray))
                    } else {
                        Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(color = colorResource(id = R.color.light_gray))
                    }

                    Box(
                        modifier = modifier
                            .clickable {
                                onProfileSelect(index)
                            }
                    ) {
                        Image(
                            painter = painterResource(id = item),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(45.dp)
                                .fillMaxSize()
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            Text(
                fontSize = 18.sp,
                text = "아이디를 입력하세요",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, bottom = 10.dp)
            )
            OutlinedTextField(
                singleLine = true,
                value = uiState.query,
                onValueChange = {
                    onQuery(it)
                },
                colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = colorResource(id = R.color.primary),
                    unfocusedIndicatorColor = colorResource(id = R.color.primary)
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
            if (uiState.errorMessage.isNotEmpty()) {
                1f
            } else {
                0f
            }.let {
                Text(
                    color = colorResource(id = R.color.red),
                    text = uiState.errorMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .alpha(it)
                )
            }
        }

        Text(
            textAlign = TextAlign.Center,
            text = "저장",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.primary))
                .padding(15.dp)
                .align(Alignment.BottomCenter)
                .clickable {
                    onSignUp()
                }
        )

        if (uiState.isLoading) {
            LoadingProgressBar(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpScreenPreview() {
    SignUpScreen(
        uiState = SignUpUiState(),
        sideEffect = SignUpSideEffect.None,
        onQuery = {
        },
        onProfileSelect = { _ ->

        },
        onSignUp = {

        }
    )
}