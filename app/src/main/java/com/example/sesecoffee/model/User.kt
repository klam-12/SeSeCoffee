package com.example.sesecoffee.model

import java.util.Date
import java.util.UUID

class User(
    val id:String,
    var fullName:String,
    var email:String,
    var password:String,
    var address: String,
    var isAdmin : Int,
    val createdAt: Date,
    var updatedAt: Date,
    ) {
    constructor() :
            this(UUID.randomUUID().toString(),"","","","",0, Date(), Date()) {}

    constructor(fullName:String) :
            this(UUID.randomUUID().toString(),fullName,"","","",0, Date(), Date()) {}

    constructor(fullName:String,email:String,password:String,address:String) :
            this(UUID.randomUUID().toString(),fullName,email,password,address,0, Date(), Date()) {}
    constructor(isAdmin:Int) :
            this(UUID.randomUUID().toString(),"","","","",isAdmin, Date(), Date()) {}

}