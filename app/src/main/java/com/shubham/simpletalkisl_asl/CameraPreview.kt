package com.shubham.simpletalkisl_asl

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContentProviderCompat
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    handLandmarkerHelper: HandLandmarkerHelper,
    modifier:Modifier= Modifier
){
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraController= remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            setImageAnalysisBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)

        }
    }

    LaunchedEffect(key1 = cameraController) {
        val backgroundExecutor = Executors.newSingleThreadExecutor()

        cameraController.setImageAnalysisAnalyzer(
            backgroundExecutor,
            {imageProxy ->

                processImageProxy(handLandmarkerHelper,imageProxy)

            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()){
        AndroidView(
            factory={ctx ->
                PreviewView(ctx).apply {
                    controller = cameraController
                    scaleType = PreviewView.ScaleType.FILL_START
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = {
                cameraController.bindToLifecycle(lifecycleOwner)
            }
        )
    }
}

private fun processImageProxy(
    helper : HandLandmarkerHelper,
    imageProxy: ImageProxy
){
    val bitmap = imageProxy.toBitmap()
    val isFrontCamera = false

    helper.detectLiveStream(bitmap,isFrontCamera)
    imageProxy.close()
}


