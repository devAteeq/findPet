package com.example.findpet.activities;

import static com.example.findpet.utils.Constants.ERRORS;
import static com.example.findpet.utils.Utils.showToast;
import static java.util.jar.Pack200.Packer.ERROR;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.findpet.R;
import com.example.findpet.databinding.ActivitySignUpBinding;
import com.example.findpet.models.ProfileDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySignUpBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    Uri imageUri;
    Dialog dialog;
    FirebaseFirestore fs;
    String password;
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            Intent data = o.getData();

            if (o.getResultCode() == RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();
                    try {
                        Picasso.get().load(imageUri).into(binding.imgProfile);
                    } catch (Exception e) {
                        Log.d(ERROR, "onActivityResult: " + e.getMessage());
                    }
                }
            }
        }
    });
    ActivityResultLauncher<String> permission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), o -> {
        if (!o) {
            // permission denied
            showToast(SignUpActivity.this, "Permission denied");
        } else {
            pickImage();
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        fs = FirebaseFirestore.getInstance();


        initializedClickListeners();

    }

    private void initializedClickListeners() {
        binding.imgEdit.setOnClickListener(this);
        binding.btnFinish.setOnClickListener(this);
        binding.txtSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgEdit) {
            // checkPermission();
            pickImage();
           /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
            else {
                checkPermission();
            }*/


        } else if (v.getId() == R.id.btnFinish) {
            visibleDialog();
            String name = Objects.requireNonNull(binding.edtName.getText()).toString().trim();
            String phone = Objects.requireNonNull(binding.edtPhone.getText()).toString().trim();
            password = Objects.requireNonNull(binding.edtPassword.getText()).toString().trim();
            String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();


            if (isValid(name, phone, password, email)) {
                editTextEmpty();
                createUserWithEmailAndPassword(name, email, password, phone);
            }
        } else if (v.getId() == R.id.txtSignIn) {
            startActivity(new Intent(this, SignInActivity.class));
        }
    }

    private boolean isValid(String name, String phone, String password, String email) {
        if (imageUri == null) {
            showToast(this, "Please select an image");
            invisibleDialog();
            return false;
        } else if (name.isEmpty()) {
            binding.edtName.setError("Name is required");
            invisibleDialog();
            return false;
        } else if (phone.isEmpty()) {
            binding.edtPhone.setError("Phone is required");
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
        } else if (email.isEmpty()) {
            binding.edtEmail.setError("Email is required");
            invisibleDialog();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.setError("Please enter a valid email address");
            invisibleDialog();
            return false;
        } else {
            return true;
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

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

    private void uploadImage(String name, String phone, String email, FirebaseUser user) {
        if (imageUri != null) {
            FirebaseStorage fs = FirebaseStorage.getInstance();
            StorageReference sr = fs.getReference("profileImages").child(user.getUid());
            sr.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    sr.getDownloadUrl().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            uploadDataInDB(task1.getResult().toString(), name, phone, email, user);
                        }
                    });
                }
            });
        }
    }

    private void uploadDataInDB(String uri, String name, String phone, String email, FirebaseUser user) {
        ProfileDataModel dataModel = new ProfileDataModel(name, phone, email, user.getUid(), uri);

        fs.collection("profile").document(user.getUid()).set(dataModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                invisibleDialog();
                goToHomeScreen();

                finish();
            }
        }).addOnFailureListener(e -> Log.d(ERROR, "onFailure: " + e.getMessage()));

    }

    private void goToHomeScreen() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void createUserWithEmailAndPassword(String name, String email, String password, String phone) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser fu = mAuth.getCurrentUser();
                uploadImage(name, phone, email, fu);
            } else {
                Log.d(ERRORS, "createUserWithEmail:failure", task.getException());
                invisibleDialog();
            }
        });
    }

    private void editTextEmpty() {
        binding.edtName.setText("");
        binding.edtPhone.setText("");
        binding.edtPassword.setText("");
        binding.edtEmail.setText("");
    }
}