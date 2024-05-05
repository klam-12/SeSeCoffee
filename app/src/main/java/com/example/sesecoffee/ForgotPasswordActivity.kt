package com.example.sesecoffee

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    val auth =FirebaseAuth.getInstance()
    lateinit var emailForgotPass: EditText
    lateinit var bnt: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        var emaill:String
        emailForgotPass=findViewById<EditText>(R.id.email_forgot)
        bnt=findViewById(R.id.next_step_forgot)

        bnt.setOnClickListener(){
            emaill=emailForgotPass.text.toString()
            if (emaill != "") {
                auth.sendPasswordResetEmail(emaill)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                        } else {
                            val layout: ViewGroup = layoutInflater.inflate(
                                R.layout.customtoast_layout,
                                null
                            ) as ViewGroup
                            val text: TextView = layout.findViewById(R.id.toast_text)
                            text.text = "Email is not exists or not valid"
                            with(Toast(applicationContext)) {
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
}