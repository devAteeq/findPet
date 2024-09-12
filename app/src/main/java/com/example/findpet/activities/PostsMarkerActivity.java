package com.example.findpet.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.findpet.R;
import com.example.findpet.databinding.ActivityPostsMarkerBinding;
import com.example.findpet.models.PostDataModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PostsMarkerActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private final Map<String, Marker> markers = new HashMap<>();// Store markers with post key as ID
    PostDataModel postDataModel;
    private ActivityPostsMarkerBinding binding;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostsMarkerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        initializedClickListener();
    }

    private void initializedClickListener() {
        binding.btnCancelMap.setOnClickListener(this);
        binding.btnSeePostMap.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCancelMap) {
            binding.cardPostMap.setVisibility(View.GONE);
        } else if (v.getId() == R.id.btnSeePostMap) {
            Intent intent = new Intent(this, PublicPostDetailsActivity.class);
            intent.putExtra("data", postDataModel);
            startActivity(intent);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        addMarkers();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                String postKey = marker.getSnippet();
                postDataModel = (PostDataModel) Objects.requireNonNull(markers.get(postKey)).getTag(); // Retrieve PostDataModel

                if (postDataModel != null) {

                    binding.cardPostMap.setVisibility(View.VISIBLE);

                    if (postDataModel.getImageUrls() != null && !postDataModel.getImageUrls().isEmpty()) {
                        Picasso.get().load(postDataModel.getImageUrls().get(0)).placeholder(R.color.light_gray).into(binding.imgPostMap);
                    }

                    binding.postNameMap.setText("Name: " + postDataModel.getName());
                    binding.postAgeMap.setText("Age: " + postDataModel.getAge());
                    binding.postGenderMap.setText("Gender: " + postDataModel.getGender());

                }

                return true;
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void addPin(PostDataModel postDataModel) {
        LatLng latLng = new LatLng(postDataModel.getLatitude(), postDataModel.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).snippet(postDataModel.getKey()) // Use post key as snippet
                .title(postDataModel.getName());

        Marker marker = mMap.addMarker(markerOptions);
        if (marker != null) {
            marker.setTag(postDataModel); // Store PostDataModel with the marker
            markers.put(postDataModel.getKey(), marker); // Store marker in the map
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
    }

    private void addMarkers() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection("posts").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d("ERRORS", "Error fetching posts: " + error.getMessage());
                return;
            }

            if (value != null) {
                List<DocumentSnapshot> snapshots = value.getDocuments();
                for (DocumentSnapshot documentSnapshot : snapshots) {
                    PostDataModel postDataModel = documentSnapshot.toObject(PostDataModel.class);
                    if (postDataModel != null) {
                        addPin(postDataModel);
                    }
                }
            }
        });
    }

}