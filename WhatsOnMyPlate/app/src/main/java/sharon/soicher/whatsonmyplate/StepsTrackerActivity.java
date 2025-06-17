package sharon.soicher.whatsonmyplate;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;

public class StepsTrackerActivity extends AppCompatActivity {

    private EditText etStepCount, etDailyGoal;
    private Button btnManualEntry, btnGoogleFit, btnSaveSteps;
    private TextView tvStepHistory, tvCurrentGoal;

    private int currentSteps = 0;
    private int dailyGoal = 10000; // Default daily goal
    private String stepHistory = "";

    // Notification constants
    private static final String CHANNEL_ID = "walking_reminder_channel";
    private static final int NOTIFICATION_ID = 101;
    private static final long REMINDER_INTERVAL = 60 * 60 * 1000; // 1 hour

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_tracker);

        // Bind UI components
        etStepCount = findViewById(R.id.et_step_count);
        etDailyGoal = findViewById(R.id.et_daily_goal);
        btnManualEntry = findViewById(R.id.btn_manual_entry);
        //btnGoogleFit = findViewById(R.id.btn_google_fit);
        btnSaveSteps = findViewById(R.id.btn_save_steps);
        tvStepHistory = findViewById(R.id.tv_step_history);
        tvCurrentGoal = findViewById(R.id.tv_current_goal);

        // Set default daily goal text
        tvCurrentGoal.setText("Daily Goal: " + dailyGoal + " steps");

        // Button Listeners
        btnManualEntry.setOnClickListener(v -> manuallyAddSteps());
        // btnGoogleFit.setOnClickListener(v -> fetchStepsFromGoogleFit());
        btnSaveSteps.setOnClickListener(v -> saveSteps());



    }

    // Manual entry: read steps from EditText and update history/currentSteps
    private void manuallyAddSteps() {
        String stepsStr = etStepCount.getText().toString().trim();
        if (stepsStr.isEmpty()) {
            etStepCount.setError("Enter step count");
            return;
        }
        try {
            int steps = Integer.parseInt(stepsStr);
            currentSteps += steps;
            stepHistory += steps + " steps added\n";
            updateUI();
        } catch (NumberFormatException e) {
            etStepCount.setError("Please enter a valid number");
        }
    }

    // This method simulates fetching steps from Google Fit.
    // Replace its internals with actual Google Fit API calls.
//    private void fetchStepsFromGoogleFit() {
//        // TODO: Integrate with Google Fit API here.
//        // For now we simulate a fetch:
//        int simulatedSteps = 1500;
//        currentSteps += simulatedSteps;
//        stepHistory += simulatedSteps + " steps (Google Fit)\n";
//        updateUI();
//        Toast.makeText(this, "Fetched " + simulatedSteps + " steps from Google Fit", Toast.LENGTH_SHORT).show();
//    }

    // Update the history and current goal text
    private void updateUI() {
        tvStepHistory.setText("Step History:\n" + stepHistory);
        // Optionally, update any other UI elements that show the current step count.
    }

    // Save steps and update Home Page (simulate by a Toast and finishing activity)
    private void saveSteps() {
        // Here, you may want to update the daily goal from the EditText if the user has changed it.
        String dailyGoalStr = etDailyGoal.getText().toString().trim();
        if (!dailyGoalStr.isEmpty()) {
            dailyGoal = Integer.parseInt(dailyGoalStr);
        }

        // Obtain the user ID from FirebaseAuth
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Save or update the steps entry in Firebase
        Firebase_Helper.addSteps(userId, dailyGoal, currentSteps);

        Toast.makeText(this, "Steps saved & updated on Home Page!", Toast.LENGTH_SHORT).show();
        finish(); // Return to Home Page after saving
    }

    // ---------------------------
    // Notification & Reminder Code
    // ---------------------------






}