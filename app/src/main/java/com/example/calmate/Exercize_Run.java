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

public class Exercize_Run extends AppCompatActivity {

    private ImageButton backButton, confirmButton;
    private RadioGroup runOptionsGroup;
    private RadioButton radio15Min, radio30Min, radio45Min, radio60Min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercize_run);

        // ผูก UI elements
        backButton = findViewById(R.id.backRun);
        confirmButton = findViewById(R.id.confirmRun);
        runOptionsGroup = findViewById(R.id.pushUpOptionsGroup);
        radio15Min = findViewById(R.id.radio10x2Reps);
        radio30Min = findViewById(R.id.radio10x3Reps);
        radio45Min = findViewById(R.id.radio10x4Reps);
        radio60Min = findViewById(R.id.radio10x5Reps);

        // ตั้งค่า Listener สำหรับปุ่ม Confirm
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = runOptionsGroup.getCheckedRadioButtonId();
                int minutes = 0;
                float caloriesBurned = 0;

                // คำนวณเวลาและแคลอรี่ตามตัวเลือก
                if (selectedId == R.id.radio10x2Reps) {
                    minutes = 15;
                    caloriesBurned = 150;
                } else if (selectedId == R.id.radio10x3Reps) {
                    minutes = 30;
                    caloriesBurned = 300;
                } else if (selectedId == R.id.radio10x4Reps) {
                    minutes = 45;
                    caloriesBurned = 450;
                } else if (selectedId == R.id.radio10x5Reps) {
                    minutes = 60;
                    caloriesBurned = 600;
                }

                // ส่งข้อมูลไปหน้า Exercize_Wait
                Intent intent = new Intent(Exercize_Run.this, Exercize_Wait.class);
                intent.putExtra("exercise_type", "Run");
                intent.putExtra("exercise_minutes", minutes);
                intent.putExtra("calories_burned", caloriesBurned);
                startActivity(intent);
            }
        });

        // ตั้งค่า Listener สำหรับปุ่ม Back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Exercize_Run.this, main_Execerize.class);
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