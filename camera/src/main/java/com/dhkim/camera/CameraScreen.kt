@file:OptIn(ExperimentalPermissionsApi::class)

package com.dhkim.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

typealias savedUrl = String

@Composable
fun CameraScreen(
    folderName: String = "",
    onNext : ((savedUrl) -> Unit)? = null,
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
    val viewModel = viewModel<CameraViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
        } else {
            // Handle permission denial
        }
    }

    LaunchedEffect(cameraPermissionState) {
        if (!cameraPermissionState.status.isGranted && cameraPermissionState.status.shouldShowRationale) {
            // Show rationale if needed
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when(sideEffect) {
                CameraSideEffect.None -> {}

                is CameraSideEffect.Completed -> {
                    onNext?.invoke(uiState.savedUrl)
                }
            }
        }
    }

    Scaffold(

    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CameraPreview(
                controller = controller,
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

            CameraButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                scope.launch {
                    takePhoto(
                        folderName = folderName.ifEmpty { "DHPicture" },
                        context = context,
                        controller = controller,
                        onPhotoTaken = viewModel::onTakePhoto,
                        onSavedPhoto = viewModel::onSavedPhoto
                    )
                }
            }
        }
    }
}

@Composable
fun CameraButton(modifier: Modifier, onPhotoTaken: () -> Unit) {
    Box(
        modifier = modifier
            .padding(bottom = 20.dp)
            .width(76.dp)
            .height(76.dp)
            .clip(CircleShape)
            .background(color = Color.White)
            .clickable {
                onPhotoTaken()
            }
    ) {
        Box(
            modifier = Modifier
                .padding(3.dp)
                .width(76.dp)
                .height(76.dp)
                .clip(CircleShape)
                .background(color = Color.Black)
        ) {
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .width(76.dp)
                    .height(76.dp)
                    .clip(CircleShape)
                    .background(color = Color.White)
            )
        }
    }
}

@Preview
@Composable
private fun CameraButtonPreview() {
    CameraButton(modifier = Modifier) {
        
    }
}


private suspend fun takePhoto(
    folderName: String,
    context: Context,
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit,
    onSavedPhoto: (String) -> Unit
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
                CoroutineScope(Dispatchers.IO).launch {
                    onPhotoTaken(rotatedBitmap)
                    val savedUrl = saveBitmap(context = context, bitmap = rotatedBitmap, folderName = folderName)
                    onSavedPhoto(savedUrl)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Couldn't take photo: ", exception)
            }
        }
    )
}

private suspend fun saveBitmap(context: Context, bitmap: Bitmap, folderName: String): savedUrl {
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
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
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