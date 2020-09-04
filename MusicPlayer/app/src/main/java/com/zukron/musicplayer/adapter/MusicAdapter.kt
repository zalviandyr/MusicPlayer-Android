package com.zukron.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.zukron.musicplayer.R
import com.zukron.musicplayer.model.Music

/**
 * Project name is Music Player
 * Created by Zukron Alviandy R on 9/3/2020
 * Contact me if any issues on zukronalviandy@gmail.com
 */
class MusicAdapter(private val context: Context) :
        RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    var musics = mutableListOf<Music>()
    var onSelected: OnSelected? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_music, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music = musics[position]

        holder.tvTitle.text = music.title
        holder.materialCard.setOnClickListener {
            onSelected.let {
                it!!.onSelectedItem(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return musics.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val materialCard = itemView.findViewById<MaterialCardView>(R.id.material_card_view)
        val tvTitle = itemView.findViewById<TextView>(R.id.tv_title)
    }

    interface OnSelected {
        fun onSelectedItem(position: Int)
    }
}