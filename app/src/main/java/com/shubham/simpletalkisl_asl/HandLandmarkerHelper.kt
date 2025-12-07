package com.shubham.simpletalkisl_asl

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmark
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class HandLandmarkerHelper(
    val context: Context,
    val handLandmarkerHelperListener: LandmarkerListener
) {

    private var handLandmarker: HandLandmarker? = null

    init {
        setupHandLandmarker()
    }

    private fun setupHandLandmarker() {
        try {
            //1.create the options for AI
            val baseOptionsBuilder = BaseOptions.builder().setModelAssetPath("hand_landmarker.task")

            val baseOptions = baseOptionsBuilder.build()

            val optionsBuilder = HandLandmarker.HandLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinHandDetectionConfidence(0.5f) //50% confidence needed
                .setMinTrackingConfidence(0.7f)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener(this::returnLiveStreamResult)
                .setErrorListener(this::returnLiveStreamError)

            val options = optionsBuilder.build()

            handLandmarker = HandLandmarker.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            handLandmarkerHelperListener.onError("Hand Landmarker failed to initialize . see logs")
        }
    }

    fun detectLiveStream(bitmap: Bitmap, isFrontCamera: Boolean,rotationDegrees :Int) {

        val imageProcessingOptions = com.google.mediapipe.tasks.vision.core.ImageProcessingOptions.builder()
            .setRotationDegrees(rotationDegrees)
            .build()

        val mpImage = BitmapImageBuilder(bitmap).build()

        val frameTime = SystemClock.uptimeMillis()
        handLandmarker?.detectAsync(mpImage, frameTime)

    }

    private fun returnLiveStreamResult(result: HandLandmarkerResult, input: MPImage) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        handLandmarkerHelperListener.onResult(
            ResultBundle(
                results = listOf(result),
                inferenceTime = inferenceTime,
                inputImageHeight = input.height,
                inputImageWidth = input.width
            )
        )
    }

    private fun returnLiveStreamError(error: RuntimeException) {
        handLandmarkerHelperListener.onError(error.message ?: "Unknown error")
    }

    data class ResultBundle(
        val results: List<HandLandmarkerResult>,
        val inferenceTime: Long,
        val inputImageHeight : Int,
        val inputImageWidth : Int
    )

    interface LandmarkerListener {
        fun onError(error: String)
        fun onResult(resultBundle: ResultBundle)
    }

}