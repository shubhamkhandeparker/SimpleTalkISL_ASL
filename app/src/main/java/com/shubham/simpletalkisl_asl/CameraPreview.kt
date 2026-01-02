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
import androidx.camera.core.AspectRatio
import androidx.camera.view.CameraController
import android.graphics.Bitmap
import android.graphics.Matrix


private fun Bitmap.rotate(degrees: Int): Bitmap {
    if (degrees == 0) return this
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

private fun Bitmap.flipHorizontal(): Bitmap {
    val matrix = Matrix().apply { preScale(-1f, 1f) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

@Composable
fun CameraPreview(
    handLandmarkerHelper: HandLandmarkerHelper,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            setImageAnalysisBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)

            imageAnalysisTargetSize = CameraController.OutputSize(android.util.Size(640, 480))
            previewTargetSize = CameraController.OutputSize(AspectRatio.RATIO_4_3)

        }
    }

    val isFrontCamera = remember {
        cameraController.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA
    }

    LaunchedEffect(key1 = cameraController) {
        val backgroundExecutor = Executors.newSingleThreadExecutor()

        cameraController.setImageAnalysisAnalyzer(
            backgroundExecutor,
            { imageProxy ->

                processImageProxy(handLandmarkerHelper, imageProxy, isFrontCamera)

            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    controller = cameraController
                    scaleType = PreviewView.ScaleType.FILL_CENTER

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
    helper: HandLandmarkerHelper,
    imageProxy: ImageProxy,
    isFrontCamera: Boolean
) {
    try {

        val bitmap = imageProxy.toBitmap()

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees


        var correctedBitmap = if (rotationDegrees != 0) bitmap.rotate(rotationDegrees) else bitmap

        if (isFrontCamera) {
            correctedBitmap = correctedBitmap.flipHorizontal()
        }



        helper.detectLiveStream(correctedBitmap, isFrontCamera, 0)
    } catch (t: Throwable) {
        t.printStackTrace()
    } finally {
        imageProxy.close()
    }
}


