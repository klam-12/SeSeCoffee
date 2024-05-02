package com.example.sesecoffee.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.text.NumberFormat
class Format {
    fun timestampToFormattedString(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy | HH:mm", Locale.US)
        return sdf.format(timestamp.toDate())
    }

    fun formatNumber(number: Int): String {
        val numberString = number.toString().reversed()

        val parts = mutableListOf<Char>()
        for (i in 0 until numberString.length) {
            parts.add(0, numberString[i])

            if ((i + 1) % 3 == 0 && i != numberString.lastIndex) {
                parts.add(0, ',')
            }
        }
        val result =  parts.joinToString("")
        return result
    }

    fun formatToDollars(number: Int): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        return formatter.format(number.toDouble())
    }
}