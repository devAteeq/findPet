package com.example.findpet.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findpet.R;
import com.example.findpet.databinding.ActivitySearchFilterBinding;
import com.example.findpet.models.CategoryModel;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFilterActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySearchFilterBinding binding;
    ArrayList<CategoryModel> list;
    String petName = "", petGender = "", petAge = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchFilterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeClickListeners();
        addCategoryChip();
        addGenderChip();
        addAgeChip();
    }

    private void initializeClickListeners() {
        binding.imgBack.setOnClickListener(this);
        binding.btnSearchPet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            finish();
        }
        if (v.getId() == R.id.btnSearchPet) {
            if (!petName.isEmpty()) {
                moveToSearchResult();
            } else {
                Toast.makeText(this, "Please select pet type", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addCategoryChip() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection("categories").addSnapshotListener((value, error) -> {
            List<DocumentSnapshot> snapshots = Objects.requireNonNull(value).getDocuments();
            for (DocumentSnapshot documentSnapshot : snapshots) {
                if (documentSnapshot.exists()) {
                    list = new ArrayList<>();
                    list.add(documentSnapshot.toObject(CategoryModel.class));
                    for (int i = 0; i < list.size(); i++) {
                        Chip chip = new Chip(SearchFilterActivity.this);
                        chip.setText(list.get(i).getCategoryName());
                        chip.setCheckable(true);
                        chip.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.light_gray)));
                        chip.setTextColor(getColor(R.color.black));
                        chip.setChipBackgroundColorResource(R.color.white);
                        //if you not create chipGroup in you xml yet then create it
                        binding.chipGroupPetType.addView(chip);

                        // Set the listener after adding all chips
                        binding.chipGroupPetType.setOnCheckedStateChangeListener((group, checkedIds) -> {
                            // Reset the appearance of any previously selected chip
                            for (int i1 = 0; i1 < group.getChildCount(); i1++) {
                                Chip chipChild = (Chip) group.getChildAt(i1);
                                if (!checkedIds.contains(chipChild.getId())) {
                                    chipChild.setChipBackgroundColorResource(R.color.white);
                                    chipChild.setTextColor(getColor(R.color.black));
                                    chipChild.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.light_gray)));
                                }
                            }
                            // Update the appearance of the newly selected chip
                            for (int id : checkedIds) {
                                Chip chip1 = group.findViewById(id);
                                chip1.setChipBackgroundColorResource(R.color.orange);
                                chip1.setTextColor(getColor(R.color.white));
                                chip1.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.orange)));
                                petName = chip1.getText().toString();
                            }
                        });

                    }
                }
            }

        });
    }

    private void addGenderChip() {
        ArrayList<String> genderList = new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");

        for (int i = 0; i < genderList.size(); i++) {
            Chip chip = new Chip(this);
            chip.setText(genderList.get(i));
            chip.setCheckable(true);
            chip.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.light_gray)));
            chip.setTextColor(getColor(R.color.black));
            chip.setChipBackgroundColorResource(R.color.white);
            //if you not create chipGroup in you xml yet then create it
            binding.chipGroupGenderSearch.addView(chip);

            binding.chipGroupGenderSearch.setOnCheckedStateChangeListener((group, checkedIds) -> {
                for (int i1 = 0; i1 < group.getChildCount(); i1++) {
                    Chip chipChild = (Chip) group.getChildAt(i1);
                    if (!checkedIds.contains(chipChild.getId())) {
                        chipChild.setChipBackgroundColorResource(R.color.white);
                        chipChild.setTextColor(getColor(R.color.black));
                        chipChild.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.light_gray)));
                    }
                }
                for (int id : checkedIds) {
                    Chip chip1 = group.findViewById(id);
                    chip1.setChipBackgroundColorResource(R.color.orange);
                    chip1.setTextColor(getColor(R.color.white));
                    chip1.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.orange)));
                    petGender = chip1.getText().toString();
                }
            });
        }
    }

    private void addAgeChip() {
        ArrayList<String> ageList = new ArrayList<>();
        ageList.add("Child");
        ageList.add("Adult");

        for (int i = 0; i < ageList.size(); i++) {
            Chip chip = new Chip(this);
            chip.setText(ageList.get(i));
            chip.setCheckable(true);
            chip.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.light_gray)));
            chip.setTextColor(getColor(R.color.black));
            chip.setChipBackgroundColorResource(R.color.white);
            //if you not create chipGroup in you xml yet then create it
            binding.chipGroupAgeSearch.addView(chip);

            binding.chipGroupAgeSearch.setOnCheckedStateChangeListener((group, checkedIds) -> {
                for (int i1 = 0; i1 < group.getChildCount(); i1++) {
                    Chip chipChild = (Chip) group.getChildAt(i1);
                    if (!checkedIds.contains(chipChild.getId())) {
                        chipChild.setChipBackgroundColorResource(R.color.white);
                        chipChild.setTextColor(getColor(R.color.black));
                        chipChild.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.light_gray)));
                    }
                }
                for (int id : checkedIds) {
                    Chip chip1 = group.findViewById(id);
                    chip1.setChipBackgroundColorResource(R.color.orange);
                    chip1.setTextColor(getColor(R.color.white));
                    chip1.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.orange)));
                    petAge = chip1.getText().toString();
                }
            });

        }
    }

    private void moveToSearchResult() {
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("petName", petName);
        intent.putExtra("petGender", petGender);
        intent.putExtra("petAge", petAge);
        startActivity(intent);
        finish();
    }
}