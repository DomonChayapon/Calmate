package com.example.calmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Exercize_Push_up extends AppCompatActivity {

    private ImageButton backButton, confirmButton;
    private RadioGroup pushUpOptionsGroup;
    private RadioButton radio10x2Reps, radio10x3Reps, radio10x4Reps, radio10x5Reps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercize_push_up);

        // ผูก UI elements
        backButton = findViewById(R.id.backPushUp);
        confirmButton = findViewById(R.id.confirmPushUp);
        pushUpOptionsGroup = findViewById(R.id.pushUpOptionsGroup);
        radio10x2Reps = findViewById(R.id.radio10x2Reps);
        radio10x3Reps = findViewById(R.id.radio10x3Reps);
        radio10x4Reps = findViewById(R.id.radio10x4Reps);
        radio10x5Reps = findViewById(R.id.radio10x5Reps);

        // ตั้งค่า Listener สำหรับปุ่ม Confirm
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = pushUpOptionsGroup.getCheckedRadioButtonId();
                int reps = 0;
                float caloriesBurned = 0;

                // คำนวณ reps และแคลอรี่ตามตัวเลือก
                if (selectedId == R.id.radio10x2Reps) {
                    reps = 20; // 10X2
                    caloriesBurned = 10;
                } else if (selectedId == R.id.radio10x3Reps) {
                    reps = 30; // 10X3
                    caloriesBurned = 15;
                } else if (selectedId == R.id.radio10x4Reps) {
                    reps = 40; // 10X4
                    caloriesBurned = 20;
                } else if (selectedId == R.id.radio10x5Reps) {
                    reps = 50; // 10X5
                    caloriesBurned = 25;
                }

                // ส่งข้อมูลไปหน้า Exercize_Wait
                Intent intent = new Intent(Exercize_Push_up.this, Exercize_Wait.class);
                intent.putExtra("exercise_type", "Push Up");
                intent.putExtra("exercise_minutes", reps); // ใช้ minutes เป็น reps
                intent.putExtra("calories_burned", caloriesBurned);
                startActivity(intent);
            }
        });

        // ตั้งค่า Listener สำหรับปุ่ม Back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Exercize_Push_up.this, main_Execerize.class);
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