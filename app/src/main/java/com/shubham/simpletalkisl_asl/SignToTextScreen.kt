package com.shubham.simpletalkisl_asl

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SignToTextScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val handLandmarkerHelper = remember {
        HandLandmarkerHelper(
            context = context,
            handLandmarkerHelperListener = object : HandLandmarkerHelper.LandmarkerListener {
                override fun onError(error: String) {
                    Log.e("SignToText", "AI Error : $error")

                }

                override fun onResult(resultBundle: HandLandmarkerHelper.ResultBundle) {
                    val firstResult = resultBundle.results.firstOrNull()
                    if (firstResult != null && firstResult.landmarks().isNotEmpty()) {
                        Log.d(
                            "SignToText",
                            "\uD83D\uDD90\uFE0F HAND DETECTED ! Landmark found :${firstResult.landmarks().size}"
                        )
                    }


                }
            }

        )

    }

    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    if (cameraPermissionState.status.isGranted) {
        CameraPreview(
            handLandmarkerHelper = handLandmarkerHelper,
            modifier = modifier.fillMaxSize()
        )
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                cameraPermissionState.launchPermissionRequest()
            }) {
                Text("Request Camera Permission")
            }
        }
    }
}