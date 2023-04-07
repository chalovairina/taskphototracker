package com.chalova.irina.todoapp.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun formatDate(date: Instant): String {
    val format = DateTimeFormatter.ofPattern("EEE, MMM dd")
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())

    return format.format(date)
}