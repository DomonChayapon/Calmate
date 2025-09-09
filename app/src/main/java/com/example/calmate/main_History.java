package com.example.calmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

public class main_History extends AppCompatActivity {

    private RecyclerView historyRecyclerView;
    private ImageButton backButton;
    private FoodHistoryAdapter adapter;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_history);

        // ผูก UI elements
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        backButton = findViewById(R.id.backButton);

        // ตั้งค่า SharedPreferences
        sp = getSharedPreferences("shared_name", MODE_PRIVATE);

        // ดึงข้อมูลประวัติอาหาร
        Set<String> foodHistorySet = sp.getStringSet("food_history", null);
        ArrayList<String> foodHistoryList = (foodHistorySet != null) ? new ArrayList<>(foodHistorySet) : new ArrayList<>();

        // ตั้งค่า RecyclerView
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoodHistoryAdapter(this, foodHistoryList);
        historyRecyclerView.setAdapter(adapter);

        // ตั้งค่า OnClickListener สำหรับ backButton
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main_History.this, MainPage.class);
                startActivity(intent);
                finish();
            }
        });

        // ตั้งค่า WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}