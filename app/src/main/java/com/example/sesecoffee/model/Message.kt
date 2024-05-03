package com.example.sesecoffee.model

data class Message(
    var message: String,
    var userId: String,
    var adminSend: Boolean,
    var dateTime: String,
){
    constructor() :
            this("","",false,"")
    constructor(message: String):
            this(message, UserSingleton.instance?.id.toString(),false, "")
    constructor(message: String, isAdmin: Boolean):
            this(message, UserSingleton.instance?.id.toString(),isAdmin, "")
}