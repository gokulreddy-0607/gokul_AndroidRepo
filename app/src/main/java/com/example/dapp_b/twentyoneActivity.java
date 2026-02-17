package com.example.dapp_b;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dapp_b.Sathram;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class twentyoneActivity extends AppCompatActivity {

    private static final String TAG = "twentyoneActivity";

    // ================= UI =================
    RecyclerView imageRecyclerView;
    Button btnPhoto, btnGetLocation, btnBus, btnRail, btnSubmit;

    EditText etName, etName1, etName3, etName4;
    EditText etLat, etLng, etCheckIn, etCheckOut;
    EditText etWebsite, etPhone, etDescription;

    EditText tvBusDistance, tvRailDistance;

    RatingBar ratingBar;

    RadioGroup rgStayType;
    RadioButton rb24Hours, rbCheckInOut;
    TextView tv24HoursInfo;
    LinearLayout layoutCheckInOut;
    CheckBox cbAC, cbNonAC, cbHotWater, cbParking;

    // ================= LOCATION =================
    FusedLocationProviderClient fusedLocationClient;
    double userLat, userLng;
    boolean isLocationFetched = false;

    // ================= COORDINATES (for aerial distance only) =================
    private static final double BUS_LAT = 13.629260;
    private static final double BUS_LNG = 79.426209;

    private static final double RAIL_LAT = 13.628033;
    private static final double RAIL_LNG = 79.419280;

    // ================= IMAGES =================
    private final List<Uri> imageUris = new ArrayList<>();
    private ImageAdapter imageAdapter;

    // ================= FIREBASE =================
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private Sathram existingSathram;


    // ================= IMAGE PICKER =================
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            if (result.getData().getClipData() != null) {
                                ClipData clipData = result.getData().getClipData();
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    imageUris.add(clipData.getItemAt(i).getUri());
                                }
                            } else if (result.getData().getData() != null) {
                                imageUris.add(result.getData().getData());
                            }
                            imageAdapter.notifyDataSetChanged();
                        }
                    });


    // ================= PERMISSION =================
    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (granted) {
                            getGPS();
                        } else {
                            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                        }
                    });

    private final ActivityResultLauncher<String> storagePermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openImagePicker();
                } else {
                    Toast.makeText(this, "Storage permission is required to select images", Toast.LENGTH_SHORT).show();
                }
            });


    // ================= ON CREATE =================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twentyone);

        bindViews();
        setupImageRecyclerView();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        String category = getIntent().getStringExtra("CATEGORY");
        if (category == null) {
            category = "CommunitySathrams"; // Default category
        }

        databaseReference = FirebaseDatabase.getInstance().getReference(category);

        if (getIntent().hasExtra("SATHRAM_DATA")) {
            existingSathram = (Sathram) getIntent().getSerializableExtra("SATHRAM_DATA");
            populateForm(existingSathram);
        }

        btnPhoto.setOnClickListener(v -> checkStoragePermission());
        btnGetLocation.setOnClickListener(v -> getGPS());
        btnSubmit.setOnClickListener(v -> uploadDataToFirebase());

        btnBus.setOnClickListener(v -> {
            calculateDistance(BUS_LAT, BUS_LNG, tvBusDistance, "Bus distance");
            openAddressInMaps("APSRTC Bus Stand Tirupati, Tata Nagar, Tirupati");
        });

        btnRail.setOnClickListener(v -> {
            calculateDistance(RAIL_LAT, RAIL_LNG, tvRailDistance, "Rail distance");
            openAddressInMaps("Tirupati Railway Station, Tirupati");
        });

        etCheckIn.setOnClickListener(v -> showTimePicker(etCheckIn));
        etCheckOut.setOnClickListener(v -> showTimePicker(etCheckOut));

        rgStayType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb24Hours) {
                tv24HoursInfo.setVisibility(View.VISIBLE);
                layoutCheckInOut.setVisibility(View.GONE);
            } else {
                tv24HoursInfo.setVisibility(View.GONE);
                layoutCheckInOut.setVisibility(View.VISIBLE);
            }
        });
    }

    private void populateForm(Sathram sathram) {
        etName.setText(sathram.getName());
        etName1.setText(sathram.getYear());
        etName3.setText(sathram.getStartCost());
        etName4.setText(sathram.getEndCost());

        if (sathram.getLat() != null && sathram.getLng() != null) {
            etLat.setText(String.valueOf(sathram.getLat()));
            etLng.setText(String.valueOf(sathram.getLng()));
            userLat = sathram.getLat();
            userLng = sathram.getLng();
            isLocationFetched = true;
        }

        tvBusDistance.setText(sathram.getBusDistance());
        tvRailDistance.setText(sathram.getRailDistance());
        etDescription.setText(sathram.getDescription());
        etWebsite.setText(sathram.getWebsite());
        etPhone.setText(sathram.getPhone());

        if (sathram.getRating() != null) {
            ratingBar.setRating(sathram.getRating().floatValue());
        }

        cbAC.setChecked(sathram.isAcRooms());
        cbNonAC.setChecked(sathram.isNonAcRooms());
        cbHotWater.setChecked(sathram.isHotWater());
        cbParking.setChecked(sathram.isParking());

        if ("24 Hours".equals(sathram.getStayType())) {
            rb24Hours.setChecked(true);
        } else if ("Check-in/Check-out".equals(sathram.getStayType())) {
            rbCheckInOut.setChecked(true);
            etCheckIn.setText(sathram.getCheckInTime());
            etCheckOut.setText(sathram.getCheckOutTime());
        }

        if (sathram.getImageUrls() != null && !sathram.getImageUrls().isEmpty()) {
            for (String url : sathram.getImageUrls()) {
                imageUris.add(Uri.parse(url));
            }
            imageAdapter.notifyDataSetChanged();
        }
    }


    // =========================================================
    // FIREBASE DATA UPLOAD
    // =========================================================
    private void uploadDataToFirebase() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a Residence/Hotel Name", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        String entryId;
        if (existingSathram != null) {
            entryId = existingSathram.getKey();
        } else {
            entryId = name;
        }

        if (imageUris.isEmpty()) {
            uploadFormDetails(entryId, new ArrayList<>());
        } else {
            uploadImagesAndThenFormDetails(entryId);
        }
    }

    private void uploadImagesAndThenFormDetails(String entryId) {
        List<String> uploadedImageUrls = new ArrayList<>();
        StorageReference imageFolderRef = storageReference.child("images/" + entryId);

        List<Uri> newImageUris = new ArrayList<>();
        for (Uri uri : imageUris) {
            if (uri != null && (uri.getScheme().equals("http") || uri.getScheme().equals("https"))) {
                uploadedImageUrls.add(uri.toString());
            } else {
                newImageUris.add(uri);
            }
        }

        if (newImageUris.isEmpty()) {
            uploadFormDetails(entryId, uploadedImageUrls);
            return;
        }


        for (int i = 0; i < newImageUris.size(); i++) {
            Uri imageUri = newImageUris.get(i);
            StorageReference imageRef = imageFolderRef.child(UUID.randomUUID().toString());
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                uploadedImageUrls.add(uri.toString());
                if (uploadedImageUrls.size() == (existingSathram != null ? existingSathram.getImageUrls().size() : 0) + newImageUris.size()) {
                    uploadFormDetails(entryId, uploadedImageUrls);
                }
            })).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Log.e(TAG, "Failed to get download URL", e);
                Toast.makeText(twentyoneActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void uploadFormDetails(String entryId, List<String> imageUrls) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", etName.getText().toString());
        data.put("year", etName1.getText().toString());
        data.put("startCost", etName3.getText().toString());
        data.put("endCost", etName4.getText().toString());
        if (isLocationFetched) {
            data.put("lat", userLat);
            data.put("lng", userLng);
        } else {
            data.put("lat", 0);
            data.put("lng", 0);
        }
        data.put("busDistance", tvBusDistance.getText().toString());
        data.put("railDistance", tvRailDistance.getText().toString());
        data.put("rating", ratingBar.getRating());
        data.put("description", etDescription.getText().toString());
        data.put("website", etWebsite.getText().toString());
        data.put("phone", etPhone.getText().toString());
        data.put("acRooms", cbAC.isChecked());
        data.put("nonAcRooms", cbNonAC.isChecked());
        data.put("hotWater", cbHotWater.isChecked());
        data.put("parking", cbParking.isChecked());
        data.put("imageUrls", imageUrls);

        int selectedStayTypeId = rgStayType.getCheckedRadioButtonId();
        if (selectedStayTypeId == R.id.rb24Hours) {
            data.put("stayType", "24 Hours");
        } else if (selectedStayTypeId == R.id.rbCheckInOut) {
            data.put("stayType", "Check-in/Check-out");
            data.put("checkInTime", etCheckIn.getText().toString());
            data.put("checkOutTime", etCheckOut.getText().toString());
        }

        databaseReference.child(entryId).setValue(data).addOnSuccessListener(aVoid -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Data uploaded successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Log.e(TAG, "Failed to upload form details", e);
            Toast.makeText(this, "Failed to upload data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void checkStoragePermission() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            storagePermissionLauncher.launch(permission);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Pictures"));
    }

    private void setupImageRecyclerView() {
        imageRecyclerView = findViewById(R.id.imageRecyclerView);
        imageAdapter = new ImageAdapter(imageUris);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageRecyclerView.setAdapter(imageAdapter);
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

    private void openAddressInMaps(String address) {
        if (!isLocationFetched) {
            Toast.makeText(this, "Get GPS location first", Toast.LENGTH_SHORT).show();
            return;
        }
        String uri = "https://www.google.com/maps/dir/?api=1" + "&origin=" + userLat + "," + userLng + "&destination=" + Uri.encode(address) + "&travelmode=driving";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void calculateDistance(double destLat, double destLng, EditText target, String label) {
        if (!isLocationFetched) return;
        float[] results = new float[1];
        Location.distanceBetween(userLat, userLng, destLat, destLng, results);
        float meters = results[0];
        float km = meters / 1000f;
        target.setText(String.format(Locale.getDefault(), "%.2f km", km));
    }

    private void showTimePicker(EditText target) {
        Calendar cal = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, min) ->
                target.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, min)),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false).show();
    }

    private void getGPS() {
        if (!isLocationEnabled()) {
            showEnableLocationDialog();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userLat = location.getLatitude();
                userLng = location.getLongitude();
                isLocationFetched = true;
                etLat.setText(String.valueOf(userLat));
                etLng.setText(String.valueOf(userLng));
            } else {
                Toast.makeText(this, "Could not retrieve location. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindViews() {
        imageRecyclerView = findViewById(R.id.imageRecyclerView);
        btnPhoto = findViewById(R.id.btnPhoto);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnBus = findViewById(R.id.btnBus);
        btnRail = findViewById(R.id.btnRail);
        btnSubmit = findViewById(R.id.btnSubmit);
        etName = findViewById(R.id.etName);
        etName1 = findViewById(R.id.etName1);
        etName3 = findViewById(R.id.etName3);
        etName4 = findViewById(R.id.etName4);
        etLat = findViewById(R.id.etLatitude);
        etLng = findViewById(R.id.etLongitude);
        etCheckIn = findViewById(R.id.etCheckIn);
        etCheckOut = findViewById(R.id.etCheckOut);
        etWebsite = findViewById(R.id.etWebsite);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        tvBusDistance = findViewById(R.id.tvBusDistance);
        tvRailDistance = findViewById(R.id.tvRailDistance);
        ratingBar = findViewById(R.id.ratingBar);
        rgStayType = findViewById(R.id.rgStayType);
        rb24Hours = findViewById(R.id.rb24Hours);
        rbCheckInOut = findViewById(R.id.rbCheckInOut);
        tv24HoursInfo = findViewById(R.id.tv24HoursInfo);
        layoutCheckInOut = findViewById(R.id.layoutCheckInOut);
        cbAC = findViewById(R.id.cbAC);
        cbNonAC = findViewById(R.id.cbNonAC);
        cbHotWater = findViewById(R.id.cbHotWater);
        cbParking = findViewById(R.id.cbParking);
    }
}
