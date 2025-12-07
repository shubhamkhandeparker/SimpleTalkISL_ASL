package com.shubham.simpletalkisl_asl

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmark
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlin.math.max
import kotlin.math.min



class HandOverlayView (context :Context?, attrs: AttributeSet?): View(context,attrs) {

    private var results : HandLandmarkerResult? = null
    private var linePaint= Paint()
    private var pointPaint = Paint()

    private var scaleFactor : Float = 1f
    private var imageWidth : Int = 1
    private var imageHeight :Int = 1

    init{
        initPaints()
    }

    fun clear(){
        results = null
        linePaint.reset()
        pointPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints(){
        linePaint.color = Color.CYAN
        linePaint.strokeWidth = 8f
        linePaint.style = Paint.Style.STROKE

        pointPaint.color= Color.RED
        pointPaint.strokeWidth = 8f
        pointPaint.style= Paint.Style.FILL

    }

    override fun draw(canvas: Canvas){
        super.draw(canvas)
        results?.let{ handLandmarkerResult ->

            canvas.save()
            canvas.scale(-1f,1f,width/2f,height/2f)

            for (landmark in handLandmarkerResult.landmarks()){
                for(normalizedLandmark in landmark){

                        val x =normalizedLandmark.x()* imageWidth * scaleFactor
                        val y = normalizedLandmark.y() * imageHeight * scaleFactor

                    val offsetX = (width - imageWidth * scaleFactor) /2
                    val offsetY = (height - imageHeight * scaleFactor) / 2

                    canvas.drawCircle(x + offsetX, y + offsetY, 8f, pointPaint)

                }

                HandLandmarker.HAND_CONNECTIONS.forEach {
                    val start = landmark[it.start()]
                    val end = landmark[it.end()]


                      val startX =  ( start.x() * imageWidth * scaleFactor) + (width - imageWidth * scaleFactor) /2
                      val startY =    (start.y() * imageHeight * scaleFactor) + (height - imageHeight * scaleFactor)/2

                        val endX = (end.x() * imageWidth * scaleFactor) + (width - imageWidth * scaleFactor) / 2
                        val endY= (end.y() * imageHeight * scaleFactor) + (height - imageHeight * scaleFactor)/2

                    canvas.drawLine(startX,startY,endX,endY,linePaint)

                }
            }
            canvas.restore()
        }
    }

    fun setResults(
        handLandmarkerResult: HandLandmarkerResult,
        imageHeight : Int,
        imageWidth :Int,
        runningMode : com.google.mediapipe.tasks.vision.core.RunningMode = com.google.mediapipe.tasks.vision.core.RunningMode.LIVE_STREAM
    ){
        results= handLandmarkerResult
        this.imageHeight= imageHeight
        this.imageWidth = imageWidth
        scaleFactor= max(width * 1f/imageWidth,height * 1f/imageHeight)
        invalidate()

    }

}