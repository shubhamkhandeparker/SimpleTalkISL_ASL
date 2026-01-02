package com.shubham.simpletalkisl_asl

import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

class SignLanguageRecognizer {

    private var lastGesture: String = ""
    private var stableCount: Int = 0
    private val STABLE_FRAMES = 3

    fun recognizeHandGesture(result: HandLandmarkerResult): String {

        val landmarks = result.landmarks().firstOrNull() ?: return ""

        // --- Palm landmarks ---
        val wrist = landmarks[0]

        val thumbMcp = landmarks[2]
        val thumbIp = landmarks[3]
        val thumbTip = landmarks[4]

        val indexMcp = landmarks[5]
        val indexPip = landmarks[6]
        val indexTip = landmarks[8]

        val middleMcp = landmarks[9]
        val middlePip = landmarks[10]
        val middleTip = landmarks[12]

        val ringMcp = landmarks[13]
        val ringPip = landmarks[14]
        val ringTip = landmarks[16]

        val pinkyMcp = landmarks[17]
        val pinkyPip = landmarks[18]
        val pinkyTip = landmarks[20]

        // --- Finger states ---
        val isIndexOpen = GestureRules.isFingerOpen(indexMcp, indexPip, indexTip)
        val isMiddleOpen = GestureRules.isFingerOpen(middleMcp, middlePip, middleTip)
        val isRingOpen = GestureRules.isFingerOpen(ringMcp, ringPip, ringTip)
        val isPinkyOpen = GestureRules.isFingerOpen(pinkyMcp, pinkyPip, pinkyTip)

        // --- Thumb state ---
        val thumbUp = GestureRules.isThumbUp(
            thumbTip = thumbTip,
            indexMcp = indexMcp,
            wrist = wrist
        )

        // --- Gesture detection (priority order matters) ---
        val detectedGesture = when {

            GestureRules.isPalmOpen(
                indexOpen = isIndexOpen,
                middleOpen = isMiddleOpen,
                ringOpen = isRingOpen,
                pinkyOpen = isPinkyOpen,
                thumbTip = thumbTip,
                indexMcp = indexMcp,
                wrist = wrist
            ) -> "Palm / Stop"

            GestureRules.isOkGesture(
                thumbTip = thumbTip,
                indexTip = indexTip,
                middleTip = middleTip,
                ringTip = ringTip,
                pinkyTip = pinkyTip,
                wrist = wrist
            ) -> "OK"

            thumbUp && GestureRules.isFist(
                indexOpen = isIndexOpen,
                middleOpen = isMiddleOpen,
                ringOpen = isRingOpen,
                pinkyOpen = isPinkyOpen
            ) -> "Thumbs Up"

            GestureRules.isFist(
                indexOpen = isIndexOpen,
                middleOpen = isMiddleOpen,
                ringOpen = isRingOpen,
                pinkyOpen = isPinkyOpen
            ) -> "Fist"

            GestureRules.isPointing(
                indexOpen = isIndexOpen,
                middleOpen = isMiddleOpen,
                ringOpen = isRingOpen,
                pinkyOpen = isPinkyOpen
            ) -> "Point"

            isIndexOpen && isMiddleOpen && !isRingOpen && !isPinkyOpen ->
                "Victory / V"

            isIndexOpen && isMiddleOpen && isRingOpen && isPinkyOpen ->
                "Hello / Open Hand"

            else -> ""
        }

        // --- Stabilization ---
        if (detectedGesture == lastGesture && detectedGesture.isNotEmpty()) {
            stableCount++
        } else {
            stableCount = 0
            lastGesture = detectedGesture
        }

        return if (stableCount >= STABLE_FRAMES) detectedGesture else ""
    }
}
