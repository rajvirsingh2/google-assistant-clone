package com.example.googleassistantclone.assistant

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.googleassistantclone.R
import com.example.googleassistantclone.data.Assistant

class AssistantAdapter : RecyclerView.Adapter<AssistantAdapter.ViewHolder>() {

    var data = listOf<Assistant>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val assistantMessage: TextView = itemView.findViewById(R.id.assistant_msg)
        val humanMessage: TextView = itemView.findViewById(R.id.human_msg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssistantAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.assistant_item_layout, parent, false)
                as ConstraintLayout
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssistantAdapter.ViewHolder, position: Int) {
        val item = data[position]
        holder.assistantMessage.text = item.assistantMessage
        holder.humanMessage.text = item.humanMessage
    }

    override fun getItemCount(): Int = data.size
}