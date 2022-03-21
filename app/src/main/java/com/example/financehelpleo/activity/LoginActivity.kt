package com.example.financehelpleo.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.financehelpleo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var mLoginEmail:EditText
    private lateinit var mLoginPassword:EditText
    private lateinit var mLoginSignIn:Button
    private lateinit var mRegister:TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBase: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth= FirebaseAuth.getInstance()
        mDataBase= FirebaseDatabase.getInstance()

        mLoginEmail=findViewById(R.id.login_edittext_email)
        mLoginPassword=findViewById(R.id.login_edittext_password)
        mLoginSignIn=findViewById(R.id.login_button_singin)
        mLoginSignIn.setOnClickListener(this)
        mRegister=findViewById(R.id.login_textview_register)
        mRegister.setOnClickListener(this)


    }
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.login_textview_register -> {
                val it= Intent(applicationContext,RegisterActivity::class.java)
                startActivity(it)
            }
            R.id.login_button_singin -> {
                val email=mLoginEmail.text.toString()
                val password=mLoginPassword.text.toString()
                var isLoginFormFilled=true
                if(email.isEmpty()) {
                    mLoginEmail.error="this section cant be empty"
                    isLoginFormFilled=false
                }
                if(password.isEmpty()) {
                    mLoginPassword.error="this section cant be empty"
                    isLoginFormFilled=false
                }
                if(isLoginFormFilled){

                    val dialog= ProgressDialog(this)
                    dialog.setTitle("Loading")
                    dialog.isIndeterminate=true
                    dialog.show()


                    mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener{
                            dialog.dismiss()
                            if(it.isSuccessful) {
                                val it=Intent(applicationContext,MainActivity::class.java)
                                startActivity(it)
                                finish()
                            }else {
                                showToastMessage()
                            }
                        }
                }

            }
        }
    }
    private fun showToastMessage() {
        val handler=Handler(Looper.getMainLooper())
        handler.post {
            Toast.makeText(
                applicationContext,
                "senha ou usuario incorretos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}