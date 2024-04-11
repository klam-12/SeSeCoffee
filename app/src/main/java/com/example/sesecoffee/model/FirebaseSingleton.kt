package com.example.sesecoffee.model

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseSingleton private constructor() {
    var firebaseAuth : FirebaseAuth = Firebase.auth
    var db = FirebaseFirestore.getInstance()
    var storageReference: StorageReference = FirebaseStorage.getInstance().reference

    companion object{
        @Volatile
        private var instance : FirebaseSingleton? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: FirebaseSingleton().also { instance = it }
            }
    }
}



