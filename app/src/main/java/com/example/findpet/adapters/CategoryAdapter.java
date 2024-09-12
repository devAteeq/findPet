package com.example.findpet.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findpet.R;
import com.example.findpet.interfaces.CategoryDataInterface;
import com.example.findpet.models.CategoryModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Holder> {

    List<CategoryModel> list;
    Context context;
    CategoryDataInterface categoryDataInterface;

    public CategoryAdapter(List<CategoryModel> list, Context context, CategoryDataInterface categoryDataInterface) {
        this.list = list;
        this.context = context;
        this.categoryDataInterface = categoryDataInterface;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.categories_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        CategoryModel model = list.get(position);
        holder.txtCategoryName.setText(model.getCategoryName());
        Picasso.get().load(model.getCategoryImage()).placeholder(R.color.light_gray).into(holder.imgCategory);

        holder.imgCategory.setOnClickListener(v -> categoryDataInterface.onItemClick(position, model));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        CircleImageView imgCategory;
        TextView txtCategoryName;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
        }
    }
}
