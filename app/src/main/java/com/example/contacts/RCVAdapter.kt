package com.example.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.databinding.ContactListBinding

class RCVAdapter(
    private val contactList: ArrayList<ContactModel>
) : RecyclerView.Adapter<RCVAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ContactListBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ContactListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = contactList[position]
        holder.binding.nameTV.text = item.displayName
        holder.binding.numberTV.text = item.number
    }

    override fun getItemCount(): Int = contactList.size
}
