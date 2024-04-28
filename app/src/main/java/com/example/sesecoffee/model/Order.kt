package com.example.sesecoffee.model

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

data class Order(
    val id:String? = null,
    var total : Int? = null,
    var createAt : Timestamp? = null,
    var address : String? = null,
    var userId : String? = null,
    var phoneNumber : String? = null,
    var paymentMethod : String? = null,
    var stripeCustomerId : String? = null,
    var done: Boolean= false,
    var delivered: Boolean=false,
    var rating : Float? = null,
    var comment : String? = null

) {
 //   constructor() :
    //        this(id,0, null, "", "","", "", "",false,false) {}
//    constructor(total: Int, paymentId: String, address: String, userId: String, phoneNumber: String, code: String) :
//            this(total, Timestamp.now(), paymentId, address, userId, phoneNumber, code) {}
    public fun formatTimestamp(timestamp: Timestamp): String {
     val dateFormatter = SimpleDateFormat("dd MMMM YYYY") // "24 June"
     val timeFormatter = SimpleDateFormat("HH:mm")  // "12:30"

     val dateString = dateFormatter.format(timestamp.toDate())
     val timeString = timeFormatter.format(timestamp.toDate())

     return "$dateString | $timeString "
 }

    public fun formatNumber(number: Int): String {
        val numberString = number.toString().reversed()

        val parts = mutableListOf<Char>()
        for (i in 0 until numberString.length) {
            parts.add(0, numberString[i])

            if ((i + 1) % 3 == 0 && i != numberString.lastIndex) {
                parts.add(0, ',')
            }
        }
        val result =  parts.joinToString("")
        println(result)
        return "$result VND"
    }
}