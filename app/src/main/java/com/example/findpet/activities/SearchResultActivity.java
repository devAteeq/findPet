package com.example.findpet.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.findpet.R;
import com.example.findpet.adapters.AllPostAdapter;
import com.example.findpet.databinding.ActivitySearchResultBinding;
import com.example.findpet.interfaces.CustomClickListener;
import com.example.findpet.models.PostDataModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class SearchResultActivity extends AppCompatActivity implements View.OnClickListener, CustomClickListener {

    ActivitySearchResultBinding binding;
    String petName, petGender, petAge;
    Dialog dialog;
    ArrayList<PostDataModel> list;
    AllPostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        visibleDialog();

        binding.recyclerSearchResult.setLayoutManager(new GridLayoutManager(this, 2));

        petName = getIntent().getStringExtra("petName");
        petGender = getIntent().getStringExtra("petGender");
        petAge = getIntent().getStringExtra("petAge");

        initializeClickListeners();
        searchPosts();

    }

    private void initializeClickListeners() {
        binding.imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            finish();
        }
    }

    private void searchPosts() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        if (!petName.isEmpty() && !petGender.isEmpty() && !petAge.isEmpty()) {
            fs.collection("posts").whereEqualTo("category", petName).whereEqualTo("gender", petGender).whereEqualTo("age", petAge).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    list = new ArrayList<>();
                    if (task.getResult().isEmpty()) {
                        invisibleDialog();
                        binding.emptyBox.setVisibility(View.VISIBLE);
                        binding.txtNoPostsFound.setVisibility(View.VISIBLE);
                    }
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        list.add(snapshot.toObject(PostDataModel.class));
                        adapter = new AllPostAdapter(SearchResultActivity.this, list, SearchResultActivity.this);
                        binding.recyclerSearchResult.setAdapter(adapter);
                        invisibleDialog();
                    }
                }
            }).addOnFailureListener(e -> {

            });

        } else if (!petName.isEmpty() && petGender.isEmpty() && petAge.isEmpty()) {
            fs.collection("posts").whereEqualTo("category", petName).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    list = new ArrayList<>();
                    if (task.getResult().isEmpty()) {
                        invisibleDialog();
                        binding.emptyBox.setVisibility(View.VISIBLE);
                        binding.txtNoPostsFound.setVisibility(View.VISIBLE);
                    }
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        list.add(snapshot.toObject(PostDataModel.class));
                        adapter = new AllPostAdapter(SearchResultActivity.this, list, SearchResultActivity.this);
                        binding.recyclerSearchResult.setAdapter(adapter);
                        invisibleDialog();
                    }
                }
            }).addOnFailureListener(e -> {

            });
        } else if (!petName.isEmpty() && !petGender.isEmpty() && petAge.isEmpty()) {
            fs.collection("posts").whereEqualTo("category", petName).whereEqualTo("gender", petGender).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    list = new ArrayList<>();
                    if (task.getResult().isEmpty()) {
                        invisibleDialog();
                        binding.emptyBox.setVisibility(View.VISIBLE);
                        binding.txtNoPostsFound.setVisibility(View.VISIBLE);
                    }
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        list.add(snapshot.toObject(PostDataModel.class));
                        adapter = new AllPostAdapter(SearchResultActivity.this, list, SearchResultActivity.this);
                        binding.recyclerSearchResult.setAdapter(adapter);
                        invisibleDialog();
                    }
                }
            }).addOnFailureListener(e -> {

            });
        } else if (!petName.isEmpty() && petGender.isEmpty() && !petAge.isEmpty()) {
            fs.collection("posts").whereEqualTo("category", petName).whereEqualTo("age", petAge).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    list = new ArrayList<>();
                    if (task.getResult().isEmpty()) {
                        invisibleDialog();
                        binding.emptyBox.setVisibility(View.VISIBLE);
                        binding.txtNoPostsFound.setVisibility(View.VISIBLE);
                    }
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        list.add(snapshot.toObject(PostDataModel.class));
                        adapter = new AllPostAdapter(SearchResultActivity.this, list, SearchResultActivity.this);
                        binding.recyclerSearchResult.setAdapter(adapter);
                        invisibleDialog();
                    }
                }
            }).addOnFailureListener(e -> {

            });
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void visibleDialog() {
        dialog = new Dialog(SearchResultActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void invisibleDialog() {
        dialog.dismiss();
    }

    @Override
    public void onItemClick(int position, PostDataModel model) {
        Intent intent = new Intent(SearchResultActivity.this, PublicPostDetailsActivity.class);
        intent.putExtra("data", model);
        startActivity(intent);
    }
}