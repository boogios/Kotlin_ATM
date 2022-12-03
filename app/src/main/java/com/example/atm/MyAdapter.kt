package com.example.atm

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class MyAdapter(private val joinList: ArrayList<Join>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = joinList[position]
        holder.profileImage.setImageResource(currentItem.profileImage)
        holder.nickname.text = currentItem.nickname
        holder.origin.text = currentItem.origin
        holder.destination.text = currentItem.destination
        holder.numberOfMember.text =
            currentItem.currentNumberPeople.toString() + " / " + currentItem.requestNumberPeople.toString()

        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return joinList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val nickname: TextView = itemView.findViewById(R.id.nickname)
        val origin: TextView = itemView.findViewById(R.id.textViewOrigin)
        val destination: TextView = itemView.findViewById(R.id.textViewDestination)
        val numberOfMember: TextView = itemView.findViewById(R.id.textViewNumberOfMember)
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener: OnItemClickListener
}