package com.example.findpet.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findpet.R;

import java.util.ArrayList;

/**
 * @noinspection ClassEscapesDefinedScope
 */
public class MultipleImagesAdapter extends RecyclerView.Adapter<MultipleImagesAdapter.Holder> {

    private final ArrayList<Uri> list;

    public MultipleImagesAdapter(ArrayList<Uri> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_multi_images, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.multiImageItem.setClipToOutline(true);
        holder.multiImageItem.setImageURI(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        ImageView multiImageItem;

        public Holder(@NonNull View itemView) {
            super(itemView);
            multiImageItem = itemView.findViewById(R.id.multiImageItem);
        }
    }
}

