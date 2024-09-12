package com.example.findpet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.findpet.R;
import com.example.findpet.activities.PublicPostDetailsActivity;
import com.example.findpet.activities.SearchFilterActivity;
import com.example.findpet.activities.ShowCategoryPostsActivity;
import com.example.findpet.adapters.AllPostAdapter;
import com.example.findpet.adapters.BannerAdapter;
import com.example.findpet.adapters.CategoryAdapter;
import com.example.findpet.databinding.FragmentHomeBinding;
import com.example.findpet.interfaces.CategoryDataInterface;
import com.example.findpet.interfaces.CustomClickListener;
import com.example.findpet.models.BannerModel;
import com.example.findpet.models.CategoryModel;
import com.example.findpet.models.PostDataModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment implements View.OnClickListener, CustomClickListener, CategoryDataInterface {

    FragmentHomeBinding binding;
    BannerAdapter bannerAdapter;
    ArrayList<BannerModel> list;
    FirebaseFirestore firestore;
    ArrayList<CategoryModel> categoryList;
    CategoryAdapter categoryAdapter;
    ArrayList<PostDataModel> dataModelArrayList;
    AllPostAdapter allPostAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        binding.recyclerBanner.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerCategories.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        binding.recyclerAllPost.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        firestore = FirebaseFirestore.getInstance();

        initializedClickListeners();
        setBanner();
        setCategories();
        setAllPosts();

        return binding.getRoot();
    }

    private void initializedClickListeners() {
        binding.imgSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgSearch) {
            Intent intent = new Intent(getActivity(), SearchFilterActivity.class);
            startActivity(intent);
        }
    }

    private void setBanner() {
        firestore.collection("banner").addSnapshotListener((value, error) -> {
            list = new ArrayList<>();
            List<DocumentSnapshot> snapshots = Objects.requireNonNull(value).getDocuments();
            for (DocumentSnapshot documentSnapshot : snapshots) {
                if (documentSnapshot.exists()) {
                    list.add(documentSnapshot.toObject(BannerModel.class));
                    bannerAdapter = new BannerAdapter(getActivity(), list);
                    binding.recyclerBanner.setAdapter(bannerAdapter);
                }
            }

        });
    }

    private void setCategories() {
        firestore.collection("categories").addSnapshotListener((value, error) -> {
            categoryList = new ArrayList<>();
            List<DocumentSnapshot> snapshots = Objects.requireNonNull(value).getDocuments();
            for (DocumentSnapshot documentSnapshot : snapshots) {
                if (documentSnapshot.exists()) {
                    categoryList.add(documentSnapshot.toObject(CategoryModel.class));
                    categoryAdapter = new CategoryAdapter(categoryList, getActivity(), this);
                    binding.recyclerCategories.setAdapter(categoryAdapter);
                }
            }
        });
    }

    private void setAllPosts() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("posts").addSnapshotListener((value, error) -> {
            dataModelArrayList = new ArrayList<>();
            List<DocumentSnapshot> snapshots = Objects.requireNonNull(value).getDocuments();
            for (DocumentSnapshot documentSnapshot : snapshots) {
                if (documentSnapshot.exists()) {
                    dataModelArrayList.add(documentSnapshot.toObject(PostDataModel.class));
                    allPostAdapter = new AllPostAdapter(getActivity(), dataModelArrayList, HomeFragment.this);
                    binding.recyclerAllPost.setAdapter(allPostAdapter);
                }
            }
        });
    }

    @Override
    public void onItemClick(int position, PostDataModel model) {
        Intent intent = new Intent(getActivity(), PublicPostDetailsActivity.class);
        intent.putExtra("data", model);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position, CategoryModel model) {
        Intent intent = new Intent(getActivity(), ShowCategoryPostsActivity.class);
        intent.putExtra("data", model);
        startActivity(intent);
    }
}