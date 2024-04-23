package com.example.sesecoffee.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.databinding.AdminRatingItemBinding
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.utils.Constant
import com.example.sesecoffee.utils.Format

class AdminRatingAdapter
    : RecyclerView.Adapter<AdminRatingAdapter.RatingViewHolder>()
{
    private val fbSingleton = FirebaseSingleton.getInstance()

        inner class RatingViewHolder(private val itemBinding : AdminRatingItemBinding):RecyclerView.ViewHolder(itemBinding.root){
            private var format : Format = Format()
            fun bind(order: Order,username: String){
                itemBinding.username.text = username;
                itemBinding.ratings.rating = 4.0F;
                itemBinding.time.text = order.createAt?.let { "On " + format.timestampToFormattedString(it) }
                itemBinding.comments.text = "Service is good. Drinks are good too but there are not many options. Waiting for more..."
            }
        }

    private val diffCallback = object : DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffCallback);
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        return RatingViewHolder(
            AdminRatingItemBinding.inflate(
                LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val currentOrder = differ.currentList[position]
        var username = ""

        val userId : String = currentOrder.userId!!
        if (userId != null && userId != "") {
            fbSingleton.db.collection(Constant.USER_COLLECTION).document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        username = document.data?.get(key = "fullname").toString()
                        Log.i("KL",username)
                        holder.bind(currentOrder,username);
                    }
                }
                .addOnFailureListener {
                    Log.i("AdminErr", it.message.toString())
                }
        }else{
            holder.bind(currentOrder,username);
        }


    }


}