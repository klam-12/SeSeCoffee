package com.example.sesecoffee.model

data class Message(
    var message: String?=null,
    var userName: String?=null,
    var adminSend: Boolean?=false,
    var dateTime: String?=null,
    var userId: String?=null,
    var avatar: String?=null
){
    constructor(message: String,dateTime: String,avatar: String?):
            this(message, UserSingleton.instance?.fullName.toString(),false, dateTime,UserSingleton.instance?.id.toString(),avatar)

    constructor(message: String,adminSend: Boolean,userName: String,dateTime: String,userId: String?,avatar: String?):
            this(message,userName,adminSend,dateTime,userId,avatar)
//    constructor() : this(null)

}