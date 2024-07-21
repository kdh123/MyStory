@file:OptIn(ExperimentalPermissionsApi::class)

package com.dhkim.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

typealias savedUrl = String

@SuppressLint("MissingPermission")
@Composable
fun CameraScreen(
    uiState: CameraUiState,
    sideEffect: CameraSideEffect,
    folderName: String = "",
    initAddress: (String, String) -> Unit,
    onSetTimeStampMode: () -> Unit,
    onSavingPhoto: () -> Unit,
    onSavedPhoto: (String) -> Unit,
    onTakePhoto: (Bitmap, ImageBitmap) -> Unit,
    onNext: ((savedUrl) -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
        }
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
        } else {
            // Handle permission denial
        }
    }
    var currentLocation by remember {
        mutableStateOf(Pair(37.572389, 126.9769117))
    }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    val requestLocationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    currentLocation = Pair(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                    initAddress("${currentLocation.first}", "${currentLocation.second}")
                }
        } else {
            Toast.makeText(context, "카메라 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            onBack?.invoke()
        }
    }

    val graphicsLayer = rememberGraphicsLayer()
    val resultGraphicsLayer = rememberGraphicsLayer()

    LaunchedEffect(permissionState) {
        permissionState.permissions.forEach {
            when (it.permission) {
                Manifest.permission.CAMERA -> {
                    if (!it.status.isGranted) {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }

                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    if (it.status.isGranted) {
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location: Location? ->
                                currentLocation = Pair(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                                initAddress("${currentLocation.first}", "${currentLocation.second}")
                            }
                    } else {
                        requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            }
        }
    }

    LaunchedEffect(sideEffect) {
        when (sideEffect) {
            CameraSideEffect.None -> {}

            is CameraSideEffect.Completed -> {
                onNext?.invoke(uiState.savedUrl)
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .height(0.dp)
                    .weight(1f)
            ) {
                if (uiState.bitmap != null && uiState.backgroundBitmap != null) {
                    Box(modifier = Modifier
                        .drawWithContent {
                            resultGraphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(resultGraphicsLayer)
                        }
                    ) {
                        Image(
                            bitmap = uiState.bitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                        Image(
                            bitmap = uiState.backgroundBitmap,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                        )

                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .width(64.dp)
                                    .align(Alignment.Center),
                                color = Color.White,
                                trackColor = colorResource(id = R.color.primary)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .background(color = Color.Green)
                            .drawWithContent {
                                graphicsLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawLayer(graphicsLayer)
                            }
                    ) {
                        CameraPreview(
                            controller = controller,
                            modifier = Modifier
                                .fillMaxSize()
                        )

                        if (uiState.isTimeStampMode) {
                            TimeStampBackground(
                                timeStamp = uiState.timeStamp,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            )
                        }
                    }

                    Icon(
                        tint = Color.Unspecified,
                        painter = painterResource(id = R.drawable.ic_flip_camera_android_white),
                        contentDescription = "Switch camera",
                        modifier = Modifier
                            .padding(10.dp)
                            .width(36.dp)
                            .height(36.dp)
                            .clickable {
                                controller.cameraSelector =
                                    if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                        CameraSelector.DEFAULT_FRONT_CAMERA
                                    } else CameraSelector.DEFAULT_BACK_CAMERA
                            }
                    )

                    if (uiState.isTimeStampMode) {
                        SettingButton(
                            resId = R.drawable.time_stamp_mode_white,
                            text = "타임스탬프",
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterStart),
                            onClick = onSetTimeStampMode
                        )
                    } else {
                        SettingButton(
                            resId = R.drawable.none_mode_white,
                            text = "일반",
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterStart),
                            onClick = onSetTimeStampMode
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(color = Color.White)
            ) {
                if (uiState.backgroundBitmap != null && uiState.bitmap != null) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    ) {
                        Button(
                            colors = ButtonColors(
                                containerColor = colorResource(id = R.color.primary),
                                contentColor = colorResource(id = R.color.primary),
                                disabledContentColor = colorResource(id = R.color.primary),
                                disabledContainerColor = colorResource(id = R.color.primary),
                            ),
                            onClick = {
                                onBack?.invoke()
                            }
                        ) {
                            Text(
                                text = "이전",
                                color = Color.White,
                                modifier = Modifier
                                    .padding(10.dp)
                            )
                        }

                        Button(
                            colors = ButtonColors(
                                containerColor = colorResource(id = R.color.primary),
                                contentColor = colorResource(id = R.color.primary),
                                disabledContentColor = colorResource(id = R.color.primary),
                                disabledContainerColor = colorResource(id = R.color.primary),
                            ),
                            onClick = {
                                scope.launch {
                                    savePhoto(
                                        bitmap = resultGraphicsLayer.toImageBitmap(),
                                        folderName = folderName.ifEmpty { "DHPicture" },
                                        context = context,
                                        onSavingPhoto = onSavingPhoto,
                                        onSavedPhoto = onSavedPhoto
                                    )
                                }
                            }) {
                            Text(
                                text = "다음",
                                color = Color.White,
                                modifier = Modifier
                                    .padding(10.dp)
                            )
                        }
                    }
                } else {
                    CameraButton(
                        modifier = Modifier
                            .align(Alignment.Center)
                    ) {
                        scope.launch {
                            takePhoto(
                                context = context,
                                controller = controller,
                                backgroundImageBitmap = graphicsLayer.toImageBitmap(),
                                onPhotoTaken = onTakePhoto,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingButton(
    resId: Int,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clickable {
                onClick()
            }
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier
                .width(36.dp)
                .height(36.dp)
        )
        /*Text(
            text = text,
            color = Color.White
        )*/
    }
}

@Composable
private fun TimeStampBackground(
    timeStamp: TimeStamp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = colorResource(id = R.color.black_40))
            .padding(10.dp)
    ) {
        Text(
            text = timeStamp.address,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 10.dp)

        )

        Text(
            text = timeStamp.date,
            color = Color.White,
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimeStampBackgroundPreview() {
    TimeStampBackground(
        timeStamp = TimeStamp(date = "2024-07-28", address = "대한민국 서울")
    )
}


@Composable
fun CameraButton(modifier: Modifier = Modifier, onPhotoTaken: () -> Unit) {
    Box(
        modifier = modifier
            .width(76.dp)
            .aspectRatio(1f)
            .border(color = Color.Black, width = 3.dp, shape = CircleShape)
            .padding(10.dp)
            .clickable {
                onPhotoTaken()
            }
    )
}

@Preview(showBackground = true)
@Composable
private fun CameraButtonPreview() {
    CameraButton(modifier = Modifier) {

    }
}

private suspend fun savePhoto(
    folderName: String,
    context: Context,
    bitmap: ImageBitmap,
    onSavingPhoto: () -> Unit,
    onSavedPhoto: (String) -> Unit
) {
    onSavingPhoto()
    withContext(NonCancellable) {
        val savedUrl = saveBitmap(context = context, bitmap = bitmap, folderName = folderName)
        onSavedPhoto(savedUrl)
    }
}

private suspend fun saveBitmap(context: Context, bitmap: ImageBitmap, folderName: String): savedUrl {
    val imageName = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        .format(System.currentTimeMillis())
    val timestamp = System.currentTimeMillis()
    var savedUrl: Uri? = null

    withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$folderName")
            values.put(MediaStore.Images.Media.IS_PENDING, true)

            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                try {
                    val outputStream = context.contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.close()
                    }
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    context.contentResolver.update(uri, values, null, null)
                } catch (_: Exception) {

                }
            }
            savedUrl = uri
        }
    }

    return savedUrl?.toString() ?: ""
}

private fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    backgroundImageBitmap: ImageBitmap,
    onPhotoTaken: (Bitmap, ImageBitmap) -> Unit,
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )
                onPhotoTaken(rotatedBitmap, backgroundImageBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Couldn't take photo: ", exception)
            }
        }
    )
}