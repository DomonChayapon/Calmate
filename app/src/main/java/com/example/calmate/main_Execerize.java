package com.example.calmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class main_Execerize extends AppCompatActivity {

    private Button backButton, runButton, pushUpButton; // เพิ่มปุ่มใหม่
    private TextView excessCaloriesText;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_execerize);

        // ผูก UI elements
        backButton = findViewById(R.id.backBT);
        runButton = findViewById(R.id.runButton);       // ผูกปุ่ม Run
        pushUpButton = findViewById(R.id.pushUpButton); // ผูกปุ่ม Push Up
        excessCaloriesText = findViewById(R.id.excessCaloriesText);

        // ตั้งค่า SharedPreferences
        sp = this.getSharedPreferences("shared_name", Context.MODE_PRIVATE);

        // ดึง Excess Calories จาก SharedPreferences
        float excessCalories = sp.getFloat("excess_calories", 0.0f); // 0.0f เป็นค่า default
        excessCaloriesText.setText(String.format("%.1f", excessCalories));

        // ตั้งค่า OnClickListener สำหรับปุ่ม Back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main_Execerize.this, MainPage.class);
                startActivity(intent);
                finish(); // ปิดหน้า main_Execerize หลังกลับไป MainPage
            }
        });

        // ตั้งค่า OnClickListener สำหรับปุ่ม Run
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main_Execerize.this, Exercize_Run.class);
                startActivity(intent);
            }
        });

        // ตั้งค่า OnClickListener สำหรับปุ่ม Push Up
        pushUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main_Execerize.this, Exercize_Push_up.class);
                startActivity(intent);
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