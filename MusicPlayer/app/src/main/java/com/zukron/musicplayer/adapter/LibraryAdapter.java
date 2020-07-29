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
import com.zukron.musicplayer.model.Library;

import java.util.ArrayList;

/**
 * Project name is Music Player
 * Created by Zukron Alviandy R on 7/27/2020
 */
public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Library> libraries;
    private OnSelected onSelected;

    public LibraryAdapter(Context context, ArrayList<Library> libraries) {
        this.context = context;
        this.libraries = libraries;
    }

    public void setOnSelected(OnSelected onSelected) {
        this.onSelected = onSelected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_library, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Library library = libraries.get(position);

        holder.tvTitleItemLibrary.setText(library.getTitle());
        holder.mcvItemLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelected.onSelectedItem(library);
            }
        });
    }

    @Override
    public int getItemCount() {
        return libraries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView mcvItemLibrary;
        private TextView tvTitleItemLibrary;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mcvItemLibrary = itemView.findViewById(R.id.mcv_item_library);
            tvTitleItemLibrary = itemView.findViewById(R.id.tv_title_item_library);
        }
    }

    public interface OnSelected{
        void onSelectedItem(Library library);
    }
}
