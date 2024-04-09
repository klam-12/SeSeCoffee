package com.example.sesecoffee.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class Redeem (
    var id: String? = null,
    var productId : String? = null,
    var productName: String? = null,
    var imageUrl: String? = null,
    var point: Int? = null,
    var untilAt: Timestamp? = null
) : Parcelable{

    fun convertToFormattedDate(): String {
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
        val date = untilAt?.toDate()
        val formattedDate = dateFormat.format(date)
        return "Valid until $formattedDate"
    }
}