package com.chari.ic.todoapp.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun formatDate(date: Instant): String {
    val format = DateTimeFormatter.ofPattern("EEE, MMM dd")
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())
//   val format = SimpleDateFormat.getDateInstance() as SimpleDateFormat
//    format.applyPattern(
//      "EEE, MMM dd"
//    )
//    val localDate = LocalDateTime.(date)

    return format.format(date)
}