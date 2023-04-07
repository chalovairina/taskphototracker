package com.chalova.irina.todoapp.tasks.data.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object DateTimeUtil {

    const val DATE_FORMAT = "yyyy-MM-dd"

    fun now(): Instant {
        return Instant.now()
    }

    fun toLocalDate(instant: Instant): LocalDate {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate()
    }

    fun toLocalDate(date: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)

        return LocalDate.parse(date, formatter)
    }

    fun toInstant(localDate: LocalDate): Instant {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
    }

    fun formatDate(date: Instant): String {
        val format = DateTimeFormatter.ofPattern(DATE_FORMAT)
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault())

        return format.format(date)
    }

    fun getLocalDate(year: Int, month: Int, dayOfMonth: Int): LocalDate {
        return LocalDate.of(year, month, dayOfMonth)
    }
}