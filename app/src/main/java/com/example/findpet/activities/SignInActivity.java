package com.example.findpet.activities;

import static com.example.findpet.utils.Constants.ERRORS;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findpet.R;
import com.example.findpet.databinding.ActivitySignInBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySignInBinding binding;
    Dialog dialog;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        initializedClickListeners();

    }

    private void initializedClickListeners() {
        binding.btnSignIn.setOnClickListener(this);
        binding.txtSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txtSignUp) {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        } else if (v.getId() == R.id.btnSignIn) {
            String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(binding.edtPassword.getText()).toString().trim().trim();

            if (isValid(email, password)) {
                visibleDialog();
                setEditTextEmpty();
                signIn(email, password);
            }
        }
    }

    private boolean isValid(String email, String password) {
        if (email.isEmpty()) {
            binding.edtEmail.setError("Email is required");
            invisibleDialog();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.setError("Email is invalid");
            invisibleDialog();
            return false;
        } else if (password.isEmpty()) {
            binding.edtPassword.setError("Password is required");
            invisibleDialog();
            return false;
        } else if (password.length() < 6) {
            binding.edtPassword.setError("Password must be at least 6 characters");
            invisibleDialog();
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

    private void setEditTextEmpty() {
        binding.edtEmail.setText("");
        binding.edtPassword.setText("");
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                invisibleDialog();
                Log.d("SignIn", "signInWithEmail:success");
                goToHomeScreen();
            } else {
                Log.d(ERRORS, "onComplete: " + task.getException());
                invisibleDialog();
            }
        });

    }

    private void goToHomeScreen() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}