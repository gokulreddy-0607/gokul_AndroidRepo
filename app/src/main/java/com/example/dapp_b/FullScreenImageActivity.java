package com.example.dapp_b;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView fullScreenImageView = findViewById(R.id.fullScreenImageView);
        ImageButton backButton = findViewById(R.id.backButton);

        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            Glide.with(this)
                    .load(imageUriString)
                    .into(fullScreenImageView);
        }

        backButton.setOnClickListener(v -> finish());
    }
}