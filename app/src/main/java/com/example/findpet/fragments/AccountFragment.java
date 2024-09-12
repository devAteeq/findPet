package com.example.findpet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.findpet.R;
import com.example.findpet.activities.AccountAndSecurityActivity;
import com.example.findpet.activities.HelpAndSupportActivity;
import com.example.findpet.activities.InviteFriendsActivity;
import com.example.findpet.activities.MyProfileActivity;
import com.example.findpet.databinding.FragmentAccountBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class AccountFragment extends Fragment implements View.OnClickListener {

    FragmentAccountBinding binding;
    FirebaseFirestore fs;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(getLayoutInflater(), container, false);

        fs = FirebaseFirestore.getInstance();
        initializedClickListeners();
        setProfileData();

        return binding.getRoot();
    }

    private void initializedClickListeners() {
        binding.layoutProfile.setOnClickListener(this);
        binding.layoutLogout.setOnClickListener(this);
        binding.layoutInviteFriends.setOnClickListener(this);
        binding.layoutHelpAndSupport.setOnClickListener(this);
        binding.layoutAccountAndSecurity.setOnClickListener(this);
        binding.layoutNotification.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layoutProfile) {
            Objects.requireNonNull(requireActivity()).startActivity(new Intent(getActivity(), MyProfileActivity.class));
        } else if (v.getId() == R.id.layoutLogout) {
            loadBottomSheet();
        } else if (v.getId() == R.id.layoutAccountAndSecurity) {
            startActivity(new Intent(getActivity(), AccountAndSecurityActivity.class));
        } else if (v.getId() == R.id.layoutHelpAndSupport) {
            startActivity(new Intent(getActivity(), HelpAndSupportActivity.class));
        }
        else if (v.getId() == R.id.layoutInviteFriends) {
            startActivity(new Intent(getActivity(), InviteFriendsActivity.class));
        }
    }

    private void setProfileData() {
        fs.collection("profile").addSnapshotListener((value, error) -> {
            List<DocumentSnapshot> snapshots = Objects.requireNonNull(value).getDocuments();
            for (DocumentSnapshot documentSnapshot : snapshots) {
                if (documentSnapshot.exists()) {
                    if (Objects.equals(documentSnapshot.getString("uId"), firebaseUser.getUid())) {
                        binding.txtUserName.setText(documentSnapshot.getString("name"));
                        binding.txtUserEmail.setText(documentSnapshot.getString("email"));
                        Picasso.get().load(documentSnapshot.getString("imageUrl")).placeholder(R.color.light_gray).into(binding.imgUser);
                    }

                }
            }
        });

    }

    private void loadBottomSheet() {
        BottomSheetDialogFragment dialogFragment = new LogoutSheetFragment();
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "Logout");
        dialogFragment.setCancelable(false);
    }
}