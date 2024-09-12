package com.example.findpet.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findpet.R;
import com.example.findpet.interfaces.CustomClickListener;
import com.example.findpet.models.PostDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context context;
    List<PostDataModel> postList;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CustomClickListener customClickListener;

    public PostAdapter(Context context, List<PostDataModel> postList, CustomClickListener customClickListener) {
        this.context = context;
        this.postList = postList;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        this.customClickListener = customClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.post_item_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PostDataModel postDataModel = postList.get(position);
        ArrayList<String> imageUrls = postDataModel.getImageUrls();
        Picasso.get().load(imageUrls.get(0)).placeholder(R.color.light_gray).into(holder.postImage);

        holder.postImage.setOnClickListener(v -> customClickListener.onItemClick(position, postDataModel));

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postImage);
        }
    }

}
