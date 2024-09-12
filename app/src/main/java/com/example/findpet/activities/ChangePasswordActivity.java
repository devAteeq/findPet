package com.example.findpet.activities;

import static com.example.findpet.utils.Utils.showToast;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findpet.R;
import com.example.findpet.databinding.ActivityChangePasswordBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityChangePasswordBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        initializedClickListeners();

    }

    private void initializedClickListeners() {
        binding.imgBack.setOnClickListener(this);
        binding.btnChangePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            finish();
        } else if (v.getId() == R.id.btnChangePassword) {
            visibleDialog();
            String currentPassword = Objects.requireNonNull(binding.edtCurrentPassword.getText()).toString().trim();
            String newPassword = Objects.requireNonNull(binding.edtNewPassword.getText()).toString().trim();

            if (isValid(currentPassword, newPassword)) {
                binding.edtCurrentPassword.setText("");
                binding.edtNewPassword.setText("");
                changePassword(currentPassword, newPassword);
            }

        }
    }

    private boolean isValid(String currentPassword, String newPassword) {
        if (currentPassword.isEmpty()) {
            binding.edtCurrentPassword.setError("Current password is required");
            invisibleDialog();
            return false;
        } else if (newPassword.isEmpty()) {
            binding.edtNewPassword.setError("New password is required");
            invisibleDialog();
            return false;
        } else if (newPassword.length() < 6) {
            binding.edtNewPassword.setError("Password must be at least 6 characters");
            invisibleDialog();
            return false;
        }
        return true;
    }

    private void changePassword(String currentPassword, String newPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), currentPassword);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        invisibleDialog();
                        showToast(ChangePasswordActivity.this, "Password changed successfully");
                        finish();
                    } else {
                        showToast(ChangePasswordActivity.this, "Failed");
                        invisibleDialog();
                    }
                });
            } else {
                showToast(ChangePasswordActivity.this, "Failed");
                invisibleDialog();
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