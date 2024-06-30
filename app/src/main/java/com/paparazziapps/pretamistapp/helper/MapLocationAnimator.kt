package com.paparazziapps.pretamistapp.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.paparazziapps.pretamistapp.R

class MapLocationAnimator(private val map: GoogleMap, private val context: Context) {

    private var pulseEffectColor: Int = ContextCompat.getColor(context, R.color.colorPrimary)
    private var pulseEffectColorElements: IntArray = intArrayOf(
        Color.red(pulseEffectColor),
        Color.green(pulseEffectColor),
        Color.blue(pulseEffectColor)
    )

    private var pulseEffectAnimator: ValueAnimator? = null
    private var pulseCircle: Circle? = null
    private var markerCoordinate: LatLng? = null

    init {
        initPulseEffect()
    }

    private fun initPulseEffect() {
        pulseEffectAnimator = ValueAnimator.ofFloat(0f, calculatePulseRadius(map.cameraPosition.zoom))
        pulseEffectAnimator?.startDelay = 3000
        pulseEffectAnimator?.duration = 1600
        pulseEffectAnimator?.interpolator = AccelerateDecelerateInterpolator()
    }

    private fun calculatePulseRadius(zoomLevel: Float): Float {
        return Math.pow(2.0, (7 - zoomLevel).toDouble()).toFloat() * 40
    }

    fun startPulseAnimation(coordinate: LatLng) {
        markerCoordinate = coordinate

        pulseCircle?.remove()

        pulseEffectAnimator?.let {
            it.removeAllUpdateListeners()
            it.removeAllListeners()
            it.end()
        }

        markerCoordinate?.let {
            pulseCircle = map.addCircle(
                CircleOptions()
                    .center(it)
                    .radius(0.0)
                    .strokeWidth(0f)
                    .fillColor(pulseEffectColor)
            )
        }



        pulseEffectAnimator?.addUpdateListener { valueAnimator ->
            pulseCircle?.let { circle ->
                val alpha = ((1 - valueAnimator.animatedFraction) * 128).toInt()
                circle.fillColor = Color.argb(
                    alpha,
                    pulseEffectColorElements[0],
                    pulseEffectColorElements[1],
                    pulseEffectColorElements[2]
                )
                circle.radius = (valueAnimator.animatedValue as Float).toDouble()
            }
        }

        pulseEffectAnimator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                pulseEffectAnimator?.startDelay = 2000
                pulseEffectAnimator?.start()
            }
        })

        pulseEffectAnimator?.start()
    }

    fun onCameraIdle() {
        val cameraPosition = map.cameraPosition
        pulseEffectAnimator?.setFloatValues(0f, calculatePulseRadius(cameraPosition.zoom))
    }
}
