package com.example.findpet.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findpet.R;
import com.example.findpet.databinding.ActivityAccountAndSecurityBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AccountAndSecurityActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityAccountAndSecurityBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountAndSecurityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializedClickListeners();

    }

    private void initializedClickListeners() {
        binding.layoutDeleteAccount.setOnClickListener(this);
        binding.imgBack.setOnClickListener(this);
        binding.layoutChangePassword.setOnClickListener(this);
        binding.layoutDeactivateAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            finish();
        } else if (v.getId() == R.id.layoutDeleteAccount) {
            startActivity(new Intent(this, DeleteAccountActivity.class));
        } else if (v.getId() == R.id.layoutChangePassword) {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        } else if (v.getId() == R.id.layoutDeactivateAccount) {
            deactivateAccount();
        }
    }

    private void deactivateAccount() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("Deactivate!").setMessage("Are you sure to deactivate your account?").setCancelable(false).setPositiveButton("Yes", (dialog1, which) -> {
            visibleDialog();
            auth.signOut();
            startActivity(new Intent(AccountAndSecurityActivity.this, SignInActivity.class));
            finish();

        }).setNegativeButton("No", (dialog12, which) -> {

        });
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
}