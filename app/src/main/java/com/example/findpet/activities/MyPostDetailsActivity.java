package com.example.findpet.activities;

import static com.example.findpet.utils.Constants.ERRORS;
import static com.example.findpet.utils.Utils.showToast;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findpet.R;
import com.example.findpet.adapters.SetImagesViewPagerAdapter;
import com.example.findpet.databinding.ActivityMyPostDetailsBinding;
import com.example.findpet.models.PostDataModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MyPostDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMyPostDetailsBinding binding;
    PostDataModel dataModel;
    SetImagesViewPagerAdapter imagesAdapter;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPostDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataModel = (PostDataModel) getIntent().getSerializableExtra("postDetails");

        initializedClickListeners();
        setPostData();

    }

    private void initializedClickListeners() {
        binding.imgBackPostD.setOnClickListener(this);
        binding.btnEditPost.setOnClickListener(this);
        binding.btnDeletePost.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBackPostD) {
            finish();
        } else if (v.getId() == R.id.btnEditPost) {
            moveToUpdatePost();
        } else if (v.getId() == R.id.btnDeletePost) {
            deletePost();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setPostData() {
        try {
            imagesAdapter = new SetImagesViewPagerAdapter(dataModel.getImageUrls());
            binding.viewPagerPosts.setAdapter(imagesAdapter);
            binding.viewPagerPosts.setClipToPadding(false);
            binding.viewPagerPosts.setClipChildren(false);
            binding.viewPagerPosts.setOffscreenPageLimit(2);
            binding.viewPagerPosts.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

            binding.circleIndicatorPostD.setViewPager(binding.viewPagerPosts);
            imagesAdapter.registerAdapterDataObserver(binding.circleIndicatorPostD.getAdapterDataObserver());

            binding.postName.setText(dataModel.getName());
            binding.postGender.setText(dataModel.getGender());
            binding.postAge.setText(dataModel.getAge());
            binding.postDesc.setText(dataModel.getDescription());
            binding.postLocation.setText(dataModel.getLocation());
            binding.postCategory.setText("(" + dataModel.getCategory() + ")");
        } catch (Exception e) {
            Log.d(ERRORS, "setPostData: Profile data" + e.getMessage());
        }
    }

    private void deletePost() {
        visibleDialog();
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        String key = dataModel.getKey();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("Delete").setMessage("Are you sure you want to delete this post?").setCancelable(false).setPositiveButton("Yes, delete", (dialog1, which) -> fs.collection("posts").document(key).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                invisibleDialog();
                showToast(MyPostDetailsActivity.this, "Post deleted successfully");
                finish();
            }
        }).addOnFailureListener(e -> Log.d(ERRORS, "onFailure: Post not delete" + e.getMessage()))).setNegativeButton("No", (dialog12, which) -> invisibleDialog());
        dialog.show();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void visibleDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void invisibleDialog() {
        dialog.dismiss();
    }

    private void moveToUpdatePost() {
        Intent intent = new Intent(this, UpdatePostActivity.class);
        intent.putExtra("postData", dataModel);
        startActivity(intent);
    }
}