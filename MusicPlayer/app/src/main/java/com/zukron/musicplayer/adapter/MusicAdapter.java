package com.zukron.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.zukron.musicplayer.R;
import com.zukron.musicplayer.model.Music;

import java.util.ArrayList;

/**
 * Project name is Music Player
 * Created by Zukron Alviandy R on 7/28/2020
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Music> musics;
    private OnSelected onSelected;

    public MusicAdapter(Context context, ArrayList<Music> musics) {
        this.context = context;
        this.musics = musics;
    }

    public void setOnSelected(OnSelected onSelected) {
        this.onSelected = onSelected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Music music = musics.get(position);

        holder.tvTitleItemMusic.setText(music.getTitle());
        holder.mcvItemMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelected.onSelectedItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musics.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView mcvItemMusic;
        private TextView tvTitleItemMusic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.mcvItemMusic = itemView.findViewById(R.id.mcv_item_music);
            this.tvTitleItemMusic = itemView.findViewById(R.id.tv_title_item_music);
        }
    }

    public interface OnSelected {
        void onSelectedItem(int position);
    }
}
