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

import androidx.appcompat.app.AppCompatActivity;

import com.example.findpet.R;
import com.example.findpet.databinding.ActivityDeleteAccountBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class DeleteAccountActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityDeleteAccountBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializedClickListeners();

    }

    private void initializedClickListeners() {
        binding.imgBack.setOnClickListener(this);
        binding.btnDeleteAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            finish();
        } else if (v.getId() == R.id.btnDeleteAccount) {
            visibleDialog();
            String name = Objects.requireNonNull(binding.edtAccountName.getText()).toString().trim();
            String password = Objects.requireNonNull(binding.edtAccountPassword.getText()).toString().trim();
            if (isValid(name, password)) {
                deleteProfileImageInStorage(name);
            }
        }
    }

    private boolean isValid(String name, String password) {
        if (name.isEmpty()) {
            binding.edtAccountName.setError("Name is required");
            invisibleDialog();
            return false;
        } else if (password.isEmpty()) {
            binding.edtAccountPassword.setError("Password is required");
            invisibleDialog();
            return false;
        } else {
            return true;
        }
    }

    private void deleteProfileImageInStorage(String name) {
        // Delete profile image from storage
        FirebaseStorage fs = FirebaseStorage.getInstance();
        StorageReference sr = fs.getReference("profileImages").child(user.getUid());
        sr.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                checkAccountDetails(name);
            }
        }).addOnFailureListener(e -> Log.d(ERRORS, "onFailure: image not delete in storage" + e.getMessage()));

    }

    private void checkAccountDetails(String name) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection("profile").whereEqualTo("name", name).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                delete();
            }
        }).addOnFailureListener(e -> Log.d(ERRORS, "onFailure: " + e.getMessage()));
    }

    private void delete() {
        // Delete user from Firestore
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection("profile").document(user.getUid()).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // delete email in authenticate
                user.delete().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        invisibleDialog();
                        startActivity(new Intent(DeleteAccountActivity.this, SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    } else {
                        showToast(DeleteAccountActivity.this, "Failed");
                    }
                });
            } else {
                showToast(DeleteAccountActivity.this, "Failed");
            }
        });
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
}