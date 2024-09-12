package com.example.findpet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findpet.R;
import com.example.findpet.databinding.ActivityHelpAndSupportBinding;

public class HelpAndSupportActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityHelpAndSupportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpAndSupportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializedClickListeners();

    }

    private void initializedClickListeners() {
        binding.imgBack.setOnClickListener(this);
        binding.layoutContactSupport.setOnClickListener(this);
        binding.layoutPrivacyPolicy.setOnClickListener(this);
        binding.layoutTermsOfService.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            finish();
        } else if (v.getId() == R.id.layoutContactSupport) {
            //startActivity(new Intent(this, ContactSupportActivity.class));
        } else if (v.getId() == R.id.layoutPrivacyPolicy) {
            startActivity(new Intent(this, PrivacyPolicyActivity.class));
        } else if (v.getId() == R.id.layoutTermsOfService) {
            startActivity(new Intent(this, TermsOfServiceActivity.class));
        }
    }
}