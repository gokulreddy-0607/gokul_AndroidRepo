package com.example.dapp_b;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class secondpageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondpage);


        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);

        btn1.setOnClickListener(v -> openSathramList("CommunitySathrams"));
        btn2.setOnClickListener(v -> openSathramList("HotelRooms"));
        btn3.setOnClickListener(v -> openSathramList("HomeStays"));


    }

    private void openSathramList(String category) {
        Intent intent = new Intent(secondpageActivity.this, SathramListActivity.class);
        intent.putExtra("CATEGORY", category);
        startActivity(intent);
    }
}
