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
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    val auth=FirebaseAuth.getInstance()
    var db=FirebaseFirestore.getInstance()
    lateinit var emailLogin: EditText
    lateinit var passwordLogin: EditText
    lateinit var forgotPassword: TextView
    lateinit var signUp:TextView
    lateinit var nextStep:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        emailLogin=findViewById<EditText>(R.id.email_login)
        passwordLogin=findViewById<EditText>(R.id.password_login)
        forgotPassword=findViewById<TextView>(R.id.forgot_pass_login)
        signUp=findViewById(R.id.signup_login)
        nextStep=findViewById(R.id.next_step_login)

        forgotPassword.setOnClickListener(){
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        signUp.setOnClickListener(){
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        nextStep.setOnClickListener(){
            val email=emailLogin.text
            val pass=passwordLogin.text
            if(email.toString() == "" || pass.toString() == ""){
                val layout: ViewGroup = layoutInflater.inflate(R.layout.customtoast_layout,
                    null) as ViewGroup
                val text: TextView = layout.findViewById(R.id.toast_text)
                text.text = "Email and password are required "
                with (Toast(applicationContext)) {
                    setGravity(Gravity.CENTER_VERTICAL, 0, 900)
                    duration = Toast.LENGTH_LONG
                    view = layout
                    show()
                }
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email.toString(), pass.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("LoginActivity", "signInWithEmail:success")
                        val user = auth.currentUser
                        val userId= user?.uid.toString()

                        db.collection(Constant.USER_COLLECTION).document(userId)
                            .get()
                            .addOnSuccessListener { document->
                                if (document != null && document.exists()) {
                                    val userData = document.data
                                    UserSingleton.instance?.fullName = userData?.get(key = "fullname").toString()
                                    UserSingleton.instance?.email = userData?.get(key = "email").toString()
                                    UserSingleton.instance?.address = userData?.get(key = "address").toString()
                                    UserSingleton.instance?.id = userId.toString()
                                    UserSingleton.instance?.isAdmin = Integer.parseInt(userData?.get(key = "isAdmin").toString())
                                    UserSingleton.instance?.phone=userData?.get(key = "phone").toString()
                                    UserSingleton.instance?.avatar=userData?.get(key = "avatar").toString()
                                    UserSingleton.instance?.redeemPoint = userData?.get(key = "redeemPoint").toString().toInt()
                                    //login vao man hinh admin or guess
                                    if(UserSingleton.instance?.isAdmin==1){
                                        val intent = Intent(this, AdminMainActivity::class.java)
                                        startActivity(intent)
                                    }
                                    else{
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }


                                } else {
                                    Log.d("FirestoreData", "No such document")
                                }
                            }.addOnFailureListener { exception ->
                                Log.w("FirestoreData", "Error getting document", exception)
                            }
                    } else {
                        Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                        val layout: ViewGroup = layoutInflater.inflate(R.layout.customtoast_layout,
                            null) as ViewGroup
                        val text: TextView = layout.findViewById(R.id.toast_text)
                        text.text = "Email or password incorrect"
                        with (Toast(applicationContext)) {
                            setGravity(Gravity.CENTER_VERTICAL, 0, 900)
                            duration = Toast.LENGTH_LONG
                            view = layout
                            show()
                        }
                    }
                }
        }

    }

}



