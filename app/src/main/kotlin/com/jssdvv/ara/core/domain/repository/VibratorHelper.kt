package com.jssdvv.ara.core.domain.repository

interface VibratorHelper {
    fun vibrate(duration: Long)
    fun shortPress()
    fun longPress()
    fun doublePress()
}