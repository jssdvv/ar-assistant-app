package com.jssdvv.ara.core.domain.utility

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.jssdvv.ara.R
import com.jssdvv.ara.schedule.domain.type.RecurrenceUnit
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.platform.LocalResources

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

fun LocalDate.nextOccurrence(quantity: Int, unit: RecurrenceUnit): LocalDate? {
    return when (unit) {
        RecurrenceUnit.ONCE -> null
        RecurrenceUnit.DAYS -> this.plusDays(quantity.toLong())
        RecurrenceUnit.WEEKS -> this.plusWeeks(quantity.toLong())
        RecurrenceUnit.MONTHS -> this.plusMonths(quantity.toLong())
        RecurrenceUnit.YEARS -> this.plusYears(quantity.toLong())
    }
}

@Composable
fun recurrenceLabel(quantity: Int, unit: RecurrenceUnit): String {
    val resources = LocalResources.current
    return when (unit) {
        RecurrenceUnit.ONCE -> ""
        RecurrenceUnit.DAYS -> if (quantity == 1)
            stringResource(R.string.recurrence_every_one_day)
        else
            resources.getQuantityString(R.plurals.recurrence_every_days, quantity, quantity)

        RecurrenceUnit.WEEKS -> if (quantity == 1)
            stringResource(R.string.recurrence_every_one_week)
        else
            resources.getQuantityString(R.plurals.recurrence_every_weeks, quantity, quantity)

        RecurrenceUnit.MONTHS -> if (quantity == 1)
            stringResource(R.string.recurrence_every_one_month)
        else
            resources.getQuantityString(R.plurals.recurrence_every_months, quantity, quantity)

        RecurrenceUnit.YEARS -> if (quantity == 1)
            stringResource(R.string.recurrence_every_one_year)
        else
            resources.getQuantityString(R.plurals.recurrence_every_years, quantity, quantity)
    }
}