package com.example.financehelpleo.adapter

import android.view.View

interface IncomeItemListener {
        fun onClick(v: View, position:Int)

        fun onLongClick(v: View, position: Int)
}