package com.example.financehelpleo.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.financehelpleo.R
import com.example.financehelpleo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBase:FirebaseDatabase
    private lateinit var mFullName:EditText
    private lateinit var mPhone:EditText
    private lateinit var mUsername:EditText
    private lateinit var mEmail:EditText
    private lateinit var mPassword:EditText
    private lateinit var mPasswordConfirmation:EditText
    private lateinit var mSignUp:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth= FirebaseAuth.getInstance()
        mDataBase= FirebaseDatabase.getInstance()

        mFullName=findViewById(R.id.register_edittext_fullName)
        mPhone=findViewById(R.id.register_edittext_phone)
        mEmail=findViewById(R.id.register_edittext_email)
        mPassword=findViewById(R.id.register_edittext_password)
        mUsername=findViewById(R.id.register_edittext_username)
        mPasswordConfirmation=findViewById(R.id.register_edittext_password_confirmation)
        mSignUp=findViewById(R.id.register_button_singup)
        mSignUp.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.register_button_singup -> {
                val fullname=mFullName.text.toString()
                val phone=mPhone.text.toString()
                val email=mEmail.text.toString()
                val username=mUsername.text.toString()
                val password=mPassword.text.toString()
                val passwordconf=mPasswordConfirmation.text.toString()

                var isFormFilled=true
                if(fullname.isEmpty()) {
                    mFullName.error = "voce não pode deixar isso em branco"
                    isFormFilled=false
                }
                if(phone.isEmpty()) {
                    mPhone.error = "voce não pode deixar isso em branco"
                    isFormFilled=false
                }
                if(email.isEmpty()) {
                    mEmail.error = "voce não pode deixar isso em branco"
                    isFormFilled=false
                }
                if(password.isEmpty()) {
                    mPassword.error = "voce não pode deixar isso em branco"
                    isFormFilled=false
                }
                if(passwordconf.isEmpty()) {
                    mPasswordConfirmation.error = "voce não pode deixar isso em branco"
                    isFormFilled=false
                }
                if(username.isEmpty()) {
                    mPasswordConfirmation.error = "voce não pode deixar isso em branco"
                    isFormFilled=false
                }
                if(fullname.isEmpty()) {
                    mFullName.error = "voce não pode deixar isso em branco"
                    isFormFilled=false
                }
                if(isFormFilled) {
                    if(password != passwordconf) {
                        mPasswordConfirmation.error= "as senhas nao estao iguais"
                        return
                    }


                    val dialog=ProgressDialog(this)
                    dialog.setTitle("carregando")
                    dialog.isIndeterminate=true
                    dialog.show()



                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                        dialog.dismiss()
                        val handler=Handler(Looper.getMainLooper())
                        if(it.isSuccessful) {
                            val user = User(fullname,phone,email,username)
                            val ref=mDataBase.getReference("users/${mAuth.uid!!}")
                            ref.setValue(user)
                            handler.post{
                                Toast.makeText(applicationContext,"usuario registrado com sucesso",Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }else {
                            handler.post {
                                Toast.makeText(applicationContext,it.exception?.message,Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

            }
        }
    }
}