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
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class FoodEntryActivity extends AppCompatActivity {

    // Views
    private Spinner spinnerMealType;
    private EditText etFoodName, etPortionSize;
    private Button btnManualEntry, btnScanFood, btnSnapshot, btnSaveFood;
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
        btnSnapshot = findViewById(R.id.btnSnapshot);
        btnSaveFood = findViewById(R.id.btn_save_food);
        tvNutritionInfo = findViewById(R.id.tv_nutrition_info);
        progressBar = findViewById(R.id.progress_bar);

       // Button for taking a snapshot of the current screen
        btnSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeSnapshot();
            }
        });


        // Set up meal type selection using a Spinner
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snack"};
        ArrayAdapter<String> mealAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mealTypes);
        mealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMealType.setAdapter(mealAdapter);

        // Handle "Add Food Manually"
        btnManualEntry.setOnClickListener(v -> fetchNutritionData());

        // Barcode Scan functionality (placeholder)
        btnScanFood.setOnClickListener(v -> Toast.makeText(FoodEntryActivity.this, "Food scan feature coming soon.", Toast.LENGTH_SHORT).show());

        // Search Food functionality (placeholder)
        btnSnapshot.setOnClickListener(v -> takeSnapshot());

        // Save Food Entry button
        btnSaveFood.setOnClickListener(v -> saveFoodEntry());
    }


    private NutritionAPI.FoodNutritionData lastFoodNutritionData = null;

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

        NutritionAPI nutritionAPI = new NutritionAPI();
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        nutritionAPI.getNutritionData(foodName, portionSize, new NutritionAPI.NutritionCallback() {
            @Override
            public void onSuccess(NutritionAPI.FoodNutritionData foodData) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvNutritionInfo.setVisibility(View.VISIBLE);
                    tvNutritionInfo.setText(foodData.toString());

                    Firebase_Helper.addMealFromAPI(userId, foodData);
                    Toast.makeText(FoodEntryActivity.this, "Meal data saved automatic",Toast.LENGTH_SHORT).show();
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

    private void takeSnapshot() {
        // 1. Capture the current window's content
        View rootView = getWindow().getDecorView().getRootView();
        Bitmap bitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        rootView.draw(canvas);

        // 2. Save the Bitmap to the Gallery.
        String filename = String.format(Locale.getDefault(), "Screenshot_%d.png", System.currentTimeMillis());

        // For Android 10 (API 29) and above, use MediaStore insertion
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Images.Media.DISPLAY_NAME, filename);
            contentValues.put(Images.Media.MIME_TYPE, "image/png");
            // Save in Pictures/MyAppScreenshots folder
            contentValues.put(Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyAppScreenshots");

            Uri imageUri = resolver.insert(Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (imageUri != null) {
                try (OutputStream outputStream = resolver.openOutputStream(imageUri)) {
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                        Toast.makeText(this, "Screenshot saved to gallery.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to compress screenshot.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error saving screenshot.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error creating media store entry.", Toast.LENGTH_SHORT).show();
            }
        }
        // For older versions, write to external storage and then notify MediaScanner
        else {
            // Ensure you have WRITE_EXTERNAL_STORAGE permission if targeting below API 29
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + "/MyAppScreenshots");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, filename);
            try (OutputStream outputStream = new FileOutputStream(file)) {
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                    outputStream.flush();
                    // Notify MediaScanner so the image appears in Gallery apps
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(file);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);
                    Toast.makeText(this, "Screenshot saved to gallery.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to compress screenshot.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving screenshot.", Toast.LENGTH_SHORT).show();
            }
        }
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