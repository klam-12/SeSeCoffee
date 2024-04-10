package com.example.sesecoffee.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sesecoffee.EditProductActivity
import com.example.sesecoffee.EditRedeemActivity
import com.example.sesecoffee.R
import com.example.sesecoffee.databinding.AdminRedeemItemBinding
import com.example.sesecoffee.model.Redeem
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class AdminRedeemAdapter()
    : RecyclerView.Adapter<AdminRedeemAdapter.RedeemViewHolder>()
{
        inner class RedeemViewHolder(private var itemBinding: AdminRedeemItemBinding) : RecyclerView.ViewHolder(itemBinding.root){
            var isValid : Boolean = true
            fun bind(redeem: Redeem){
                itemBinding.redeem = redeem
                // Convert Timestamp to Date
                val date = redeem.untilAt?.toDate()

                // Define the desired date format
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

                // Format the Date object into a string
                var validDateString = "Err"
                if(date != null){
                    validDateString = dateFormat.format(date)
                }

                val currentTimestamp = Timestamp.now()
                // Compare timestamps
                if(redeem.untilAt != null) {
                    if (currentTimestamp.seconds <= redeem.untilAt!!.seconds) {
                        itemBinding.chipStatus.text = "Valid"
                    } else {
                        isValid = false
                        itemBinding.chipStatus.text = "Invalid"
                        itemBinding.chipStatus.setChipBackgroundColorResource(R.color.dark_grey)
                    }
                }


                itemBinding.apply {
                    Glide.with(itemView).load(redeem.imageUrl).into(imageView)
                    redeemValid.text = validDateString;
                }


            }
        }

    private val diffCallback = object : DiffUtil.ItemCallback<Redeem>(){
        override fun areItemsTheSame(oldItem: Redeem, newItem: Redeem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Redeem, newItem: Redeem): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback);
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedeemViewHolder {
        return RedeemViewHolder(AdminRedeemItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RedeemViewHolder, position: Int) {
        val currentRedeem = differ.currentList[position]
        holder.bind(currentRedeem);

        holder.itemView.setOnClickListener(){
            if(holder.isValid){
                val intent = Intent(it.context, EditRedeemActivity::class.java)
                intent.putExtra("redeem",currentRedeem)
                it.context.startActivity(intent)
            } else{
                Toast.makeText(it.context,"This redeem is overdue so cannot be edited",Toast.LENGTH_SHORT).show()
            }
        }
    }


}