package com.example.findpet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findpet.R;
import com.example.findpet.databinding.ActivityInviteFriendsBinding;

public class InviteFriendsActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityInviteFriendsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInviteFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeClickListeners();

    }

    private void initializeClickListeners() {
        binding.imgBack.setOnClickListener(this);
        binding.btnInviteFriends.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            finish();
        } else if (v.getId() == R.id.btnInviteFriends) {
            inviteFriendIntent();
        }
    }

    private void inviteFriendIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}