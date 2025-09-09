package com.example.calmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Exercize_Wait extends AppCompatActivity {

    private static final String TAG = "Exercize_Wait";
    private TextView exerciseTypeText, exerciseMinutesText;
    private ImageButton doneButton;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercize_wait);
        Log.d(TAG, "onCreate: Activity started");

        // ผูก UI elements
        exerciseTypeText = findViewById(R.id.exerciseTypeText);
        exerciseMinutesText = findViewById(R.id.exerciseMinutesText);
        doneButton = findViewById(R.id.doneButton);

        if (exerciseTypeText == null || exerciseMinutesText == null || doneButton == null) {
            Log.e(TAG, "onCreate: One or more UI elements are null");
            finish();
            return;
        }
        Log.d(TAG, "onCreate: UI elements bound successfully");

        // ตั้งค่า SharedPreferences
        sp = getSharedPreferences("shared_name", MODE_PRIVATE);

        // รับข้อมูลจาก Intent
        Intent intent = getIntent();
        String exerciseType = intent.getStringExtra("exercise_type");
        int exerciseValue = intent.getIntExtra("exercise_minutes", 0); // ใช้สำหรับทั้ง min และ reps
        float caloriesBurned = intent.getFloatExtra("calories_burned", 0.0f);
        Log.d(TAG, "onCreate: Received exerciseType=" + exerciseType + ", value=" + exerciseValue + ", caloriesBurned=" + caloriesBurned);

        // แสดงข้อมูลการออกกำลังกาย
        exerciseTypeText.setText(exerciseType != null ? exerciseType : "Unknown");
        if ("Push Up".equals(exerciseType)) {
            exerciseMinutesText.setText(exerciseValue + " reps"); // แสดง reps สำหรับ Push Up
        } else {
            exerciseMinutesText.setText(exerciseValue + " min"); // แสดง min สำหรับ Run หรืออื่น ๆ
        }

        // ตั้งค่า Listener สำหรับปุ่ม Done
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Calories burned=" + caloriesBurned);

                // ลด excessCalories
                float currentExcessCalories = sp.getFloat("excess_calories", 0.0f);
                float newExcessCalories = Math.max(0, currentExcessCalories - caloriesBurned);
                Log.d(TAG, "onClick: Current excessCalories=" + currentExcessCalories + ", New excessCalories=" + newExcessCalories);

                // บันทึกค่าใหม่ลง SharedPreferences
                SharedPreferences.Editor editor = sp.edit();
                editor.putFloat("excess_calories", newExcessCalories);
                editor.apply();

                // กลับไปหน้า main_Execerize
                Intent intent = new Intent(Exercize_Wait.this, main_Execerize.class);
                startActivity(intent);
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}