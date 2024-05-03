package com.example.sesecoffee

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewModelScope
import com.example.sesecoffee.databinding.ActivityProfileBinding
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Resource
import com.google.firebase.storage.StorageReference
import com.stripe.model.PaymentMethod.Boleto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProfileActivity : AppCompatActivity() {

    lateinit var binding : ActivityProfileBinding
    private var imageUri: Uri? = null

    private  val fbSingleton = FirebaseSingleton.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_profile)
        binding.profileInputFullName.setText(UserSingleton.instance?.fullName )
        binding.profileInputEmail.setText(UserSingleton.instance?.email )
        binding.profileInputPhone.setText(UserSingleton.instance?.phone )
        binding.profileInputAddress.setText(UserSingleton.instance?.address )

        binding.profileSaveBtn.setOnClickListener() {
            if(!validateInput()){
                return@setOnClickListener
            }
            UserSingleton.instance?.fullName = binding.profileInputFullName.text.toString()
            UserSingleton.instance?.email = binding.profileInputEmail.text.toString()
            UserSingleton.instance?.phone = binding.profileInputPhone.text.toString()
            UserSingleton.instance?.address = binding.profileInputAddress.text.toString()

            saveInfoToDB()

        }
        binding.profileSignOutBtn.setOnClickListener(){
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        val selectImageActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
                if(result.resultCode == AppCompatActivity.RESULT_OK){
                    val intent = result.data
                    imageUri = intent?.data

                    imageUri?.let {
                        this.contentResolver.takePersistableUriPermission(
                            it,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }
                    binding.avatar.setImageURI(imageUri)
                }
            }

        binding.profileChangeAva.setOnClickListener{
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            selectImageActivityForResult.launch(intent)
        }

    }

    private fun validateInput() : Boolean{
        var isValid = true
        val fullName = binding.profileInputFullName.text.toString()
        val phone = binding.profileInputPhone.text.toString()

        if(fullName == ""){
            binding.profileInputFullName.error = "Fullname can't be empty"
            isValid = false
        }

        if(phone == ""){
            binding.profileInputPhone.error = "Phone can't be empty"
            isValid = false
        }

        return isValid
    }

    private fun saveInfoToDB(){
//        saveImageAva()
        val documentRef = fbSingleton.db.collection("USER").document(UserSingleton.instance?.id.toString())
        val updates = hashMapOf(
            "fullname" to binding.profileInputFullName.text.toString(),
            "email" to binding.profileInputEmail.text.toString(),
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
    }

    private fun saveImageAva() {

    }
}