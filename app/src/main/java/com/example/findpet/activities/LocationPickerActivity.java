package com.example.findpet.activities;

import static com.example.findpet.utils.Constants.TAG;
import static com.example.findpet.utils.Utils.showToast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findpet.R;
import com.example.findpet.databinding.ActivityLocationPickerBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;
import java.util.Objects;

public class LocationPickerActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final int DEFAULT_ZOOM = 15;
    ActivityLocationPickerBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap map = null;
    private Location mLastKnownLocation = null;
    private Double selectedLatitude = null;
    private Double selectedLongitude = null;
    private String selectedAddress = "";
    ActivityResultLauncher<String> requestLocationPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @SuppressLint("MissingPermission")
        @Override
        public void onActivityResult(Boolean isGranted) {
            Log.d(TAG, "onActivityResult: ");
            if (isGranted) {
                pickCurrentPlace();
            } else {
                showToast(LocationPickerActivity.this, "Location Permission Denied");
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationPickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializedClickListeners();

        binding.doneLayout.setVisibility(View.GONE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        // initialize the places client
        Places.initialize(this, ("AIzaSyAvafPm6yDZIb1rXNOaiGzMjR9vcJ9qd70"));

        PlacesClient placesClient = Places.createClient(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void initializedClickListeners() {
        binding.imgBack.setOnClickListener(this);
        binding.imgGps.setOnClickListener(this);
        binding.doneLayout.setOnClickListener(this);
        binding.btnDone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            finish();
        } else if (v.getId() == R.id.imgGps) {
            if (isGpsEnabled()) {
                requestLocationPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                showToast(this, "Please enable GPS");
            }
        } else if (v.getId() == R.id.btnDone) {
            Intent intent = new Intent();
            intent.putExtra("latitude", selectedLatitude);
            intent.putExtra("longitude", selectedLongitude);
            intent.putExtra("address", selectedAddress);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");

        map = googleMap;

        requestLocationPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);

        map.setOnMapClickListener(latLng -> {
            selectedLatitude = latLng.latitude;
            selectedLongitude = latLng.longitude;

            Log.d(TAG, "onMapClick: selectedLatitude" + selectedLatitude);
            Log.d(TAG, "onMapClick: selectedLongitude" + selectedLongitude);

            addressFromLatLng(latLng);
        });
    }

    private void addressFromLatLng(LatLng latLng) {
        Log.d(TAG, "addressFromLatLng: ");

        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address address = Objects.requireNonNull(addressList).get(0);

            String addressLine = address.getAddressLine(0);
            String subLocality = address.getSubLocality();

            selectedAddress = addressLine;
            addMarker(latLng, subLocality, addressLine);
        } catch (Exception e) {
            Log.d(TAG, "addressFromLatLng: " + e.getMessage());
        }
    }

    private void pickCurrentPlace() {
        Log.d(TAG, "pickCurrentPlace: ");
        if (map == null) {
            return;
        }
        detectAndShowDeviceLocationMap();
    }

    @SuppressLint("MissingPermission")
    private void detectAndShowDeviceLocationMap() {
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnSuccessListener(location -> {
                if (location != null) {
                    mLastKnownLocation = location;
                    selectedLatitude = location.getLatitude();
                    selectedLongitude = location.getLongitude();

                    Log.d(TAG, "onSuccess: selectedLatitude" + selectedLatitude);
                    Log.d(TAG, "onSuccess: selectedLongitude" + selectedLongitude);

                    LatLng latLng = new LatLng(selectedLatitude, selectedLongitude);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                    map.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));

                    addressFromLatLng(latLng);
                } else {
                    Log.d(TAG, "onSuccess: location is null");
                }
            }).addOnFailureListener(e -> {

            });
        } catch (Exception e) {
            Log.d(TAG, "detectAndShowDeviceLocationMap: " + e.getMessage());
        }
    }

    private boolean isGpsEnabled() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            Log.d(TAG, "isGpsEnabled: " + e.getMessage());
        }
        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            Log.d(TAG, "isGpsEnabled: " + e.getMessage());
        }
        return !(!gpsEnabled && !networkEnabled);
    }

    private void addMarker(LatLng latLng, String title, String selectedAddress) {
        Log.d(TAG, "onPlaceSelected: title" + title);
        Log.d(TAG, "onPlaceSelected: latitude" + latLng.latitude);
        Log.d(TAG, "onPlaceSelected: longitude" + latLng.longitude);
        Log.d(TAG, "onPlaceSelected: address" + selectedAddress);

        map.clear();

        try {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(title);
            markerOptions.snippet(selectedAddress);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            map.addMarker(markerOptions);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

            binding.doneLayout.setVisibility(View.VISIBLE);
            binding.txtSelectedLocation.setText(selectedAddress);

        } catch (Exception e) {
            Log.d(TAG, "addMarker: " + e.getMessage());
        }
    }
}