package com.example.findpet.activities;

import static com.example.findpet.utils.Constants.ERRORS;
import static com.example.findpet.utils.Utils.showToast;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
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
import com.example.findpet.adapters.MultipleImagesAdapter;
import com.example.findpet.databinding.ActivityAddPostBinding;
import com.example.findpet.models.CategoryModel;
import com.example.findpet.models.PostDataModel;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityAddPostBinding binding;
    FirebaseFirestore firestore;
    ArrayList<CategoryModel> list;
    String[] genderList = {"Male", "Female"};
    ArrayAdapter<String> genderAdapter;
    String[] ageList = {"Child", "Adult"};
    ArrayAdapter<String> ageAdapter;
    ArrayList<Uri> uriList;
    MultipleImagesAdapter imagesAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String address, categoryName;
    Dialog dialog;
    String text, text2, urls;
    double longitude;
    double latitude;

    ActivityResultLauncher<Intent> locationPickerActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            Intent data = o.getData();
            if (o.getResultCode() == RESULT_OK && data != null) {
                latitude = data.getDoubleExtra("latitude", 0.0);
                longitude = data.getDoubleExtra("longitude", 0.0);
                address = data.getStringExtra("address");

                binding.txtLocation.setText(address);
                binding.iconDropDown.setVisibility(View.GONE);

                Log.d("latitude", "onActivityResult: " + latitude);
                Log.d("longitude", "onActivityResult: " + longitude);
                Log.d("address", "onActivityResult: " + address);
            }
        }
    });
    ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onActivityResult(ActivityResult o) {
            Intent data = o.getData();
            if (o.getResultCode() == RESULT_OK && data != null) {
                if (data.getClipData() != null) {
                    int x = data.getClipData().getItemCount();
                    for (int i = 0; i < x; i++) {
                        binding.multipleImgViewPager.setVisibility(View.VISIBLE);
                        binding.imgPost.setVisibility(View.GONE);
                        uriList.add(data.getClipData().getItemAt(i).getUri());
                        detectImage(data.getClipData().getItemAt(i).getUri());
                    }
                    imagesAdapter.notifyDataSetChanged();

                } else if (data.getData() != null) {
                    String imageUri = data.getData().getPath();
                    binding.multipleImgViewPager.setVisibility(View.VISIBLE);
                    uriList.add(Uri.parse(imageUri));
                    binding.imgPost.setVisibility(View.GONE);
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        initializedClickListener();
        setMultipleImages();
        readCategoryForDB();
        addGenderDropdownList();
        addAgeDropdownList();


    }

    private void initializedClickListener() {
        binding.iconBack.setOnClickListener(this);
        binding.txtLocation.setOnClickListener(this);
        binding.imgPick.setOnClickListener(this);
        binding.btnPost.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iconBack) {
            finish();
        } else if (v.getId() == R.id.txtLocation) {
            openLocationPickerActivity();
        } else if (v.getId() == R.id.imgPick) {
            pickImage();
        } else if (v.getId() == R.id.btnPost) {
            if (text2.equals("Cat") || text2.equals("Pet") || text2.equals("Dog") || text2.equals("Puppy") || text2.equals("Bird") || text2.equals("Pigeon") || text2.equals("Parrot") || text2.equals("Rabbit") || text.equals("Cat") || text.equals("Pet") || text.equals("Dog") || text.equals("Puppy") || text.equals("Bird") || text.equals("Pigeon") || text.equals("Parrot") || text.equals("Rabbit")) {
                visibleDialog();
                String petName = Objects.requireNonNull(binding.edtPetName.getText()).toString().trim();
                String petAge = binding.edtAge.getText().toString().trim();
                String petGender = binding.edtGender.getText().toString().trim();
                String petDescription = Objects.requireNonNull(binding.edtPostDescription.getText()).toString().trim();

                if (isValid(petName, petAge, petGender, petDescription)) {
                    uploadImagesInStorage(new ArrayList<>(), petName, petAge, petGender, petDescription);
                }
            } else {
                showToast(this, "This post pictures not uploaded please change the images");
            }
        }
    }

    private boolean isValid(String name, String age, String gender, String description) {
        if (uriList == null) {
            showToast(this, "Please select an image");
            invisibleDialog();
            return false;
        } else if (name.isEmpty()) {
            binding.edtPetName.setError("Name is required");
            invisibleDialog();
            return false;
        } else if (age.isEmpty()) {
            binding.edtAge.setError("Age is required");
            invisibleDialog();
            return false;
        } else if (gender.isEmpty()) {
            binding.edtGender.setError("Gender is required");
            invisibleDialog();
            return false;
        } else if (description.isEmpty()) {
            binding.edtPostDescription.setError("Description is required");
            invisibleDialog();
            return false;
        } else if (address.isEmpty()) {
            showToast(this, "Please select location");
            invisibleDialog();
            return false;
        } else {
            return true;
        }
    }

    private void setMultipleImages() {
        uriList = new ArrayList<>();
        imagesAdapter = new MultipleImagesAdapter(uriList);
        binding.multipleImgViewPager.setAdapter(imagesAdapter);
        binding.multipleImgViewPager.setClipToPadding(false);
        binding.multipleImgViewPager.setClipChildren(false);
        binding.multipleImgViewPager.setOffscreenPageLimit(2);
        binding.multipleImgViewPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        binding.circleIndicator.setViewPager(binding.multipleImgViewPager);
        imagesAdapter.registerAdapterDataObserver(binding.circleIndicator.getAdapterDataObserver());
    }

    @SuppressLint("ResourceAsColor")
    private void readCategoryForDB() {
        firestore.collection("categories").addSnapshotListener((value, error) -> {
            List<DocumentSnapshot> snapshots = Objects.requireNonNull(value).getDocuments();
            for (DocumentSnapshot documentSnapshot : snapshots) {
                if (documentSnapshot.exists()) {
                    list = new ArrayList<>();
                    list.add(documentSnapshot.toObject(CategoryModel.class));
                    for (int i = 0; i < list.size(); i++) {
                        Chip chip = new Chip(this);
                        chip.setText(list.get(i).getCategoryName());
                        chip.setCheckable(true);
                        chip.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.light_gray)));
                        chip.setTextColor(getColor(R.color.black));
                        chip.setChipBackgroundColorResource(R.color.white);
                        binding.chipGroup.addView(chip);
                    }

                    // Set the listener after adding all chips
                    binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                        // Reset the appearance of any previously selected chip
                        for (int i = 0; i < group.getChildCount(); i++) {
                            Chip childChip = (Chip) group.getChildAt(i);
                            if (!checkedIds.contains(childChip.getId())) {
                                childChip.setChipBackgroundColorResource(R.color.white);
                                childChip.setTextColor(getColor(R.color.black));
                                childChip.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.light_gray)));
                            }
                        }

                        // Update the appearance of the newly selected chip
                        for (int id : checkedIds) {
                            Chip chip1 = group.findViewById(id);
                            categoryName = chip1.getText().toString();
                            chip1.setChipBackgroundColorResource(R.color.orange);
                            chip1.setTextColor(getColor(R.color.white));
                            chip1.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.orange)));
                        }
                    });
                }
            }
        });
    }

    private void openLocationPickerActivity() {
        Intent intent = new Intent(this, LocationPickerActivity.class);
        locationPickerActivityResultLauncher.launch(intent);
    }

    private void addGenderDropdownList() {
        genderAdapter = new ArrayAdapter<>(this, androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, genderList);
        binding.edtGender.setAdapter(genderAdapter);
    }

    private void addAgeDropdownList() {
        ageAdapter = new ArrayAdapter<>(this, androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, ageList);
        binding.edtAge.setAdapter(ageAdapter);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        pickImageActivityResultLauncher.launch(intent);

    }

    private void detectImage(Uri uriImage) {
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(this, uriImage);
        } catch (Exception e) {
            Log.d(ERRORS, "detectImage: " + e.getMessage());
        }
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(Objects.requireNonNull(image)).addOnSuccessListener(labels -> {
            for (ImageLabel imageLabel : labels) {
                text = labels.get(0).getText();
                text2 = labels.get(1).getText();

            }
        }).addOnFailureListener(e -> Log.d(ERRORS, "onFailure: " + e.getMessage()));
    }

    private void uploadImagesInStorage(ArrayList<String> imageUrls, String petName, String petAge, String petGender, String petDescription) {
        for (Uri uriSize : uriList) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("imagesPosts").child(UUID.randomUUID().toString());
            storageReference.putFile(uriSize).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                urls = task.getResult().toString();
                imageUrls.add(urls);

                if (uriList.size() == imageUrls.size()) {
                    addPostDataOnFS(imageUrls, petName, petAge, petGender, petDescription);

                }
            }).addOnFailureListener(e -> Log.d(ERRORS, "onFailure: " + e.getMessage())));
        }
    }


    private void addPostDataOnFS(ArrayList<String> imageUrls, String petName, String petAge, String petGender, String petDescription) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        String key = fs.collection("posts").document().getId();
        PostDataModel postDataModel = new PostDataModel();
        postDataModel.setName(petName);
        postDataModel.setAge(petAge);
        postDataModel.setGender(petGender);
        postDataModel.setDescription(petDescription);
        postDataModel.setLocation(address);
        postDataModel.setImageUrls(imageUrls);
        postDataModel.setCategory(categoryName);
        postDataModel.setKey(key);
        postDataModel.setuId(firebaseUser.getUid());
        postDataModel.setLatitude(latitude);
        postDataModel.setLongitude(longitude);


        fs.collection("posts").document(key).set(postDataModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showToast(AddPostActivity.this, "Post added successfully");
                invisibleDialog();
                finish();
            }
        }).addOnFailureListener(e -> {

        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void visibleDialog() {
        dialog = new Dialog(AddPostActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void invisibleDialog() {
        dialog.dismiss();
    }
}