package com.example.dapp_b;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SathramListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SathramAdapter sathramAdapter;
    private List<Sathram> sathramList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sathram_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sathramList = new ArrayList<>();
        sathramAdapter = new SathramAdapter(this, sathramList);
        recyclerView.setAdapter(sathramAdapter);

        String categoryValue = getIntent().getStringExtra("CATEGORY");
        if (categoryValue == null) {
            categoryValue = "CommunitySathrams"; // Default category
        }
        final String category = categoryValue;
        databaseReference = FirebaseDatabase.getInstance().getReference(category);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sathramList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sathram sathram = postSnapshot.getValue(Sathram.class);
                    sathramList.add(sathram);
                }
                sathramAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SathramListActivity.this, twentyoneActivity.class);
                intent.putExtra("CATEGORY", category);
                startActivity(intent);
            }
        });
    }
}