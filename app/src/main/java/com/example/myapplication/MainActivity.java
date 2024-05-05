package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView toLocTextView = findViewById(R.id.to_loc);
        TextView fromLocTextView = findViewById(R.id.from_loc);
        TextView btn_hostipal = findViewById(R.id.btn_hostipal);
        sharedPreferences = getSharedPreferences("to_Location", Context.MODE_PRIVATE);
        sharedPreferences2 = getSharedPreferences("from_Location", Context.MODE_PRIVATE);
        double to_latitude = Double.parseDouble(sharedPreferences.getString("location_lat", "0.0"));
        double to_longitude = Double.parseDouble(sharedPreferences.getString("location_lng", "0.0"));
        String to_name = sharedPreferences.getString("location_name", "");
        double from_latitude = Double.parseDouble(sharedPreferences2.getString("location_lat", "0.0"));
        double from_longitude = Double.parseDouble(sharedPreferences2.getString("location_lng", "0.0"));
        String from_name = sharedPreferences2.getString("location_name", "");

        toLocTextView.setText(to_name);
        fromLocTextView.setText(from_name);

        toLocTextView.setOnClickListener(view -> {
            // Handle click for "To Locations" TextView
            Intent intent = new Intent(MainActivity.this, LocationSearch.class);
            intent.putExtra("click", "to");
            startActivity(intent);
        });

        fromLocTextView.setOnClickListener(view -> {
            // Handle click for "From Locations" TextView
            Intent intent = new Intent(MainActivity.this, LocationSearch.class);
            intent.putExtra("click", "from");
            startActivity(intent);
        });
        btn_hostipal.setOnClickListener(view -> {
            // Handle click for "From Locations" TextView
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("to_lat", to_latitude);
            intent.putExtra("to_lng", to_longitude);
            intent.putExtra("from_lat", from_latitude);
            intent.putExtra("from_lng", from_longitude);
            startActivity(intent);
        });
    }
}
