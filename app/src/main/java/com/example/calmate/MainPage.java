package com.example.calmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainPage extends AppCompatActivity {

    private EditText foodInput;
    private Button submitButton, goToExerciseButton, resetButton, historyButton;
    private ProgressBar proteinProgressBar, carbsProgressBar, fatProgressBar, caloriesProgressBar, achievementProgressBar;
    private TextView proteinStatus, carbsStatus, fatStatus, caloriesStatus, timeDisplay;
    private RequestQueue requestQueue;
    private static final String API_KEY = "fb76b675a70e4e409c47efcd25afc5bf";
    private double targetProtein, targetCarbs, targetFat, targetCalories;
    private double consumedProtein, consumedCarbs, consumedFat, consumedCalories;
    private int achievementProgress;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);

        // ผูก UI elements
        foodInput = findViewById(R.id.foodInput);
        submitButton = findViewById(R.id.submitButton);
        goToExerciseButton = findViewById(R.id.goToExerciseButton);
        resetButton = findViewById(R.id.resetButton);
        historyButton = findViewById(R.id.historyButton);
        proteinProgressBar = findViewById(R.id.proteinProgressBar);
        carbsProgressBar = findViewById(R.id.carbsProgressBar);
        fatProgressBar = findViewById(R.id.fatProgressBar);
        caloriesProgressBar = findViewById(R.id.caloriesProgressBar);
        achievementProgressBar = findViewById(R.id.achievementProgressBar);
        proteinStatus = findViewById(R.id.proteinStatus);
        carbsStatus = findViewById(R.id.carbsStatus);
        fatStatus = findViewById(R.id.fatStatus);
        caloriesStatus = findViewById(R.id.caloriesStatus);
        timeDisplay = findViewById(R.id.timeDisplay);
        requestQueue = Volley.newRequestQueue(this);

        // ตั้งค่าสีตัวอักษรปุ่ม Submit เป็นสีดำ
        submitButton.setTextColor(Color.BLACK);

        // ตั้งค่า SharedPreferences
        sp = this.getSharedPreferences("shared_name", Context.MODE_PRIVATE);

        // ดึงข้อมูล TDEE จาก Intent หรือ SharedPreferences
        Intent intent = getIntent();
        double tdee;
        if (intent != null && intent.hasExtra("TDEE")) {
            // กรณีมาจาก MainActivity
            tdee = intent.getDoubleExtra("TDEE", 0.0);
        } else {
            // กรณีกลับมาจากหน้าอื่น
            tdee = sp.getFloat("tdee", 0.0f);
            if (tdee == 0.0) {
                Toast.makeText(this, "กรุณากรอกข้อมูลในหน้าแรก", Toast.LENGTH_SHORT).show();
                Intent backIntent = new Intent(this, MainActivity.class);
                startActivity(backIntent);
                finish();
                return;
            }
        }

        // คำนวณค่า target ใหม่จาก TDEE
        targetProtein = (tdee * 0.3) / 4; // 30% โปรตีน
        targetCarbs = (tdee * 0.5) / 4;   // 50% คาร์โบไฮเดรต
        targetFat = (tdee * 0.2) / 9;     // 20% ไขมัน
        targetCalories = tdee;

        // โหลดค่า consumed และ achievement จาก SharedPreferences
        consumedProtein = sp.getFloat("consumed_protein", 0.0f);
        consumedCarbs = sp.getFloat("consumed_carbs", 0.0f);
        consumedFat = sp.getFloat("consumed_fat", 0.0f);
        consumedCalories = sp.getFloat("consumed_calories", 0.0f);
        achievementProgress = sp.getInt("achievement_progress", 0);

        // บันทึกค่า target ใหม่ลง SharedPreferences
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("target_protein", (float) targetProtein);
        editor.putFloat("target_carbs", (float) targetCarbs);
        editor.putFloat("target_fat", (float) targetFat);
        editor.putFloat("target_calories", (float) targetCalories);
        editor.putFloat("tdee", (float) tdee);
        editor.apply();

        // ตั้งค่าสูงสุดของ ProgressBar
        proteinProgressBar.setMax((int) targetProtein);
        carbsProgressBar.setMax((int) targetCarbs);
        fatProgressBar.setMax((int) targetFat);
        caloriesProgressBar.setMax((int) targetCalories);
        achievementProgressBar.setMax(10);

        // แสดงเวลาปัจจุบันครั้งแรก
        updateTimeDisplay();

        // อัปเดต UI
        updateNutritionStatus();

        // ตั้งค่า OnClickListener สำหรับ submitButton
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = foodInput.getText().toString().trim();
                if (query.isEmpty()) {
                    Toast.makeText(MainPage.this, "กรุณากรอกชื่ออาหาร", Toast.LENGTH_SHORT).show();
                    return;
                }
                fetchNutritionData(query);
            }
        });

        // ตั้งค่า OnClickListener สำหรับ goToExerciseButton
        goToExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, main_Execerize.class);
                startActivity(intent);
            }
        });

        // ตั้งค่า OnClickListener สำหรับ resetButton
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float excessCalories = sp.getFloat("excess_calories", 0.0f);
                // ปรับเงื่อนไขให้ยืดหยุ่นขึ้น (เลือกใช้ตามความต้องการ)
                if (consumedProtein >= targetProtein * 0.9 && // ลดเป้าลงเหลือ 90%
                        consumedCarbs >= targetCarbs * 0.9 &&
                        consumedFat >= targetFat * 0.9 &&
                        excessCalories <= 0) {
                    if (achievementProgress < 10) {
                        achievementProgress++;
                        Toast.makeText(MainPage.this, "Achievement +1! Progress: " + achievementProgress, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (achievementProgress > 0) {
                        achievementProgress--;
                        Toast.makeText(MainPage.this, "Achievement -1! Progress: " + achievementProgress, Toast.LENGTH_SHORT).show();
                    }
                }

                consumedProtein = 0.0;
                consumedCarbs = 0.0;
                consumedFat = 0.0;
                consumedCalories = 0.0;

                SharedPreferences.Editor editor = sp.edit();
                editor.putFloat("consumed_protein", (float) consumedProtein);
                editor.putFloat("consumed_carbs", (float) consumedCarbs);
                editor.putFloat("consumed_fat", (float) consumedFat);
                editor.putFloat("consumed_calories", (float) consumedCalories);
                editor.putFloat("excess_calories", 0.0f);
                editor.putInt("achievement_progress", achievementProgress);
                editor.remove("food_history");
                editor.apply();

                timeDisplay.setText("Time: 00:00");
                updateNutritionStatus();
            }
        });

        // ตั้งค่า OnClickListener สำหรับ historyButton
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, main_History.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchNutritionData(String query) {
        String url = "https://api.spoonacular.com/recipes/parseIngredients?apiKey=" + API_KEY + "&includeNutrition=true";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() > 0) {
                                JSONObject food = jsonArray.getJSONObject(0);
                                String name = food.getString("name");

                                if (food.has("nutrition")) {
                                    JSONObject nutrition = food.getJSONObject("nutrition");
                                    JSONArray nutrients = nutrition.getJSONArray("nutrients");

                                    double protein = 0, carbs = 0, fat = 0, calories = 0;
                                    for (int i = 0; i < nutrients.length(); i++) {
                                        JSONObject nutrient = nutrients.getJSONObject(i);
                                        String nutrientName = nutrient.getString("name");
                                        double amount = nutrient.getDouble("amount");
                                        switch (nutrientName) {
                                            case "Protein":
                                                protein = amount;
                                                break;
                                            case "Carbohydrates":
                                                carbs = amount;
                                                break;
                                            case "Fat":
                                                fat = amount;
                                                break;
                                            case "Calories":
                                                calories = amount;
                                                break;
                                        }
                                    }
                                    consumedProtein = Math.min(consumedProtein + protein, targetProtein);
                                    consumedCarbs = Math.min(consumedCarbs + carbs, targetCarbs);
                                    consumedFat = Math.min(consumedFat + fat, targetFat);
                                    consumedCalories += calories;

                                    Set<String> foodHistory = sp.getStringSet("food_history", new HashSet<>());
                                    String foodEntry = name + "|" + protein + "|" + carbs + "|" + fat + "|" + calories;
                                    foodHistory.add(foodEntry);

                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putFloat("consumed_protein", (float) consumedProtein);
                                    editor.putFloat("consumed_carbs", (float) consumedCarbs);
                                    editor.putFloat("consumed_fat", (float) consumedFat);
                                    editor.putFloat("consumed_calories", (float) consumedCalories);
                                    editor.putStringSet("food_history", foodHistory);
                                    if (consumedCalories > targetCalories) {
                                        double excessCalories = consumedCalories - targetCalories;
                                        editor.putFloat("excess_calories", (float) excessCalories);
                                    } else {
                                        editor.putFloat("excess_calories", 0.0f);
                                    }
                                    editor.apply();

                                    updateNutritionStatus();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainPage.this, "เกิดข้อผิดพลาดในการดึงข้อมูลอาหาร", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainPage.this, "ไม่สามารถเชื่อมต่อ API ได้", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("ingredientList", query);
                params.put("servings", "1");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void updateNutritionStatus() {
        proteinProgressBar.setProgress((int) consumedProtein);
        carbsProgressBar.setProgress((int) consumedCarbs);
        fatProgressBar.setProgress((int) consumedFat);
        caloriesProgressBar.setProgress((int) consumedCalories);
        achievementProgressBar.setProgress(achievementProgress);

        String proteinText = String.format("Protein: %.1f / %.1f g.", consumedProtein, targetProtein);
        String carbsText = String.format("Carbohydrates: %.1f / %.1f g.", consumedCarbs, targetCarbs);
        String fatText = String.format("Fat: %.1f / %.1f g.", consumedFat, targetFat);
        String caloriesText = String.format("Calories: %.1f / %.1f kcal", consumedCalories, targetCalories);

        proteinStatus.setText(proteinText);
        carbsStatus.setText(carbsText);
        fatStatus.setText(fatText);
        caloriesStatus.setText(caloriesText);
    }

    private void updateTimeDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());
        timeDisplay.setText("Time: " + currentTime);
    }
}