package com.shubham.simpletalkisl_asl

import android.Manifest
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SignToTextScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val recognizedText = remember { mutableStateOf("") }

    val recognizer= remember { SignLanguageRecognizer() }

    var consecutiveFrames = 0
    var lastGesture =""


    val handOverlayView = remember {
        HandOverlayView(context,null)
    }


    val handLandmarkerHelper = remember {
        HandLandmarkerHelper(
            context = context,
            handLandmarkerHelperListener = object : HandLandmarkerHelper.LandmarkerListener {
                override fun onError(error: String) {
                    Log.e("SignToText", "AI Error : $error")

                }

                override fun onResult(resultBundle: HandLandmarkerHelper.ResultBundle) {
                    val firstResult = resultBundle.results.firstOrNull()
                    if (firstResult != null ) {

                        val gesture = recognizer.recognizeHandGesture(firstResult)

                        handOverlayView.post {
                            if(gesture==lastGesture){
                                consecutiveFrames++
                            }
                            else{
                                consecutiveFrames=0
                                lastGesture=gesture
                            }
                            if(consecutiveFrames>5) {

                                if (gesture.isNotEmpty()) {
                                    recognizedText.value = gesture
                                }
                            }
                            handOverlayView.setResults(firstResult,
                                imageHeight = resultBundle.inputImageHeight,
                                imageWidth = resultBundle.inputImageWidth,
                                runningMode = com.google.mediapipe.tasks.vision.core.RunningMode.LIVE_STREAM)
                        }
                    }


                }
            }

        )

    }

    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    if (cameraPermissionState.status.isGranted) {
        Box(modifier = Modifier.fillMaxSize()){
        CameraPreview(
            handLandmarkerHelper = handLandmarkerHelper,
            modifier = modifier.fillMaxSize()
        )
        AndroidView(
            factory = { handOverlayView },
            modifier = Modifier.fillMaxSize()
        )
            if(recognizedText.value.isNotEmpty()){
                Text(
                    text=recognizedText.value,
                    style= MaterialTheme.typography.displayMedium,
                    color=Color.White,
                    modifier=Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 48.dp)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(16.dp)
                )
            }
    }
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