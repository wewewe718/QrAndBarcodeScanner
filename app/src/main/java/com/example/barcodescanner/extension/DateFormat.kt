package com.example.barcodescanner.extension

import com.example.barcodescanner.usecase.Logger
import java.text.DateFormat
import java.util.*

fun DateFormat.parseOrNull(date: String?): Date? {
    return try {
        parse(date.orEmpty())
    } catch (ex: Exception) {
        Logger.log(ex)
        null
    }
}

fun List<DateFormat>.parseOrNull(date: String?): Date? {
    forEach { dateParser ->
        val parsedDate = dateParser.parseOrNull(date)
        if (parsedDate != null) {
            return parsedDate
        }
    }
    return null
}

fun DateFormat.formatOrNull(time: Long?): String? {
    return try {
        format(Date(time!!))
    } catch (ex: Exception) {
        Logger.log(ex)
        null
    }
}