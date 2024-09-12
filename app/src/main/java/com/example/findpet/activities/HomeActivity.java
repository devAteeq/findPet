package com.example.findpet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.findpet.R;
import com.example.findpet.databinding.ActivityHomeBinding;
import com.example.findpet.fragments.AccountFragment;
import com.example.findpet.fragments.FavouriteFragment;
import com.example.findpet.fragments.HomeFragment;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadFragment(new HomeFragment());

        initializedClickListeners();

        binding.bottomNavi.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.menuHome) {
                loadFragment(new HomeFragment());
            } else if (id == R.id.menuMaps) {
                startActivity(new Intent(this, PostsMarkerActivity.class));
            } else if (id == R.id.menuFavourite) {
                loadFragment(new FavouriteFragment());
            } else if (id == R.id.menuAccount) {
                loadFragment(new AccountFragment());
            }

            return true;
        });
    }

    private void initializedClickListeners() {
        binding.btnAddPost.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAddPost) {
            startActivity(new Intent(this, AddPostActivity.class));
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }
}