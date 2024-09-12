package com.example.findpet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findpet.R;
import com.example.findpet.models.BannerModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.Holder> {

    Context context;
    List<BannerModel> bannerModelList;

    public BannerAdapter(Context context, List<BannerModel> bannerModelList) {
        this.context = context;
        this.bannerModelList = bannerModelList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.banner_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        BannerModel bannerModel = bannerModelList.get(position);
        Picasso.get().load(bannerModel.getImgBanner()).placeholder(R.color.light_gray).into(holder.bannerImage);
        holder.bannerTitle.setText(bannerModel.getTxtBannerTitle());
        holder.bannerMessage.setText(bannerModel.getTxtBannerMessage());
    }

    @Override
    public int getItemCount() {
        return bannerModelList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        ImageView bannerImage;
        TextView bannerTitle, bannerMessage;

        public Holder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.bannerImage);
            bannerTitle = itemView.findViewById(R.id.bannerTitle);
            bannerMessage = itemView.findViewById(R.id.bannerMessage);
        }
    }
}
