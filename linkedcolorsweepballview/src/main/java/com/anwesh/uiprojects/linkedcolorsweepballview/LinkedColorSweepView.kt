package com.anwesh.uiprojects.linkedcolorsweepballview

/**
 * Created by anweshmishra on 23/06/18.
 */

import android.view.View
import android.content.Context
import android.view.MotionEvent
import android.graphics.*

val CSV_NODES : Int = 5

class LinkedColorSweepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class CSVState (var j : Int = 0, var dir : Float = 0f, var prevScale : Float = 0f) {

        val scales : Array<Float> = arrayOf(0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += 0.1f * dir
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                dir = 0f
                prevScale = scales[j]
                stopcb(prevScale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class CSVAnimator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(60)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }



    data class CSVNode(var i : Int, private val state : CSVState = CSVState()) {

        private var next : CSVNode? = null

        private var prev : CSVNode? = null

        init {

        }

        fun addNeighbor() {
            if (i < CSV_NODES - 1) {
                next = CSVNode(i + 1)
                next?.prev = this
            }
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb  : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun draw(canvas : Canvas, paint : Paint) {
            prev?.draw(canvas, paint)
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val gap : Float = (0.8F * Math.min(w, h)) / CSV_NODES
            canvas.save()
            canvas.translate(-gap/2 + i * gap + gap * state.scales[0], h + gap/2 + i * gap)
            paint.color = Color.YELLOW
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = Math.min(w, h) / 60
            val index1 : Int = i % 2
            val scale : Float = (1 - state.scales[1]) * index1 + (state.scales[1]) * (1 - index1)
            canvas.drawCircle(0f, 0f, gap/2, paint)
            paint.style = Paint.Style.FILL
            canvas.drawArc(RectF(-gap/2, -gap/2, gap/2, gap/2), 0f, 360f * scale, true, paint)
            canvas.restore()
        }

        fun getNext(dir : Int, cb : () -> Unit) : CSVNode {
            var curr : CSVNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LinkedColorSweep (var i : Int) {

        private var curr : CSVNode = CSVNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }
}