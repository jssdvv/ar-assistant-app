package com.jssdvv.ara.core.domain.utility

import android.annotation.SuppressLint
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormats {
    @SuppressLint("ConstantLocale")
    private val isSpanish = Locale.getDefault().language == "es"

    @SuppressLint("ConstantLocale")
    val short: DateTimeFormatter = DateTimeFormatter.ofPattern(
        if (isSpanish) "dd/MM/yyyy" else "yyyy/MM/dd"
    ).withLocale(Locale.getDefault())

    @SuppressLint("ConstantLocale")
    val medium: DateTimeFormatter = DateTimeFormatter.ofPattern(
        if (isSpanish) "d 'de' MMM 'de' yyyy" else "MMM d, yyyy"
    ).withLocale(Locale.getDefault())
}

fun LocalDate.formatShort(): String = format(DateFormats.short)
fun LocalDate.formatMedium(): String = format(DateFormats.medium)

fun Instant.formatShort(): String = atZone(ZoneId.systemDefault())
    .format(DateFormats.short)

fun Instant.formatMedium(): String = atZone(ZoneId.systemDefault())
    .format(DateFormats.medium)