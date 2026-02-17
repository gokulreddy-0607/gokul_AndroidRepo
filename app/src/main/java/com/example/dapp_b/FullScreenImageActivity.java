package com.example.dapp_b;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView fullScreenImageView = findViewById(R.id.fullScreenImageView);
        ImageButton backButton = findViewById(R.id.backButton);

        Uri imageUri = getIntent().getParcelableExtra("imageUri");
        if (imageUri != null) {
            fullScreenImageView.setImageURI(imageUri);
        }

        backButton.setOnClickListener(v -> finish());
    }
}
