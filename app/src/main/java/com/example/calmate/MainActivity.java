package com.example.calmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button mysubmitinformation;
    EditText myheight, myweight, myname, myage;
    Spinner myexercise;
    RadioButton myradiomale, myradiofemale;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // ผูก UI elements
        mysubmitinformation = findViewById(R.id.submitinformation);
        myheight = findViewById(R.id.height);
        myweight = findViewById(R.id.weight);
        myname = findViewById(R.id.name);
        myage = findViewById(R.id.age);
        myradiofemale = findViewById(R.id.radiofemale);
        myradiomale = findViewById(R.id.radiomale);
        myexercise = findViewById(R.id.exercise);

        // ตั้งค่า SharedPreferences
        sp = getSharedPreferences("shared_name", MODE_PRIVATE);

        // Spinner
        String[] items = {"Sedentary", "Lightly active", "Moderately", "Very active", "Super active"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        myexercise.setAdapter(adapter);

        // Spinner Style
        ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<>(this, R.layout.spinner_item_text, items);
        myexercise.setAdapter(adapter1);

        // ปุ่ม Submit
        mysubmitinformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String heightStr = myheight.getText().toString().trim();
                String weightStr = myweight.getText().toString().trim();
                String nameStr = myname.getText().toString().trim();
                String ageStr = myage.getText().toString().trim();

                // ตรวจสอบว่าทุกช่องมีการกรอกข้อมูลหรือไม่
                if (heightStr.isEmpty() || weightStr.isEmpty() || nameStr.isEmpty() || ageStr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "กรุณากรอกข้อมูลให้ครบทุกช่อง", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ตรวจสอบการเลือกเพศ
                if (!myradiomale.isChecked() && !myradiofemale.isChecked()) {
                    Toast.makeText(getApplicationContext(), "กรุณาเลือกเพศ", Toast.LENGTH_SHORT).show();
                    return;
                }

                // รีเซ็ต SharedPreferences เฉพาะค่า target และ consumed
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("target_protein");
                editor.remove("target_carbs");
                editor.remove("target_fat");
                editor.remove("target_calories");
                editor.remove("consumed_protein");
                editor.remove("consumed_carbs");
                editor.remove("consumed_fat");
                editor.remove("consumed_calories");
                editor.remove("excess_calories");
                editor.remove("food_history");
                editor.apply();

                // แปลงข้อมูลและคำนวณ
                try {
                    double height = Double.parseDouble(heightStr);
                    double weight = Double.parseDouble(weightStr);
                    String name = nameStr;
                    int age = Integer.parseInt(ageStr);
                    double BMR;

                    // คำนวณ BMR ตามเพศ
                    if (myradiomale.isChecked()) {
                        BMR = (weight * 13.7) + (height * 5) - (age * 6.8) + 66;
                    } else {
                        BMR = (weight * 9.6) + (height * 1.8) - (age * 4.7) + 655; // แก้จาก 665 เป็น 655 ตามสูตร Harris-Benedict
                    }

                    // ดึงค่าที่เลือกจาก Spinner
                    String selectedExercise = myexercise.getSelectedItem().toString();
                    double activityMultiplier;

                    // กำหนดตัวคูณตามระดับกิจกรรม
                    switch (selectedExercise) {
                        case "Sedentary":
                            activityMultiplier = 1.2;
                            break;
                        case "Lightly active":
                            activityMultiplier = 1.375;
                            break;
                        case "Moderately":
                            activityMultiplier = 1.55;
                            break;
                        case "Very active":
                            activityMultiplier = 1.725;
                            break;
                        case "Super active":
                            activityMultiplier = 1.9;
                            break;
                        default:
                            activityMultiplier = 1.2;
                    }

                    // คำนวณ TDEE
                    double TDEE = BMR * activityMultiplier;

                    // บันทึก TDEE ลง SharedPreferences
                    editor.putFloat("tdee", (float) TDEE);
                    editor.apply();

                    // ส่งข้อมูลไป MainPage
                    Intent myintent = new Intent(getApplicationContext(), MainPage.class);
                    myintent.putExtra("BMR", BMR);
                    myintent.putExtra("TDEE", TDEE);
                    myintent.putExtra("name", name);
                    startActivity(myintent);

                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "กรุณากรอกตัวเลขให้ถูกต้องในช่องส่วนสูง น้ำหนัก และอายุ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}