package com.example.findpet.activities;

import static com.example.findpet.utils.Constants.ERRORS;
import static com.example.findpet.utils.Utils.showToast;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.findpet.R;
import com.example.findpet.adapters.CommentsAdapter;
import com.example.findpet.adapters.SetImagesViewPagerAdapter;
import com.example.findpet.databinding.ActivityPublicPostDetailsBinding;
import com.example.findpet.dp.SQLiteHelper;
import com.example.findpet.interfaces.CommentDelete;
import com.example.findpet.models.CommentsModel;
import com.example.findpet.models.PostDataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PublicPostDetailsActivity extends AppCompatActivity implements View.OnClickListener, CommentDelete {

    private static final String TAG = "PublicPostDetailsActivity";
    ActivityPublicPostDetailsBinding binding;
    PostDataModel postDataModel;
    SetImagesViewPagerAdapter setImagesViewPagerAdapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();
    String imageUrl;
    String name, key,phone;
    ArrayList<CommentsModel> list;
    CommentsAdapter commentsAdapter;
    CommentsModel commentsModel;
    SQLiteHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPublicPostDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        postDataModel = (PostDataModel) getIntent().getSerializableExtra("data");
        binding.recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        key = postDataModel.getKey();
        helper = new SQLiteHelper(this);

        initializedClickListeners();
        setData();
        setCommentYourPic();
        showAllComments();
        checkFavourite();
    }

    private void initializedClickListeners() {
        binding.imgBack.setOnClickListener(this);
        binding.btnSendComment.setOnClickListener(this);
        binding.imgFavorite.setOnClickListener(this);
        binding.btnAdopt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            finish();
        } else if (v.getId() == R.id.btnSendComment) {
            String comment = binding.edtComment.getText().toString();
            if (comment.isEmpty()) {
                Toast.makeText(this, "Please enter comment", Toast.LENGTH_SHORT).show();
            } else {
                addComment(comment);
            }
        } else if (v.getId() == R.id.imgFavorite) {
            toggleFavorite();
        } else if (v.getId() == R.id.btnAdopt) {
            adoptionContact();
        }
    }

    private void setData() {
        binding.postNamePublic.setText(postDataModel.getName());
        binding.postCategoryPublic.setText(postDataModel.getCategory());
        binding.postGenderPublic.setText(postDataModel.getGender());
        binding.postAgePublic.setText(postDataModel.getAge());
        binding.postDescPublic.setText(postDataModel.getDescription());
        binding.postLocationPublic.setText(postDataModel.getLocation());

        setImagesViewPagerAdapter = new SetImagesViewPagerAdapter(postDataModel.getImageUrls());
        binding.viewPagerPostsPublic.setAdapter(setImagesViewPagerAdapter);
        binding.viewPagerPostsPublic.setClipToPadding(false);
        binding.viewPagerPostsPublic.setClipChildren(false);
        binding.viewPagerPostsPublic.setOffscreenPageLimit(2);
        binding.viewPagerPostsPublic.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        binding.circleIndicatorPostPublic.setViewPager(binding.viewPagerPostsPublic);
        setImagesViewPagerAdapter.registerAdapterDataObserver(binding.circleIndicatorPostPublic.getAdapterDataObserver());
    }

    private void setCommentYourPic() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection("profile").addSnapshotListener((value, error) -> {
            List<DocumentSnapshot> snapshots = Objects.requireNonNull(value).getDocuments();
            for (DocumentSnapshot documentSnapshot : snapshots) {
                if (documentSnapshot.exists()) {
                    if (Objects.equals(documentSnapshot.getString("uId"), firebaseUser.getUid())) {
                        name = documentSnapshot.getString("name");
                        imageUrl = documentSnapshot.getString("imageUrl");
                        phone = documentSnapshot.getString("phone");

                        Picasso.get().load(imageUrl).placeholder(R.color.light_gray).into(binding.imgYourComment);
                    }
                }
            }
        });
    }

    private void addComment(String comment) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        commentsModel = new CommentsModel();
        String key = fs.collection("posts").document(postDataModel.getKey()).collection("comments").document().getId();
        commentsModel.setComment(comment);
        commentsModel.setKey(key);
        commentsModel.setuId(firebaseUser.getUid());
        commentsModel.setName(name);
        commentsModel.setImageUrl(imageUrl);

        fs.collection("posts").document(postDataModel.getKey()).collection("comments").document(key).set(commentsModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                binding.edtComment.setText("");
                showToast(PublicPostDetailsActivity.this, "Comment added successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(ERRORS, "onFailure: Comments Not Added" + e.getMessage());
            }
        });
    }

    private void showAllComments() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection("posts").document(postDataModel.getKey()).collection("comments").addSnapshotListener((value, error) -> {
            List<DocumentSnapshot> snapshots = Objects.requireNonNull(value).getDocuments();
            list = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : snapshots) {
                if (documentSnapshot.exists()) {
                    CommentsModel commentsModel = documentSnapshot.toObject(CommentsModel.class);
                    list.add(commentsModel);
                }
            }
            commentsAdapter = new CommentsAdapter(list, PublicPostDetailsActivity.this, PublicPostDetailsActivity.this);
            binding.recyclerComments.setAdapter(commentsAdapter);
        });
    }

    @Override
    public void deleteComment(String key) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("Delete Comment?").setMessage("Are you sure you want to delete this comment?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore fs = FirebaseFirestore.getInstance();
                fs.collection("posts").document(postDataModel.getKey()).collection("comments").document(key).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showToast(PublicPostDetailsActivity.this, "Comment deleted successfully");
                        }
                    }
                });
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        dialog.show();
    }

    void addDataInDB() {
        long result = helper.insertPost(postDataModel.getKey());
        if (result != -1) {
            Toast.makeText(this, "Added to Favourite", Toast.LENGTH_SHORT).show();
            binding.imgFavorite.setImageResource(R.drawable.heart);
        } else {
            Log.d(TAG, "addDataInDB: Failed to insert data. Result: " + result);
        }
    }

    private void toggleFavorite() {
        String key = helper.readPost(postDataModel.getKey());
        if (key != null) {
            removeFavorite();
        } else {
            addDataInDB();
        }
    }

    private void removeFavorite() {
        helper.removedFavourite(postDataModel.getKey());
        Toast.makeText(this, "Removed from Favourite", Toast.LENGTH_SHORT).show();
        binding.imgFavorite.setImageResource(R.drawable.icon_favorite);
    }

    private void checkFavourite() {
        String key = helper.readPost(postDataModel.getKey());
        if (key != null) {
            binding.imgFavorite.setImageResource(R.drawable.heart);
        } else {
            binding.imgFavorite.setImageResource(R.drawable.icon_favorite);
        }
    }
    private void adoptionContact() {
        Uri uri = Uri.parse("smsto:" + phone);
        Intent intentWhatsApp = new Intent(Intent.ACTION_SENDTO, uri);
        intentWhatsApp.setPackage("com.whatsapp");
        startActivity(intentWhatsApp);
    }
}
