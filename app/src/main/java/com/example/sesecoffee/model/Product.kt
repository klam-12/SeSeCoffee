package com.example.sesecoffee.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class Product(
    var name: String? = null,
    var price: Int? = null,
    var imageUrl: String? = null,
    val createdAt: Timestamp? = null,
    val redeem: Redeem? = null
) : Parcelable {
    @Parcelize
    data class Redeem(
        var point: Int? = null,
        var untilAt: Timestamp? = null
    ) : Parcelable
}
