package com.example.findpet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.findpet.R;
import com.example.findpet.adapters.PostAdapter;
import com.example.findpet.databinding.ActivityMyProfileBinding;
import com.example.findpet.interfaces.CustomClickListener;
import com.example.findpet.models.PostDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener, CustomClickListener {

    ActivityMyProfileBinding binding;
    FirebaseFirestore fs;
    FirebaseAuth auth;
    FirebaseUser user;
    ArrayList<PostDataModel> postList;
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fs = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        initializedClickListeners();
        setData();
        setPosts();


    }

    private void initializedClickListeners() {
        binding.imgBack.setOnClickListener(this);
        binding.imgMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            finish();
        } else if (v.getId() == R.id.imgMenu) {
            PopupMenu popupMenu = new PopupMenu(this, binding.imgMenu, Gravity.END);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, R.string.edit_profile);
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == 0) {
                    startActivity(new Intent(MyProfileActivity.this, EditProfileActivity.class));
                }

                return true;
            });
        }
    }

    private void setData() {
        fs.collection("profile").addSnapshotListener((value, error) -> {
            List<DocumentSnapshot> snapshots = Objects.requireNonNull(value).getDocuments();
            for (DocumentSnapshot snapshot : snapshots) {
                if (snapshot.exists()) {
                    if (Objects.equals(snapshot.getString("uId"), user.getUid())) {
                        binding.txtUserName.setText(snapshot.getString("name"));
                        binding.txtUserEmail.setText(snapshot.getString("email"));
                        Picasso.get().load(snapshot.getString("imageUrl")).placeholder(R.color.light_gray).into(binding.imgProfile);
                    }
                }
            }
        });
    }

    private void setPosts() {
        binding.postsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("posts").addSnapshotListener((value, error) -> {
            postList = new ArrayList<>();
            List<DocumentSnapshot> snapshots = Objects.requireNonNull(value).getDocuments();
            for (DocumentSnapshot snapshot : snapshots) {
                if (snapshot.exists()) {
                    if (Objects.equals(snapshot.getString("uId"), user.getUid())) {
                        PostDataModel postDataModel = snapshot.toObject(PostDataModel.class);
                        postList.add(postDataModel);
                        postAdapter = new PostAdapter(MyProfileActivity.this, postList, MyProfileActivity.this);
                        binding.postsRecyclerView.setAdapter(postAdapter);
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(int position, PostDataModel model) {
        Intent intent = new Intent(this, MyPostDetailsActivity.class);
        intent.putExtra("postDetails", model);
        startActivity(intent);

    }
}