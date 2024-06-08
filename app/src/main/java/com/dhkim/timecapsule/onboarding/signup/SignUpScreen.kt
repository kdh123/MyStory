@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.dhkim.timecapsule.onboarding.signup

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dhkim.timecapsule.main.MainActivity
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.common.composable.LoadingProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is SignUpSideEffect.Message -> {
                    Toast.makeText(context, sideEffect.message, Toast.LENGTH_LONG).show()
                }

                is SignUpSideEffect.Completed -> {
                    Intent(context, MainActivity::class.java).run {
                        context.startActivity(this)
                        (context as? Activity)?.finish()
                    }
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
                .align(Alignment.Center)
                .padding(20.dp)
        ) {
            Text(
                fontSize = 18.sp,
                text = "아이디를 입력하세요",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )
            OutlinedTextField(
                singleLine = true,
                value = uiState.query,
                onValueChange = {
                    viewModel.onQuery(it)
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
                    viewModel.signUp()
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
    /*SignUpScreen {

    }*/
}