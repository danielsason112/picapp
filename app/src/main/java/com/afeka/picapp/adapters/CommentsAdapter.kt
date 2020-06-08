package com.afeka.picapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afeka.picapp.R

class CommentsAdapter(private val dataset: HashMap<String, String>) :
    RecyclerView.Adapter<CommentsAdapter.MyViewHolder>() {

    var iterator = dataset?.iterator()

    class MyViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        val layout: LinearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_item, parent, false) as LinearLayout

        return MyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val next = iterator!!.next()
        holder.layout.findViewById<TextView>(R.id.text_view_author).text = next.key
            holder.layout.findViewById<TextView>(R.id.text_view_comment).text = next.value
    }

    override fun getItemCount() = dataset.size
}