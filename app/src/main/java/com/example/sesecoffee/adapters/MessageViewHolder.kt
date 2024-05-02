package com.example.sesecoffee.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.model.Message

open class MessageViewHolder (view: View) : RecyclerView.ViewHolder(view) {
     open fun bind(message: Message) {}
}