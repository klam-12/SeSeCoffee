package com.example.sesecoffee.model

import android.app.Application

class UserSingleton : Application() {
    var id:String? = null
    var fullName:String? = null
    var email:String? = null
    var address: String? = null
    var isAdmin : Int? = null
    var phone: String?=null
    var redeemPoint: Int?=null
    var avatar:String?=null
    companion object{
        var instance : UserSingleton? = null
            get(){
                if(field == null){
                    field = UserSingleton()
                }
                return field
            }

            private set
    }
}

