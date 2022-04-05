package com.example.radiokanal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.radiokanal.R
import com.example.radiokanal.model.RadioModel

class RadioListAdapter(val itemList: ArrayList<RadioModel>) :
    RecyclerView.Adapter<RadioListAdapter.ItemListViewHolder>() {

    var onItemClick: ((RadioModel) -> Unit) ?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        return ItemListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_radio_channel,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        holder.bindItems(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    inner class ItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindItems(itemModel: RadioModel){
            val title = itemView.findViewById(R.id.t_title) as TextView
            title.text = itemModel.name

            itemView.setOnClickListener{
                onItemClick?.invoke(itemList[adapterPosition])
            }

        }
    }
}