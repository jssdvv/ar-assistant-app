package com.jssdvv.ara.core.data.repository

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.jssdvv.ara.core.domain.repository.VibratorHelper

class VibratorHelperImpl(
    private val context: Context,
    val amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE,
) : VibratorHelper {

    companion object {
        const val SUPPRESS_DEPRECATION_NAME = "DEPRECATION"
        const val SHORT_PRESS_DURATION = 100L
        const val LONG_PRESS_DURATION = 200L
        const val DOUBLE_PRESS_DURATION = 200L
    }

    private val vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress(SUPPRESS_DEPRECATION_NAME)
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    override fun vibrate(
        duration: Long,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(duration, amplitude))
        } else {
            @Suppress(SUPPRESS_DEPRECATION_NAME)
            vibrator?.vibrate(duration)
        }
    }

    override fun shortPress() = vibrate(SHORT_PRESS_DURATION)

    override fun longPress() = vibrate(LONG_PRESS_DURATION)

    override fun doublePress() = vibrate(DOUBLE_PRESS_DURATION)
}