package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private double pickupLatitude1;
    private double pickupLongitude1;
    private double pickupLatitude2;
    private double pickupLongitude2;
    private int radius = 1500;
    private String apiKey = "AIzaSyC0a7wnEltBsNzTFbRTspR3jCmtAIQjnFQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Retrieve coordinates from intent
        Intent intent = getIntent();
        pickupLatitude1 = intent.getDoubleExtra("to_lat", 0.0);
        pickupLongitude1 = intent.getDoubleExtra("to_lng", 0.0);
        pickupLatitude2 = intent.getDoubleExtra("from_lat", 0.0);
        pickupLongitude2 = intent.getDoubleExtra("from_lng", 0.0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Move camera to initial position (e.g., your location)
        LatLng initialLocation = new LatLng(pickupLatitude1, pickupLongitude1);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f));

        // Construct the request URLs for the two LatLng points
        String url1 = getUrl(pickupLatitude1, pickupLongitude1);
        String url2 = getUrl(pickupLatitude2, pickupLongitude2);

        // Retrieve and display common hospital names
        getCommonHospitalNames(url1, url2);
    }

    private String getUrl(double latitude, double longitude) {
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + latitude + "," + longitude
                + "&radius=" + radius
                + "&type=hospital"
                + "&key=" + apiKey;
    }

    private void getCommonHospitalNames(String url1, String url2) {
        new Thread(() -> {
            try {
                // Retrieve hospital names from the first URL
                Set<String> hospitalNames1 = getHospitalNames(url1);

                // Retrieve hospital names from the second URL
                Set<String> hospitalNames2 = getHospitalNames(url2);

                // Find the common hospital names
                Set<String> commonHospitalNames = new HashSet<>(hospitalNames1);
                commonHospitalNames.retainAll(hospitalNames2);

                // Display common hospital names
                runOnUiThread(() -> {
                    for (String name : commonHospitalNames) {
                        // Print the common hospital names
                        System.out.println(name);
                    }
                });

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Set<String> getHospitalNames(String url) throws IOException, JSONException {
        Set<String> hospitalNames = new HashSet<>();

        // Create a URL object from the given URL string
        URL apiUrl = new URL(url);

        // Open a connection to the URL
        HttpURLConnection urlConnection = (HttpURLConnection) apiUrl.openConnection();

        // Set request method
        urlConnection.setRequestMethod("GET");

        // Connect to the URL
        urlConnection.connect();

        // Get the input stream from the connection
        InputStream inputStream = urlConnection.getInputStream();

        // Read the input stream and convert it to a String
        String jsonResponse = readInputStream(inputStream);

        // Parse the JSON response
        JSONObject jsonObject = new JSONObject(jsonResponse);

        // Check if the response contains a 'results' array
        if (jsonObject.has("results")) {
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            // Loop through the results array
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject resultObject = resultsArray.getJSONObject(i);

                // Extract hospital name
                String name = resultObject.getString("name");
                hospitalNames.add(name);

                // Extract hospital location and add marker on the map
                JSONObject geometry = resultObject.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");
                LatLng hospitalLatLng = new LatLng(lat, lng);
                runOnUiThread(() -> {
                    mMap.addMarker(new MarkerOptions().position(hospitalLatLng).title(name));
                });
            }
        }

        // Close the input stream and disconnect the connection
        inputStream.close();
        urlConnection.disconnect();

        return hospitalNames;
    }

    private String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }
}
