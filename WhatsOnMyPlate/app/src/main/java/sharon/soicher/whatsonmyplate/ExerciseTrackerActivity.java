package sharon.soicher.whatsonmyplate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ExerciseTrackerActivity extends AppCompatActivity {

    private Spinner spinnerExerciseType;
    private EditText etDuration, etCaloriesBurned;
    private Button btnCalculateCalories, btnSaveExercise;
    private TextView tvExerciseLog;

    private List<String> exerciseHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_tracker);

        spinnerExerciseType = findViewById(R.id.spinner_exercise_type);
        etDuration = findViewById(R.id.et_duration);
        etCaloriesBurned = findViewById(R.id.et_calories_burned);
        btnCalculateCalories = findViewById(R.id.btn_calculate_calories);
        btnSaveExercise = findViewById(R.id.btn_save_exercise);
        tvExerciseLog = findViewById(R.id.tv_exercise_log);

        setupSpinner();

        btnCalculateCalories.setOnClickListener(v -> calculateCalories());
        btnSaveExercise.setOnClickListener(v -> saveExerciseLog());
    }

    private void setupSpinner() {
        String[] exerciseTypes = {"Running", "Cycling", "Walking", "Swimming", "Strength Training"};
        spinnerExerciseType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, exerciseTypes));
    }

    private void calculateCalories() {
        String exerciseType = spinnerExerciseType.getSelectedItem().toString();
        String durationStr = etDuration.getText().toString().trim();

        if (durationStr.isEmpty()) {
            etDuration.setError("Enter duration in minutes");
            return;
        }

        int duration = Integer.parseInt(durationStr);
        int caloriesBurned = estimateCalories(exerciseType, duration);
        etCaloriesBurned.setText(String.valueOf(caloriesBurned));
    }

    private int estimateCalories(String exerciseType, int duration) {
        switch (exerciseType) {
            case "Running": return duration * 10;
            case "Cycling": return duration * 8;
            case "Walking": return duration * 5;
            case "Swimming": return duration * 9;
            case "Strength Training": return duration * 6;
            default: return 0;
        }
    }

    private void saveExerciseLog() {
        String exerciseType = spinnerExerciseType.getSelectedItem().toString();
        String durationStr = etDuration.getText().toString().trim();
        String caloriesStr = etCaloriesBurned.getText().toString().trim();

        if (durationStr.isEmpty() || caloriesStr.isEmpty()) {
            Toast.makeText(this, "Complete the exercise details before saving.", Toast.LENGTH_SHORT).show();
            return;
        }

        exerciseHistory.add(exerciseType + " - " + durationStr + " min - " + caloriesStr + " kcal burned");
        updateExerciseLog();

        Toast.makeText(this, "Exercise logged & updated on Home Page!", Toast.LENGTH_SHORT).show();
        finish(); // Return to Home Page after saving
    }

    private void updateExerciseLog() {
        tvExerciseLog.setText("Exercise Log:\n" + String.join("\n", exerciseHistory));
    }
}