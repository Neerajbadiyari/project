package com.example.myapplication;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {

    private Geocoder geocoder;

    public LocationHelper(Geocoder geocoder) {
        this.geocoder = geocoder;
    }

    public LatLng getLatLngFromAddress(String addressStr) throws IOException {
        List<Address> addresses = geocoder.getFromLocationName(addressStr, 1);
        if (addresses != null && !addresses.isEmpty()) {
            Address address = addresses.get(0);
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            return new LatLng(latitude, longitude);
        } else {
            return null; // Address not found
        }
    }
}

