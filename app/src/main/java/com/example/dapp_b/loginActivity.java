package com.example.dapp_b;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class loginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvError;

    private final String VALID_USERNAME = "damodar reddy";
    private final String VALID_PASSWORD = "521012";

    private FusedLocationProviderClient fusedLocationClient;
    private boolean isLoginAttempted = false;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getLocationAndProceed();
        } else {
            Toast.makeText(this, "Location permission required to log in", Toast.LENGTH_LONG).show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnLogin.setOnClickListener(v -> {
            isLoginAttempted = true;
            attemptLogin();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoginAttempted) {
            attemptLogin();
        }
    }

    private void attemptLogin() {
        String user = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (!user.equalsIgnoreCase(VALID_USERNAME) || !pass.equals(VALID_PASSWORD)) {
            tvError.setText("Invalid username or password");
            return;
        }

        tvError.setText(""); // Clear error message
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (isLocationEnabled()) {
                getLocationAndProceed();
            } else {
                showEnableLocationDialog();
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showEnableLocationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Enable Location")
                .setMessage("Your location settings are set to \"Off\". Please enable location to use this feature.")
                .setPositiveButton("Location Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .show();
    }

    private void getLocationAndProceed() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    Intent intent = new Intent(loginActivity.this, firstpageActivity.class);
                    intent.putExtra("lat", latitude);
                    intent.putExtra("lng", longitude);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Could not retrieve location. Please ensure GPS is enabled.", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(this, e -> {
                Log.e("loginActivity", "Error getting location", e);
                Toast.makeText(this, "Failed to get location.", Toast.LENGTH_SHORT).show();
            });
        } catch (SecurityException e) {
            Log.e("loginActivity", "Location permission error", e);
            Toast.makeText(this, "Location permission is required.", Toast.LENGTH_SHORT).show();
        }
    }
}
