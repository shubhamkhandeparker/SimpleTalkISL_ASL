package com.shubham.simpletalkisl_asl

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.sqrt



object GestureRules {

    fun isFingerOpen(
        mcp: NormalizedLandmark,
        pip: NormalizedLandmark,
        tip: NormalizedLandmark
    ):Boolean {

        val v1x = pip.x() - mcp.x()
        val v1y = pip.y() - mcp.y()
        val v2x = tip.x() - pip.x()
        val v2y = tip.y() - pip.y()

           val dot = v1x * v2x + v1y  * v2y
        val mag1 =sqrt(v1x * v1x + v1y * v1y)
        val mag2 = sqrt(v2x * v2x + v2y * v2y)

        if(mag1 == 0f || mag2 == 0f) return false

        val cosAngle = dot /(mag1 * mag2)
        val tipDist = distance(tip, mcp)
        val pipDist = distance(pip, mcp)

        return  cosAngle  >  0.82f && tipDist / pipDist  >  1.25f
    }

    fun isThumbUp(
        thumbTip : NormalizedLandmark,
        indexMcp : NormalizedLandmark,
        wrist : NormalizedLandmark
    ) : Boolean {

        val thumbDist = distance(thumbTip, wrist)
        val indexDist = distance ( indexMcp,wrist)
        return  thumbDist > indexDist * 1.15f

    }


    fun isFist(
        indexOpen : Boolean ,
        middleOpen : Boolean,
        ringOpen: Boolean,
        pinkyOpen : Boolean
    ): Boolean{
        return  !indexOpen && !middleOpen && !ringOpen && !pinkyOpen
    }

    fun isOkGesture(
        thumbTip: NormalizedLandmark,
        indexTip: NormalizedLandmark,
        middleTip: NormalizedLandmark,
        ringTip: NormalizedLandmark,
        pinkyTip: NormalizedLandmark,
        wrist: NormalizedLandmark
    ): Boolean

    {
        val dist = distance ( thumbTip, indexTip)
        return dist <0.03f
    }

    fun isPointing(
        indexOpen: Boolean,
        middleOpen: Boolean,
        ringOpen : Boolean,
        pinkyOpen: Boolean
    ): Boolean {
        return indexOpen && !middleOpen && !ringOpen && !pinkyOpen
    }

    fun isPalmOpen(
        indexOpen: Boolean,
        middleOpen: Boolean,
        ringOpen: Boolean,
        pinkyOpen: Boolean,
        thumbTip: NormalizedLandmark,
        wrist: NormalizedLandmark,
        indexMcp: NormalizedLandmark
    ):Boolean{
        val thumbDist = distance(thumbTip,wrist)
        val indexDist = distance(indexMcp,wrist)

        val thumbExtended = thumbDist > indexDist *  1.15f

        return  indexOpen &&
                middleOpen &&
                ringOpen &&
                pinkyOpen &&
                thumbExtended
    }
    private fun distance (
        a : NormalizedLandmark,
        b: NormalizedLandmark
    ):Float {
        val dx = a.x() -b.x()
        val dy = a.y() - b.y()
        return  sqrt(dx * dx + dy * dy)
    }

}