package com.example.sesecoffee.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.R
import com.example.sesecoffee.model.Message

private const val VIEW_TYPE_MY_MESSAGE = 10
private const val VIEW_TYPE_OTHER_MESSAGE = 11
class MessageApdapter()
    : RecyclerView.Adapter<MessageViewHolder>() {

    fun loadMessages(messages: MutableList<Message>) {
        //this.messList = messages
    }

    fun addFirst(message: Message) {
        //messList.add(0, message)
    }


    private val diffCallback = object : DiffUtil.ItemCallback<Message>(){
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.message == newItem.message
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback);
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if(viewType == VIEW_TYPE_MY_MESSAGE) {
            MyMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.send_message_item, parent, false))
        } else {
            OtherMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.received_message_item, parent, false))
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = differ.currentList.get(position)

        holder?.bind(message)


    }

    inner class MyMessageViewHolder (view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.findViewById(R.id.MessageID)
        private var timeText: TextView = view.findViewById(R.id.MessageDateTime)

        override fun bind(message: Message) {
            messageText.text = message.message
            timeText.text = message.dateTime.toString()
        }
    }

    inner class OtherMessageViewHolder (view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.findViewById(R.id.message_receive)
        private var timeText: TextView = view.findViewById(R.id.date_time_receive)

        override fun bind(message: Message) {
            messageText.text = message.message
            timeText.text = message.dateTime.toString()
        }
    }


    override fun getItemViewType(position: Int): Int {
        val message = differ.currentList.get(position)

        return if(message.adminSend == false) {
            VIEW_TYPE_MY_MESSAGE
        }
        else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}

