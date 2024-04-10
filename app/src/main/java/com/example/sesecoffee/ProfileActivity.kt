package com.example.sesecoffee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.sesecoffee.databinding.ActivityProfileBinding
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.UserSingleton
import dagger.hilt.android.AndroidEntryPoint


class ProfileActivity : AppCompatActivity() {

    lateinit var binding : ActivityProfileBinding
    private  val fbSingleton = FirebaseSingleton.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_profile)
        binding.profileInputFullName.setText(UserSingleton.instance?.fullName )
        binding.profileInputEmail.setText(UserSingleton.instance?.email )
        binding.profileInputPhone.setText(UserSingleton.instance?.phone )
        binding.profileInputAddress.setText(UserSingleton.instance?.address )



        binding.profileSaveBtn.setOnClickListener() {

            UserSingleton.instance?.fullName = binding.profileInputFullName.text.toString()
            UserSingleton.instance?.email = binding.profileInputEmail.text.toString()
            UserSingleton.instance?.phone = binding.profileInputPhone.text.toString()
            UserSingleton.instance?.address = binding.profileInputAddress.text.toString()

            val documentRef = fbSingleton.db.collection("USER").document(UserSingleton.instance?.id.toString())
            val updates = hashMapOf(
                "fullname" to binding.profileInputFullName.text.toString(),
                "email " to binding.profileInputEmail.text.toString(),
                "phone" to binding.profileInputPhone.text.toString(),
                "address" to binding.profileInputAddress.text.toString()
            )
            documentRef.update(updates as Map<String, Any>)
                .addOnSuccessListener {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that occur
                    Log.i("R","sai roi")
                    println("Error updating fields: $exception")
                }


//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)


        }
        binding.profileSignOutBtn.setOnClickListener(){
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

    }
}