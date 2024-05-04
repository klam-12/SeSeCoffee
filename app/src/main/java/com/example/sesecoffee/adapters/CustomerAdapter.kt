package com.example.sesecoffee.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.AdminChatActivity
import com.example.sesecoffee.R
import com.example.sesecoffee.databinding.UserItemBinding
import com.example.sesecoffee.model.Message
import com.example.sesecoffee.viewModel.MessageViewModel

class CustomerAdapter(val context: Context) : RecyclerView.Adapter<CustomerAdapter.CustomViewHolder>(){
    lateinit var binding: UserItemBinding
    lateinit var viewModel: MessageViewModel
    inner class CustomViewHolder(var binding: UserItemBinding):  RecyclerView.ViewHolder(binding.root){
        fun bind(message: Message){
            Log.i("message",message.toString())
            binding.userMessageItem = message
//            binding.customerName.text = message.userName
            binding.userImg.setImageResource(R.drawable.background_chat_input)
        }
    }
    private val diffCallback = object : DiffUtil.ItemCallback<Message>(){
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.message == newItem.message
        }
        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return  oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        binding = UserItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val message =  differ.currentList[position]
        holder.bind(message)
        holder.itemView.setOnClickListener{
            val intent = Intent(context, AdminChatActivity::class.java)
            intent.putExtra("userId", message.userId)
            intent.putExtra("userName",message.userName)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }
    }


}