package com.example.findpet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.findpet.activities.PublicPostDetailsActivity;
import com.example.findpet.adapters.AllPostAdapter;
import com.example.findpet.databinding.FragmentFavouriteBinding;
import com.example.findpet.dp.SQLiteHelper;
import com.example.findpet.interfaces.CustomClickListener;
import com.example.findpet.models.PostDataModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class FavouriteFragment extends Fragment implements View.OnClickListener, CustomClickListener {

    private FragmentFavouriteBinding binding;
    private SQLiteHelper sqLiteHelper;
    private AllPostAdapter adapter;
    private ArrayList<PostDataModel> list;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sqLiteHelper = new SQLiteHelper(getActivity());
        binding.recyclerFavourite.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        setFavourites();
        initializedClickListeners();

        return view;
    }

    private void initializedClickListeners() {

    }

    @Override
    public void onClick(View v) {
    }

    private void setFavourites() {
        // Get keys from Firestore
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection("posts").addSnapshotListener((value, error) -> {
            if (error != null) {
                // Handle error
                return;
            }

            list = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(value).getDocuments()) {
                String key = documentSnapshot.getString("key");
                // Read from SQLite based on Firestore key
                String postData = sqLiteHelper.readPost(key);
                if (postData != null) {
                    // Add matching posts to list
                    list.add(documentSnapshot.toObject(PostDataModel.class));
                }
            }
            // Update RecyclerView after collecting all matching posts
            adapter = new AllPostAdapter(getActivity(), list, FavouriteFragment.this);
            binding.recyclerFavourite.setAdapter(adapter);
        });
    }

    @Override
    public void onItemClick(int position, PostDataModel model) {
        Intent intent = new Intent(getActivity(), PublicPostDetailsActivity.class);
        intent.putExtra("data", model);
        startActivity(intent);
    }
}
