package com.example.findpet.activities;

import static com.example.findpet.utils.Constants.ERRORS;
import static com.example.findpet.utils.Utils.showToast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.findpet.R;
import com.example.findpet.databinding.ActivityEditProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityEditProfileBinding binding;
    FirebaseFirestore fs;
    FirebaseAuth fa;
    FirebaseUser fu;
    Uri imageUri;
    Dialog dialog;
    FirebaseStorage fStorage;
    StorageReference sReference;
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            Intent data = o.getData();
            if (o.getResultCode() == RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();
                    Picasso.get().load(imageUri).into(binding.imgProfile);
                }
            }
        }
    });
    ActivityResultLauncher<String> requestPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), o -> {
        if (!o) {
            showToast(EditProfileActivity.this, "Permission Denied");
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fs = FirebaseFirestore.getInstance();
        fa = FirebaseAuth.getInstance();
        fu = fa.getCurrentUser();
        fStorage = FirebaseStorage.getInstance();
        sReference = fStorage.getReference("profileImages").child(fu.getUid());


        initializedClickListener();
        setData();

    }

    private void initializedClickListener() {
        binding.imgBack.setOnClickListener(this);
        binding.btnUpdateProfile.setOnClickListener(this);
        binding.imgEdit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            finish();
        } else if (v.getId() == R.id.btnUpdateProfile) {
            if (binding.btnUpdateProfile.getText().toString().equals("Edit Profile")) {
                binding.edtName.setEnabled(true);
                binding.imgEdit.setVisibility(View.VISIBLE);
                binding.txtUpdateProfile.setText(R.string.update_profile);
                binding.btnUpdateProfile.setText(R.string.update_profile);
            } else if (binding.btnUpdateProfile.getText().toString().equals("Update Profile")) {
                String name = Objects.requireNonNull(binding.edtName.getText()).toString().trim();
                if (isValid(name)) {
                    binding.edtName.setText("");
                    visibleDialog();
                    updateImageInDB(name);
                }
            }
        } else if (v.getId() == R.id.imgEdit) {
            pickImageFromGallery();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                requestPermission();
            }
        }
    }

    private void setData() {
        fs.collection("profile").addSnapshotListener((value, error) -> {
            List<DocumentSnapshot> snapshotList = Objects.requireNonNull(value).getDocuments();

            for (DocumentSnapshot snapshot : snapshotList) {
                if (snapshot.exists()) {
                    if (Objects.equals(snapshot.getString("uId"), fu.getUid())) {
                        binding.edtName.setText(snapshot.getString("name"));
                        Picasso.get().load(snapshot.getString("imageUrl")).placeholder(R.color.light_gray).into(binding.imgProfile);
                    }
                }
            }
        });

    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        resultLauncher.launch(intent);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private boolean isValid(String name) {
        if (name.isEmpty()) {
            binding.edtName.setError("Name is required");
            return false;
        } else {
            return true;
        }
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

    private void updateImageInDB(String name) {
        if (imageUri != null) {
            sReference.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    sReference.getDownloadUrl().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            String imageUrl = task1.getResult().toString();
                            updateProfileInDB(imageUrl, name);
                        }
                    });
                }
            }).addOnFailureListener(e -> Log.d(ERRORS, "onFailure: " + e.getMessage()));
        }
    }

    private void updateProfileInDB(String image, String name) {
        fs.collection("profile").document(fu.getUid()).update("name", name, "imageUrl", image).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                invisibleDialog();
                finish();
            }
        });
    }
}