package com.dhkim.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dhkim.designsystem.MyStoryTheme
import com.dhkim.main.work.CheckOpenableTimeCapsuleWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var workManager: WorkManager

    private val viewModel: MainViewModel by viewModels()

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
    }

    companion object {
        const val CHECK_OPENABLE_TIME_CAPSULE_WORK_NAME = "checkOpenableTimeCapsuleWork"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val showGuide by viewModel.showGuide.collectAsStateWithLifecycle(initialValue = false)
            val currentPopup by viewModel.currentPopup.collectAsStateWithLifecycle()

            MyStoryTheme {
                MainScreen(
                    appState = rememberMyStoryAppState(),
                    showGuide = showGuide,
                    onCloseGuide = viewModel::closeGuideDialog,
                    onNeverShowGuideAgain = viewModel::neverShowGuideAgain,
                    currentPopup = currentPopup,
                    showPopup = viewModel::showPopup
                )
            }
        }

        requestPermission()
        checkOpenableTimeCapsule()
    }

    private fun checkOpenableTimeCapsule() {
        val checkOpenableTimeCapsuleWorker = PeriodicWorkRequestBuilder<CheckOpenableTimeCapsuleWorker>(
            24, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            CHECK_OPENABLE_TIME_CAPSULE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            checkOpenableTimeCapsuleWorker
        )
    }

    private fun requestPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        permissions.forEach { permission ->
            when {
                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> Unit

                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestPermissionLauncher.launch(permissions)
                }

                else -> {
                    requestPermissionLauncher.launch(permissions)
                }
            }
        }
    }
}