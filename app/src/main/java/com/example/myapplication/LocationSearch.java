package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class LocationSearch extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String clickValue = extras != null ? extras.getString("click") : null;

        if (clickValue != null) {
            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), "AIzaSyC0a7wnEltBsNzTFbRTspR3jCmtAIQjnFQ");
            }

            // Create a new Places client instance
            Places.createClient(this);

            // Initialize AutocompleteSupportFragment
            // Initialize the AutocompleteSupportFragment.
            AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                    getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

            // Specify the types of place data to return.
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));


            // Set up a PlaceSelectionListener to handle the user's selection
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    // Handle the selected place
                    if (clickValue.equals("to")) {


                        // Create a Geocoder object
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

// Create LocationHelper instance with the Geocoder object
                        LocationHelper locationHelper = new LocationHelper(geocoder);

// Provide the address string

// Get the LatLng from the address
                        try {
                            LatLng latLng = locationHelper.getLatLngFromAddress(place.getName());
                            if (latLng != null) {
                                double latitude = latLng.latitude;
                                double longitude = latLng.longitude;
                                String toLocationName = place.getName();

                                sharedPreferences = getSharedPreferences("to_Location", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("location_lat", String.valueOf(latitude));
                                editor.putString("location_lng", String.valueOf(longitude));
                                editor.putString("location_name", toLocationName);
                                editor.apply();
                                // Use latitude and longitude as needed
                            } else {
                                // Address not found
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                       // Double toLocation_lat = Objects.requireNonNull(place.getLatLng()).latitude;
                        //Double toLocation_lng = place.getLatLng().longitude;



                    } else {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

// Create LocationHelper instance with the Geocoder object
                        LocationHelper locationHelper = new LocationHelper(geocoder);

// Provide the address string

// Get the LatLng from the address
                        try {
                            LatLng latLng = locationHelper.getLatLngFromAddress(place.getName());
                            if (latLng != null) {
                                double latitude = latLng.latitude;
                                double longitude = latLng.longitude;
                                String fromLocationName = place.getName();

                                sharedPreferences2 = getSharedPreferences("from_Location", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences2.edit();
                                editor.putString("location_lat", String.valueOf(latitude));
                                editor.putString("location_lng", String.valueOf(longitude));
                                editor.putString("location_name", fromLocationName);
                                editor.apply();
                                // Use latitude and longitude as needed
                            } else {
                                // Address not found
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onError(com.google.android.gms.common.api.Status status) {
                    // Handle the error
                    Toast.makeText(
                            getApplicationContext(),
                            "An error occurred: " + status,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }
    }
}
