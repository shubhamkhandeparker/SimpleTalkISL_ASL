package com.shubham.simpletalkisl_asl

import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class SignLanguageRecognizer {

    fun recognizeHandGesture(result:HandLandmarkerResult):String{

        val landmarks = result.landmarks().firstOrNull()?: return ""

        val thumbTip = landmarks[4]
        val indexTip = landmarks[8]
        val middleTip = landmarks[12]
        val ringTip = landmarks[16]
        val pinkyTip = landmarks[20]

        val indexPip = landmarks[6]
        val middlePip = landmarks[10]
        val ringPip = landmarks[14]
        val pinkyPip = landmarks[18]

        val isThumbUp = thumbTip.y() <indexTip.y() && thumbTip.y() < middleTip.y()

        val areFingersCurled = indexTip.y() > indexPip.y() &&
                middleTip.y() > middlePip.y() &&
                ringTip.y() > ringPip.y() &&
                pinkyTip.y() > pinkyPip.y()


        if(isThumbUp && areFingersCurled){
            return "Thumbs Up"
        }

        val areAllFingersOpen = indexTip.y() < indexPip.y() &&
                middleTip.y() < middlePip.y() &&
                ringTip.y() < ringPip.y() &&
                pinkyTip.y() < pinkyPip.y()

        if(areAllFingersOpen){
            return  "Hello / Open Hand"
        }

        val isIndexOpen = indexTip.y() < indexPip.y()
        val isMiddleOpen = middleTip.y() <middlePip.y()
        val areOtherCurled = ringPip.y() > ringPip.y() && pinkyTip.y() > pinkyPip.y()

        if(isIndexOpen && isMiddleOpen && areOtherCurled){
            return "Victory /v"
        }

        return "" // No gesture recognized 
    }
}