package com.example.sesecoffee.model

data class Message(
    var message: String?=null,
    var userName: String?=null,
    var adminSend: Boolean?=false,
    var dateTime: String?=null,
    var userId: String?=null
){
    constructor(message: String,dateTime: String):
            this(message, UserSingleton.instance?.fullName.toString(),false, dateTime,UserSingleton.instance?.id.toString())

    constructor(message: String,adminSend: Boolean,userName: String,dateTime: String,userId: String?):
            this(message,userName,adminSend,dateTime,userId)
//    constructor() : this(null)

}