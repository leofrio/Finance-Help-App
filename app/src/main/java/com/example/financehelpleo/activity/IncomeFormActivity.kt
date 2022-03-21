package com.example.financehelpleo.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.financehelpleo.R
import com.example.financehelpleo.model.Income
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IncomeFormActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mIncomeFormTitle:TextView
    private lateinit var mIncomeFormName:EditText
    private lateinit var mIncomeFormValue:EditText
    private lateinit var mIncomeFormSave:Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBase: FirebaseDatabase
    private val handler= Handler(Looper.getMainLooper())
    private var mIncomeId=""
    private var mIsLoss=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_form)

        mAuth= FirebaseAuth.getInstance()
        mDataBase= FirebaseDatabase.getInstance()

        mIncomeFormTitle=findViewById(R.id.income_form_textview_title)
        mIncomeFormName=findViewById(R.id.income_form_edittext_name)
        mIncomeFormValue=findViewById(R.id.income_form_edittext_value)
        mIncomeFormSave=findViewById(R.id.income_form_button_save)
        mIncomeFormSave.setOnClickListener(this)
        mIncomeId=intent.getStringExtra("Incomeid") ?: ""
        //:? means that if the function is null it will return whats on the right of it
        mIsLoss=intent.getBooleanExtra("isloss",false)
        if(mIsLoss) {
            mIncomeFormTitle.text="Nova Dispesa"
        }
        if(mIncomeId.isNotEmpty()) {

            val query=mDataBase.reference.child("users/${mAuth.uid}/Incomes/${mIncomeId}").orderByKey()
            query.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val Income=snapshot.getValue(Income::class.java)
                    handler.post{
                        if(mIsLoss) {
                            mIncomeFormTitle.text = "editar dispesa"
                        } else {
                            mIncomeFormTitle.text = "editar receita"
                        }
                        if(Income != null) {
                            mIncomeFormName.text =
                                Editable.Factory.getInstance().newEditable(Income?.name)
                            //android sissy, we do this to convert a string into an editable(android text type)
                            mIncomeFormValue.text =
                                Editable.Factory.getInstance().newEditable(Income?.value)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                 }
            })

        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.income_form_button_save -> {
                val name=mIncomeFormName.text.toString()
                val value=mIncomeFormValue.text.toString()
                if(name.isEmpty()) {
                    mIncomeFormName.error="nao pode deixar isso branco"
                    return
                }
                if(value.isEmpty()) {
                    mIncomeFormValue.error="nao pode deixar isso em branco"
                    return
                }
                if(mIncomeId.isEmpty()) {
                    //new Income
                    val IncomeId = mDataBase.reference.child("/users/${mAuth.uid}/Incomes").push().key
                    val ref = mDataBase.getReference("/users/${mAuth.uid}/Incomes/$IncomeId")
                    val Income = Income(IncomeId!!, name, value,mIsLoss)
                    ref.setValue(Income)
                    finish()
                } else {
                    //edit Income
                    val ref = mDataBase.getReference("/users/${mAuth.uid}/Incomes/$mIncomeId")
                    val Income = Income(mIncomeId, name, value, mIsLoss)
                    ref.setValue(Income)
                    finish()
                }
            }
        }
    }
}