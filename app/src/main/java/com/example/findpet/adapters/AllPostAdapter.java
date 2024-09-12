package com.example.findpet.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findpet.R;
import com.example.findpet.dp.SQLiteHelper;
import com.example.findpet.interfaces.CustomClickListener;
import com.example.findpet.models.PostDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AllPostAdapter extends RecyclerView.Adapter<AllPostAdapter.ViewHolder> {

    Context context;
    ArrayList<PostDataModel> list;
    CustomClickListener customClickListener;
    SQLiteHelper sqliteHelper;

    public AllPostAdapter(Context context, ArrayList<PostDataModel> list, CustomClickListener customClickListener) {
        this.context = context;
        this.list = list;
        this.customClickListener = customClickListener;
        sqliteHelper = new SQLiteHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.all_post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PostDataModel dataModel = list.get(position);
        holder.postImage.setClipToOutline(true);
        //TODO: Set image
        Picasso.get().load(dataModel.getImageUrls().get(0)).placeholder(R.color.light_gray).into(holder.postImage);
        holder.postTitle.setText(dataModel.getName());

        holder.postImage.setOnClickListener(v -> customClickListener.onItemClick(position, dataModel));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView postImage;
        TextView postTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postImage);
            postTitle = itemView.findViewById(R.id.postName);
        }
    }
}
