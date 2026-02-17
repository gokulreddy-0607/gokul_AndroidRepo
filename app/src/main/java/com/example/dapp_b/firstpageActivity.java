package com.example.dapp_b;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class firstpageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstpage);

        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);

        // Open thirtyoneActivity when first button is clicked
        btn1.setOnClickListener(v -> {
            Intent intent = new Intent(firstpageActivity.this, secondpageActivity.class);
            startActivity(intent);
        });

        // Other buttons show Toasts

    }

    private void show(String name) {
        Toast.makeText(this, name + " clicked", Toast.LENGTH_SHORT).show();
    }
}
