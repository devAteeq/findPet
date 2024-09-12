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

public class SetImagesViewPagerAdapter extends RecyclerView.Adapter<SetImagesViewPagerAdapter.ViewHolder> {

    private final ArrayList<String> list;

    public SetImagesViewPagerAdapter(ArrayList<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_multi_images, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.multiImageItem.setClipToOutline(true);
        Picasso.get().load(list.get(position)).placeholder(R.color.light_gray).into(holder.multiImageItem);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView multiImageItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            multiImageItem = itemView.findViewById(R.id.multiImageItem);
        }
    }
}
