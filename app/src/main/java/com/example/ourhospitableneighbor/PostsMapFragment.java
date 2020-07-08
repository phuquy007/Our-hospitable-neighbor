package com.example.ourhospitableneighbor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.ourhospitableneighbor.helper.Debouncer;
import com.example.ourhospitableneighbor.model.Post;
import com.example.ourhospitableneighbor.view.PanelView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PostsMapFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private PanelView panel;

    private Debouncer showPostsDebouncer = new Debouncer();
    private FusedLocationProviderClient locationClient;

    private boolean loadingPostsFirstTime = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts_map, container, false);
        panel = rootView.findViewById(R.id.panel);
        locationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));
        addMapFragment();
        return rootView;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Reload the map so my location button works
                addMapFragment();
            }
        }
    }

    private void onMapReady(GoogleMap map) {
        this.map = map;

        Context ctx = this.getActivity();
        assert ctx != null;

        if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationClient.getLastLocation().addOnSuccessListener(location -> {
                PostService.getInstance().setUserCurrentLocation(location);
                showPostsInAreaDebounced();
            });
            setUpMyLocationButton();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        map.setOnCameraIdleListener(this::showPostsInAreaDebounced);
        map.getUiSettings().setMapToolbarEnabled(false);

        setUpCompassButton();
        setInitialViewPoint();
        showAllPostMarkers();
    }

    private void setInitialViewPoint() {
        // Somewhere in New West
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(49.2156, -122.9177), 14);
        map.moveCamera(update);
    }

    private void addMapFragment() {
        mapFragment = new SupportMapFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment).commit();
        mapFragment.getMapAsync(this::onMapReady);
    }

    @SuppressLint("MissingPermission")
    private void setUpMyLocationButton() {
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        View mapView = mapFragment.getView();
        if (mapView == null) return;

        View locationButton = mapView.findViewWithTag("GoogleMapMyLocationButton");
        if (locationButton == null) return;

        View toolbar = getToolbar();
        if (toolbar == null) return;

        // Convert dp to px
        int padding = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10,
                getResources().getDisplayMetrics())
        );
        toolbar.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                    RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                    rlp.setMargins(0, bottom + padding, 0, 0);
                    locationButton.setLayoutParams(rlp);
                }
        );
    }

    private void setUpCompassButton() {
        View mapView = mapFragment.getView();
        if (mapView == null) return;

        View compassButton = mapView.findViewWithTag("GoogleMapCompass");
        if (compassButton == null) return;

        View toolbar = getToolbar();
        if (toolbar == null) return;

        // Convert dp to px
        int padding = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10,
                getResources().getDisplayMetrics())
        );
        toolbar.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                    RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) compassButton.getLayoutParams();
                    rlp.setMargins(0, bottom + padding, 0, 0);
                    compassButton.setLayoutParams(rlp);
                }
        );
    }

    private void showPostsInAreaDebounced() {
        showPostsDebouncer.debounce(this::showPostsInArea, 200, TimeUnit.MILLISECONDS);
    }

    @SuppressLint("MissingPermission")
    private void showPostsInArea() {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
            PostService.getInstance().getPostsInArea(bounds).thenAccept(posts -> getActivity().runOnUiThread(() -> {
                panel.setPosts(posts);
                if (loadingPostsFirstTime) {
                    panel.setCollapse(false, true);
                    loadingPostsFirstTime = false;
                }
            }));
        });
    }

    private void showAllPostMarkers() {
        PostService.getInstance().getAllPosts().thenAccept(posts -> {
            map.clear();
            for (Post post : posts) {
                map.addMarker(createMarkerFromPost(post));
            }
        });
    }

    private MarkerOptions createMarkerFromPost(Post post) {
        return new MarkerOptions()
                .position(new LatLng(post.getLatitude(), post.getLongitude()))
                .title(post.getPostTitle());
    }

    private View getToolbar() {
        Activity activity = getActivity();
        if (activity == null) return null;
        return activity.findViewById(R.id.toolbar_main);
    }
}