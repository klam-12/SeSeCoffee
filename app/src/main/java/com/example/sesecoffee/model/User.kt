package com.example.sesecoffee.model

data class User(
    val id:String?=null,
    var fullName:String?=null,
    var email:String?=null,
    var password:String?=null,
    var address: String?=null,
    var isAdmin : Int?=null,
    var redeemPoint: Int?=null,
    var avatar: String ?=null
    ) {

}