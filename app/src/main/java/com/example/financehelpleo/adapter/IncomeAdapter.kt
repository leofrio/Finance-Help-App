package com.example.financehelpleo.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financehelpleo.R
import com.example.financehelpleo.model.Income


class IncomeAdapter(val incomes: List<Income>): RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {
    private var listener:IncomeItemListener? =null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_income,parent,false)
        return IncomeViewHolder(itemView,listener)
    }

    override fun getItemCount(): Int {
        return incomes.size
    }
    fun setIncomeItemListener(listener: IncomeItemListener?) {
        this.listener=listener
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        holder.incomeName.text=incomes[position].name
        holder.incomeValue.text=incomes[position].value
        if(incomes[position].loss) {
            holder.incomeIsLoss.setBackgroundColor(Color.parseColor("#e04612"))
            holder.incomeRealSign.setTextColor(Color.parseColor("#e04612"))
            holder.incomeValue.setTextColor(Color.parseColor("#e04612"))
        }
        else {
            holder.incomeIsLoss.setBackgroundColor(Color.parseColor("#78DD41"))
            holder.incomeRealSign.setTextColor(Color.parseColor("#78DD41"))
            holder.incomeValue.setTextColor(Color.parseColor("#78DD41"))
        }
    }
    class IncomeViewHolder(itemView: View,listener: IncomeItemListener?):RecyclerView.ViewHolder(itemView) {
        val incomeName:TextView=itemView.findViewById(R.id.item_income_textview_name)
        val incomeValue:TextView=itemView.findViewById(R.id.item_income_textview_value)
        val incomeRealSign:TextView=itemView.findViewById(R.id.item_income_textview_realsign)
        val incomeIsLoss:View=itemView.findViewById(R.id.item_income_view_is_loss)
        init {
            itemView.setOnClickListener{
                listener?.onClick(it,adapterPosition)
            }
            itemView.setOnLongClickListener{
                listener?.onLongClick(it,adapterPosition)
                true
            }
        }
    }
}