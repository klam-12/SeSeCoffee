package com.example.sesecoffee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.sesecoffee.model.User
import com.example.sesecoffee.model.UserSingleton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    lateinit var email:EditText
    lateinit var passwordSignup:EditText
    lateinit var nextStep: Button
    lateinit var signIn: TextView
    lateinit var firebaseAuth : FirebaseAuth
    lateinit var username: EditText
    lateinit var phone: EditText
    var db= FirebaseFirestore.getInstance()
    var auth= FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        nextStep = findViewById<Button>(R.id.nextstepsignup)
        email = findViewById<EditText>(R.id.email_signup)
        passwordSignup = findViewById<EditText>(R.id.password_signup)
        signIn = findViewById<TextView>(R.id.signin_signup)
        phone=findViewById<EditText>(R.id.phone_signup)
        username= findViewById<EditText>(R.id.username_signup)


        firebaseAuth = Firebase.auth
        nextStep.setOnClickListener(){
            if(!validateInput()){
                return@setOnClickListener
            }

            val pass = passwordSignup.text
            val emailAddress = email.text

            firebaseAuth.createUserWithEmailAndPassword(emailAddress.toString(), pass.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("MainActivity", "createUserWithEmail:success")
                        val user = auth.currentUser


                        val newuser = hashMapOf(
                            "UID" to user?.uid.toString(),
                            "fullname" to username.text.toString(),
                            "address" to "",
                            "email" to emailAddress.toString(),
                            "phone" to phone.text.toString(),
                            "isAdmin" to "0"
                        )
                        if (user != null) {
                            db.collection("USER").document(user.uid)
                                .set(newuser)
                                .addOnSuccessListener {
                                    Log.d("MainActivity", "createUserWithEmail:success")
                                    val intent = Intent(this, SignInActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    Log.d(it.toString(), e.message.toString())
                                }
                        }
                    } else {
                        Log.w("MainActivity", "createUserWithEmail:failure", task.exception)
                        val layout: ViewGroup = layoutInflater.inflate(R.layout.customtoast_layout,
                            null)as ViewGroup
                        val text: TextView = layout.findViewById(R.id.toast_text)
                        text.text = "Email already exists"
                        with (Toast(applicationContext)) {
                            setGravity(Gravity.CENTER_VERTICAL, 0, 900)
                            duration = Toast.LENGTH_LONG
                            view = layout
                            show()
                        }
                    }
                }.addOnFailureListener(this) { it ->
                    Log.d(it.toString(), it.message.toString())
                }
        }

        signIn.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInput() : Boolean{
        var isValid = true
        val fullName = username.text.toString()
        val emailInput = email.text.toString()
        val password = passwordSignup.text.toString()
        val phoneNumber = phone.text.toString()

        if(fullName == ""){
            username.error = "Name can't be empty"
            isValid = false
        }

        if(emailInput == ""){
            email.error = "Email can't be empty"
            isValid = false
        }

        if(password == ""){
            passwordSignup.error = "Password can't be empty"
            isValid = false
        }

        if(phoneNumber == ""){
            phone.error = "Phone can't be empty"
            isValid = false
        }

        return isValid
    }
}