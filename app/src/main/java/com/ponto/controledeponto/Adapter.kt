package com.ponto.controledeponto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter (
    private val myList: MutableList<String>

) : RecyclerView.Adapter<Adapter.MyViewHolder>() {

    //cria o layout de cada linha
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_adapter, parent, false)
        return MyViewHolder(itemView)
    }
    //exibe as informacoes
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val name = myList[position]

        holder.textName.text = name
    }
    //retorna o tamanho da lista
    override fun getItemCount() = myList.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.textName)
    }

}