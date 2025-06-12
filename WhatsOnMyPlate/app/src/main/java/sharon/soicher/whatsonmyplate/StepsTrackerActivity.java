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
        btnGoogleFit = findViewById(R.id.btn_google_fit);
        btnSaveSteps = findViewById(R.id.btn_save_steps);
        tvStepHistory = findViewById(R.id.tv_step_history);
        tvCurrentGoal = findViewById(R.id.tv_current_goal);

        // Set default daily goal text
        tvCurrentGoal.setText("Daily Goal: " + dailyGoal + " steps");

        // Button Listeners
        btnManualEntry.setOnClickListener(v -> manuallyAddSteps());
        btnGoogleFit.setOnClickListener(v -> fetchStepsFromGoogleFit());
        btnSaveSteps.setOnClickListener(v -> saveSteps());

        // Create notification channel and schedule reminders
        createNotificationChannel();
        scheduleWalkingReminder();
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
    private void fetchStepsFromGoogleFit() {
        // TODO: Integrate with Google Fit API here.
        // For now we simulate a fetch:
        int simulatedSteps = 1500;
        currentSteps += simulatedSteps;
        stepHistory += simulatedSteps + " steps (Google Fit)\n";
        updateUI();
        Toast.makeText(this, "Fetched " + simulatedSteps + " steps from Google Fit", Toast.LENGTH_SHORT).show();
    }

    // Update the history and current goal text
    private void updateUI() {
        tvStepHistory.setText("Step History:\n" + stepHistory);
        // Optionally, update any other UI elements that show the current step count.
    }

    // Save steps and update Home Page (simulate by a Toast and finishing activity)
    private void saveSteps() {
        // Optionally, you can persist the data (e.g., in Firebase or SharedPreferences)
        Toast.makeText(this, "Steps saved & updated on Home Page!", Toast.LENGTH_SHORT).show();
        finish();
    }


    // ---------------------------
    // Notification & Reminder Code
    // ---------------------------

    // Schedules a repeating reminder using AlarmManager
    private void scheduleWalkingReminder() {
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // First reminder triggered after REMINDER_INTERVAL from now.
        long triggerAtMillis = SystemClock.elapsedRealtime() + REMINDER_INTERVAL;
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAtMillis, REMINDER_INTERVAL, pendingIntent);
    }

    // Creates a notification channel for Android O and above.
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "Walking Reminder",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Reminds you to walk and reach your step goal");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    // Method to send a walking reminder notification
    public static void sendWalkingReminder(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logoapp) // Replace with your drawable icon
                .setContentTitle("Time to Walk!")
                .setContentText("Keep moving to reach your daily step goal.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}