package com.example.sesecoffee.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class Format {
    fun timestampToFormattedString(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy | HH:mm", Locale.getDefault())
        return sdf.format(timestamp.toDate())
    }
}