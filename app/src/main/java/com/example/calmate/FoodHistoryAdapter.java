package com.example.calmate;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FoodHistoryAdapter extends RecyclerView.Adapter<FoodHistoryAdapter.ViewHolder> {

    private ArrayList<String> foodList;
    private Context context;
    private SharedPreferences sp;

    public FoodHistoryAdapter(Context context, ArrayList<String> foodList) {
        this.context = context;
        this.foodList = foodList;
        this.sp = context.getSharedPreferences("shared_name", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String foodEntry = foodList.get(position);
        String[] parts = foodEntry.split("\\|"); // แยกด้วย |
        if (parts.length == 5) {
            holder.foodNameText.setText(parts[0]);
            holder.proteinText.setText(String.format("Protein: %sg", parts[1]));
            holder.carbsText.setText(String.format("Carbs: %sg", parts[2]));
            holder.fatText.setText(String.format("Fat: %sg", parts[3]));
            holder.caloriesText.setText(String.format("kcal: %s", parts[4]));

            // ตั้งค่าสีตัวอักษรตามตำแหน่ง (ดำ/ขาว)
            int textColor = (position % 2 == 0) ? Color.BLACK : Color.WHITE;
            holder.foodNameText.setTextColor(textColor);
            holder.proteinText.setTextColor(textColor);
            holder.carbsText.setTextColor(textColor);
            holder.fatText.setTextColor(textColor);
            holder.caloriesText.setTextColor(textColor);

            // ตั้งค่าสีพื้นหลังตามตำแหน่ง (สลับตามที่ต้องการ)
            int backgroundColor = (position % 2 == 0) ? Color.parseColor("#61B1A6") : Color.parseColor("#22162A");
            holder.itemView.setBackgroundColor(backgroundColor);
        }

        holder.deleteButton.setOnClickListener(v -> {
            // ลบรายการจาก foodList
            foodList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, foodList.size());

            // อัปเดต SharedPreferences
            Set<String> foodSet = new HashSet<>(foodList);
            SharedPreferences.Editor editor = sp.edit();
            editor.putStringSet("food_history", foodSet);
            editor.apply();

            // อัปเดต consumed ใน MainPage
            updateConsumedValues();
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    private void updateConsumedValues() {
        double totalProtein = 0, totalCarbs = 0, totalFat = 0, totalCalories = 0;
        for (String entry : foodList) {
            String[] parts = entry.split("\\|");
            if (parts.length == 5) {
                totalProtein += Double.parseDouble(parts[1]);
                totalCarbs += Double.parseDouble(parts[2]);
                totalFat += Double.parseDouble(parts[3]);
                totalCalories += Double.parseDouble(parts[4]);
            }
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("consumed_protein", (float) Math.min(totalProtein, sp.getFloat("target_protein", 0)));
        editor.putFloat("consumed_carbs", (float) Math.min(totalCarbs, sp.getFloat("target_carbs", 0)));
        editor.putFloat("consumed_fat", (float) Math.min(totalFat, sp.getFloat("target_fat", 0)));
        editor.putFloat("consumed_calories", (float) totalCalories);
        if (totalCalories > sp.getFloat("target_calories", 0)) {
            editor.putFloat("excess_calories", (float) (totalCalories - sp.getFloat("target_calories", 0)));
        } else {
            editor.putFloat("excess_calories", 0.0f);
        }
        editor.apply();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameText, proteinText, carbsText, fatText, caloriesText;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameText = itemView.findViewById(R.id.foodNameText);
            proteinText = itemView.findViewById(R.id.proteinText);
            carbsText = itemView.findViewById(R.id.carbsText);
            fatText = itemView.findViewById(R.id.fatText);
            caloriesText = itemView.findViewById(R.id.caloriesText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}