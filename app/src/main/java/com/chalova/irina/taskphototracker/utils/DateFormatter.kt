package com.chalova.irina.taskphototracker.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun formatDate(date: Instant): String {
    val format = DateTimeFormatter.ofPattern("EEE, dd MMM")
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())

    return format.format(date)
}