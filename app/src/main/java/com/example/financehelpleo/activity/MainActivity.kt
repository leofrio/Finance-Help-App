package com.example.financehelpleo.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.financehelpleo.R
import com.example.financehelpleo.adapter.IncomeAdapter
import com.example.financehelpleo.adapter.IncomeItemListener
import com.example.financehelpleo.model.Income
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), View.OnClickListener, IncomeItemListener {
    private lateinit var mIncomeRecyclerView: RecyclerView
    private lateinit var mIncomeAdd:FloatingActionButton
    private lateinit var mIncomeLoss:FloatingActionButton
    private lateinit var IncomeAdapter: IncomeAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBase: FirebaseDatabase

    private var mIncomeList= mutableListOf<Income>()
    private var handler= Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth= FirebaseAuth.getInstance()
        mDataBase= FirebaseDatabase.getInstance()

        mIncomeRecyclerView= findViewById(R.id.main_recycleview_incomes)
        mIncomeAdd = findViewById(R.id.main_floatingbutton_add_receita)
        mIncomeLoss=findViewById(R.id.main_floatingbutton_add_loss)
        mIncomeLoss.setOnClickListener(this)
        mIncomeAdd.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        val dialog= ProgressDialog(this)
        dialog.setTitle("Carregando")
        dialog.isIndeterminate=true
        dialog.show()

        val query=mDataBase.reference.child("users/${mAuth.uid}/Incomes").orderByKey().limitToLast(10)
        query.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mIncomeList.clear()
                snapshot.children.forEach{
                    val Income=it.getValue(Income::class.java)
                    mIncomeList.add(Income!!)
                }
                handler.post {
                    dialog.dismiss()
                    IncomeAdapter = IncomeAdapter(mIncomeList)
                    IncomeAdapter.setIncomeItemListener(this@MainActivity)
                    //up here we used the @ to set the context of the main activity not the global scope as the listener
                    val llm = LinearLayoutManager(applicationContext)
                    llm.setReverseLayout(true);
                    llm.setStackFromEnd(true);
                    mIncomeRecyclerView.apply {
                        adapter = IncomeAdapter
                        layoutManager = llm
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.main_floatingbutton_add_receita -> {
                val it=Intent(applicationContext,IncomeFormActivity::class.java)
                startActivity(it)
            }
            R.id.main_floatingbutton_add_loss -> {
                val it=Intent(applicationContext,IncomeFormActivity::class.java)
                it.putExtra("isloss",true)
                startActivity(it)
            }
        }
    }

    override fun onClick(v: View, position: Int) {
        val it=Intent(applicationContext,IncomeFormActivity::class.java)
        it.putExtra("Incomeid",mIncomeList[position].id)
        it.putExtra("isloss",mIncomeList[position].loss)
        startActivity(it)
    }

    override fun onLongClick(v: View, position: Int) {
        val dialog =AlertDialog.Builder(this)
            .setTitle("Finance Help")
            .setMessage("voce quer deletar o gasto '${mIncomeList[position].name}'?")
            .setPositiveButton("sim") {dialog, id ->
                val ref = mDataBase.getReference("/users/${mAuth.uid}/Incomes/${mIncomeList[position].id}")
                ref.removeValue().addOnCompleteListener{
                    if(it.isSuccessful) {
                        handler.post {
                            dialog.dismiss()
                            IncomeAdapter.notifyItemRemoved(position)
                            IncomeAdapter.notifyItemRangeChanged(position, IncomeAdapter.itemCount)
                        }
                    } else {
                        Toast.makeText(MainActivity@this,"ocorreu um erro tente denovo",Toast.LENGTH_SHORT).show()
                    }
                }
                /*GlobalScope.launch {

                    mIncomeList.removeAt(position)
                    handler.post {
                        IncomeAdapter.notifyItemRemoved(position)
                    }
                }*/
            }
            .setNegativeButton("nao") {dialog, id ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
}