package com.example.findpet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findpet.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MultipleImagesUpdateAdapter extends RecyclerView.Adapter<MultipleImagesUpdateAdapter.ViewHolder> {

    ArrayList<String> list;

    public MultipleImagesUpdateAdapter(ArrayList<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_multi_images, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setClipToOutline(true);
        Picasso.get().load(list.get(position)).placeholder(R.color.light_gray).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.multiImageItem);
        }
    }
}
