package com.zukron.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.zukron.musicplayer.R
import com.zukron.musicplayer.model.Library

/**
 * Project name is Music Player
 * Created by Zukron Alviandy R on 9/3/2020
 * Contact me if any issues on zukronalviandy@gmail.com
 */
class LibraryAdapter(private val context: Context) :
        RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

    var libraries = mutableListOf<Library>()
    var onSelected: OnSelected? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_library, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val library = libraries[position]

        holder.tvTitle.text = library.title
        holder.materialCard.setOnClickListener {
            onSelected.let {
                it!!.onSelectedItem(library)
            }
        }
    }

    override fun getItemCount(): Int {
        return libraries.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val materialCard = itemView.findViewById<MaterialCardView>(R.id.material_card_view)
        val tvTitle = itemView.findViewById<TextView>(R.id.tv_title)
    }

    interface OnSelected {
        fun onSelectedItem(library: Library)
    }
}