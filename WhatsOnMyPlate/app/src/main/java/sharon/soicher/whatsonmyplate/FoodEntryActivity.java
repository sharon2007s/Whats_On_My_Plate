package sharon.soicher.whatsonmyplate;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FoodEntryActivity extends AppCompatActivity {

    // Views
    private Spinner spinnerMealType;
    private EditText etFoodName, etPortionSize;
    private Button btnManualEntry, btnScanFood, btnSearchFood, btnSaveFood;
    private TextView tvNutritionInfo;
    private ProgressBar progressBar;

    private NutritionAPI nutritionAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_entry);

        nutritionAPI = new NutritionAPI();

        // Initialize Views
        spinnerMealType = findViewById(R.id.spinner_meal_type);
        etFoodName = findViewById(R.id.et_food_name);
        etPortionSize = findViewById(R.id.et_portion_size);
        btnManualEntry = findViewById(R.id.btn_manual_entry);
        btnScanFood = findViewById(R.id.btn_scan_food);
        btnSearchFood = findViewById(R.id.btn_search_food);
        btnSaveFood = findViewById(R.id.btn_save_food);
        tvNutritionInfo = findViewById(R.id.tv_nutrition_info);
        progressBar = findViewById(R.id.progress_bar);

        // Set up meal type selection using a Spinner
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snack"};
        ArrayAdapter<String> mealAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mealTypes);
        mealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMealType.setAdapter(mealAdapter);

        // Handle "Add Food Manually"
        btnManualEntry.setOnClickListener(v -> fetchNutritionData());

        // Barcode Scan functionality (placeholder)
        btnScanFood.setOnClickListener(v -> Toast.makeText(FoodEntryActivity.this, "Barcode Scanner feature coming soon.", Toast.LENGTH_SHORT).show());

        // Search Food functionality (placeholder)
        btnSearchFood.setOnClickListener(v -> Toast.makeText(FoodEntryActivity.this, "Food search feature coming soon.", Toast.LENGTH_SHORT).show());

        // Save Food Entry button
        btnSaveFood.setOnClickListener(v -> saveFoodEntry());
    }

    private void fetchNutritionData() {
        String foodName = etFoodName.getText().toString().trim();
        String portionSizeStr = etPortionSize.getText().toString().trim();

        if (foodName.isEmpty()) {
            etFoodName.setError("Enter an ingredient");
            return;
        }

        if (portionSizeStr.isEmpty()) {
            etPortionSize.setError("Enter weight in grams");
            return;
        }

        double portionSize;
        try {
            portionSize = Double.parseDouble(portionSizeStr);
        } catch (NumberFormatException e) {
            etPortionSize.setError("Enter a valid number");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvNutritionInfo.setVisibility(View.GONE);

        nutritionAPI.getNutritionData(foodName, portionSize, new NutritionAPI.NutritionCallback() {
            @Override
            public void onSuccess(NutritionAPI.FoodNutritionData foodData) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvNutritionInfo.setVisibility(View.VISIBLE);
                    tvNutritionInfo.setText(foodData.toString());
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(FoodEntryActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void saveFoodEntry() {
        String mealType = spinnerMealType.getSelectedItem().toString();
        String foodName = etFoodName.getText().toString().trim();
        String portionSize = etPortionSize.getText().toString().trim();

        if (foodName.isEmpty() || portionSize.isEmpty() || tvNutritionInfo.getText().toString().isEmpty()) {
            Toast.makeText(this, "Complete the food entry before saving.", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Save to Firebase (or your local DB) and update Home page
        Toast.makeText(this, "Food saved for " + mealType, Toast.LENGTH_SHORT).show();
        finish(); // Return to Home page after saving
    }
}