package com.example.findpet.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.findpet.R;
import com.example.findpet.adapters.AllPostAdapter;
import com.example.findpet.databinding.ActivityShowCategoryPostsBinding;
import com.example.findpet.interfaces.CustomClickListener;
import com.example.findpet.models.CategoryModel;
import com.example.findpet.models.PostDataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ShowCategoryPostsActivity extends AppCompatActivity implements View.OnClickListener, CustomClickListener {

    ActivityShowCategoryPostsBinding binding;
    CategoryModel categoryModel;
    AllPostAdapter categoryPostsAdapter;
    ArrayList<PostDataModel> dataModelArrayList;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowCategoryPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        visibleDialog();
        binding.CategoriesRecycler.setLayoutManager(new GridLayoutManager(this, 2));

        categoryModel = (CategoryModel) getIntent().getSerializableExtra("data");
        binding.CategoryName.setText(Objects.requireNonNull(categoryModel).getCategoryName());

        initializedClickListeners();
        setCategoriesPosts();

    }

    private void initializedClickListeners() {
        binding.imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            finish();
        }
    }

    private void setCategoriesPosts() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection("posts").whereEqualTo("category", categoryModel.getCategoryName()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    dataModelArrayList = new ArrayList<>();
                    if (task.getResult().isEmpty()) {
                        invisibleDialog();
                        binding.emptyBox.setVisibility(View.VISIBLE);
                        binding.txtNoPostsFound.setVisibility(View.VISIBLE);
                    }

                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        dataModelArrayList.add(snapshot.toObject(PostDataModel.class));
                        categoryPostsAdapter = new AllPostAdapter(ShowCategoryPostsActivity.this, dataModelArrayList, ShowCategoryPostsActivity.this);
                        binding.CategoriesRecycler.setAdapter(categoryPostsAdapter);
                        invisibleDialog();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onItemClick(int position, PostDataModel model) {
        Intent intent = new Intent(ShowCategoryPostsActivity.this, PublicPostDetailsActivity.class);
        intent.putExtra("data", model);
        startActivity(intent);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void visibleDialog() {
        dialog = new Dialog(ShowCategoryPostsActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void invisibleDialog() {
        dialog.dismiss();
    }
}