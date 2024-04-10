package com.example.sesecoffee.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
@Parcelize
data class Redeem (
    var id: String? = null,
    var productId : String? = null,
    var productName: String? = null,
    var imageUrl: String? = null,
    var point: Int? = null,
    var untilAt: Timestamp? = null
) : Parcelable {
}