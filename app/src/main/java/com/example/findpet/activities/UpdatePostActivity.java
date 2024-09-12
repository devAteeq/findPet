package com.example.findpet.activities;

import static com.example.findpet.utils.Constants.ERRORS;
import static com.example.findpet.utils.Utils.showToast;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findpet.R;
import com.example.findpet.adapters.MultipleImagesUpdateAdapter;
import com.example.findpet.databinding.ActivityUpdatePostBinding;
import com.example.findpet.models.PostDataModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class UpdatePostActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityUpdatePostBinding binding;
    PostDataModel dataModel;
    MultipleImagesUpdateAdapter updateAdapter;
    String[] genderList = {"Male", "Female"};
    String[] ageList = {"Child", "Adult"};
    ArrayAdapter<String> genderAdapter;
    ArrayAdapter<String> ageAdapter;
    String address;
    Dialog dialog;
    ActivityResultLauncher<Intent> locationPickerActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            Intent data = o.getData();
            if (o.getResultCode() == RESULT_OK && data != null) {
                double latitude = data.getDoubleExtra("latitude", 0.0);
                double longitude = data.getDoubleExtra("longitude", 0.0);
                address = data.getStringExtra("address");

                binding.txtLocationUpdate.setText(address);
                binding.iconDropDown.setVisibility(View.GONE);

                Log.d("latitude", "onActivityResult: " + latitude);
                Log.d("longitude", "onActivityResult: " + longitude);
                Log.d("address", "onActivityResult: " + address);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataModel = (PostDataModel) getIntent().getSerializableExtra("postData");

        binding.txtLocationUpdate.setTextColor(getColor(R.color.gray));

        initializedClickListeners();
        setData();
        addGenderDropdownList();
        addAgeDropdownList();

    }

    private void initializedClickListeners() {
        binding.iconBack.setOnClickListener(this);
        binding.btnPostUpdate.setOnClickListener(this);
        binding.txtLocationUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iconBack) {
            finish();
        } else if (v.getId() == R.id.btnPostUpdate) {
            if (binding.btnPostUpdate.getText().toString().equals("Edit post")) {
                binding.edtPetNameUpdate.setEnabled(true);
                binding.edtGenderUpdate.setEnabled(true);
                binding.edtAgeUpdate.setEnabled(true);
                binding.edtPostDescriptionUpdate.setEnabled(true);
                binding.iconDropDown.setVisibility(View.VISIBLE);
                binding.txtLocationUpdate.setTextColor(getColor(R.color.black));
                binding.btnPostUpdate.setText(R.string.update_post);
                return;
            }
            if (binding.btnPostUpdate.getText().equals("Update Post")) {
                if (address == null) {
                    address = dataModel.getLocation();
                    visibleDialog();
                    String name = Objects.requireNonNull(binding.edtPetNameUpdate.getText()).toString().trim();
                    String gender = binding.edtGenderUpdate.getText().toString().trim();
                    String age = binding.edtAgeUpdate.getText().toString().trim();
                    String description = Objects.requireNonNull(binding.edtPostDescriptionUpdate.getText()).toString().trim();

                    if (isValid(name, gender, age, description)) {
                        updatePost(name, gender, age, description);
                    }
                } else {
                    visibleDialog();
                    String name = Objects.requireNonNull(binding.edtPetNameUpdate.getText()).toString().trim();
                    String gender = binding.edtGenderUpdate.getText().toString().trim();
                    String age = binding.edtAgeUpdate.getText().toString().trim();
                    String description = Objects.requireNonNull(binding.edtPostDescriptionUpdate.getText()).toString().trim();

                    if (isValid(name, gender, age, description)) {
                        updatePost(name, gender, age, description);
                    }
                }

            }

        } else if (v.getId() == R.id.txtLocationUpdate) {
            if (binding.btnPostUpdate.getText().equals("Update Post")) {
                openLocationPickerActivity();
            }
        }
    }

    private boolean isValid(String name, String age, String gender, String description) {
        if (name.isEmpty()) {
            binding.edtPetNameUpdate.setError("Name is required");
            invisibleDialog();
            return false;
        } else if (age.isEmpty()) {
            binding.edtAgeUpdate.setError("Age is required");
            invisibleDialog();
            return false;
        } else if (gender.isEmpty()) {
            binding.edtGenderUpdate.setError("Gender is required");
            invisibleDialog();
            return false;
        } else if (description.isEmpty()) {
            binding.edtPostDescriptionUpdate.setError("Description is required");
            invisibleDialog();
            return false;
        } else {
            return true;
        }
    }

    private void setData() {
        binding.edtPetNameUpdate.setText(dataModel.getName());
        binding.edtGenderUpdate.setText(dataModel.getGender());
        binding.edtAgeUpdate.setText(dataModel.getAge());
        binding.edtPostDescriptionUpdate.setText(dataModel.getDescription());
        binding.txtLocationUpdate.setText(dataModel.getLocation());

        updateAdapter = new MultipleImagesUpdateAdapter(dataModel.getImageUrls());
        binding.multipleImgViewPagerUpdate.setAdapter(updateAdapter);
        binding.multipleImgViewPagerUpdate.setClipToPadding(false);
        binding.multipleImgViewPagerUpdate.setClipChildren(false);
        binding.multipleImgViewPagerUpdate.setOffscreenPageLimit(2);
        binding.multipleImgViewPagerUpdate.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        binding.circleIndicatorUpdate.setViewPager(binding.multipleImgViewPagerUpdate);
        updateAdapter.registerAdapterDataObserver(binding.circleIndicatorUpdate.getAdapterDataObserver());
    }

    private void addGenderDropdownList() {
        genderAdapter = new ArrayAdapter<>(this, androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, genderList);
        binding.edtGenderUpdate.setAdapter(genderAdapter);
    }

    private void addAgeDropdownList() {
        ageAdapter = new ArrayAdapter<>(this, androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, ageList);
        binding.edtAgeUpdate.setAdapter(ageAdapter);
    }

    private void openLocationPickerActivity() {
        Intent intent = new Intent(this, LocationPickerActivity.class);
        locationPickerActivityResultLauncher.launch(intent);
    }

    private void updatePost(String name, String gender, String age, String description) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        String key = dataModel.getKey();

        fs.collection("posts").document(key).update("name", name, "gender", gender, "age", age, "description", description, "location", address).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(ERRORS, "onComplete: Post updated successfully");
                showToast(UpdatePostActivity.this, "Post updated successfully");
                invisibleDialog();
                startActivity(new Intent(UpdatePostActivity.this, MyProfileActivity.class));
                finish();
            }
        }).addOnFailureListener(e -> Log.d(ERRORS, "onFailure: Not update post" + e.getMessage()));
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