package com.example.findpet.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.findpet.R;
import com.example.findpet.activities.SignInActivity;
import com.example.findpet.databinding.FragmentLogoutSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    FragmentLogoutSheetBinding binding;
    FirebaseAuth auth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLogoutSheetBinding.inflate(inflater, container, false);

        auth = FirebaseAuth.getInstance();

        initializedClickListeners();

        return binding.getRoot();
    }

    private void initializedClickListeners() {
        binding.btnLogout.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCancel) {
            dismiss();
        } else if (v.getId() == R.id.btnLogout) {
            signOut();
        }
    }

    private void signOut() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity()).setCancelable(false).setTitle("Delete").setMessage("Are you sure you want to delete?").setPositiveButton("Yes", (dialog1, which) -> {
            auth.signOut();
            startActivity(new Intent(getActivity(), SignInActivity.class));
            dismiss();
        }).setNegativeButton("No", (dialog12, which) -> {

        });
        dialog.show();
    }
}