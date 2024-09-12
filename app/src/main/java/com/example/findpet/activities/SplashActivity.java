package com.example.findpet.activities;

import static com.example.findpet.utils.Constants.SPLASH_TIME;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findpet.R;
import com.google.firebase.auth.FirebaseAuth;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loadSplash();
    }

    void loadSplash() {
        new Handler().postDelayed(this::checkUserLoggedIn, SPLASH_TIME);
    }

    private void checkUserLoggedIn() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        } else {
            startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
            finish();
        }
    }
}