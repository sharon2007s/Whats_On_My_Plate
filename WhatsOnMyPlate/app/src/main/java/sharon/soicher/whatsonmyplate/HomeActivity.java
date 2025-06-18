package sharon.soicher.whatsonmyplate;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private TextView title;
    private TextView calorieBudget;

    // Declare views for the circular progress and text display
    private ProgressBar circularProgress;
    private TextView calorieLeftText;

    // Declare buttons for meal entries and health trackers
    private Button breakfastButton, lunchButton, dinnerButton, snacksButton;
    private Button exerciseButton, stepsButton, waterButton, notificationButton;
    private Button ProfileActivityButton, bluetoothButton;

    private Firebase_Helper helper;
    private Utilities utilities;

    private void init() {
        title = findViewById(R.id.title);
        calorieBudget = findViewById(R.id.calorieBudget);
        circularProgress = findViewById(R.id.circular_progress);
        calorieLeftText = findViewById(R.id.calorie_left_text);
        breakfastButton = findViewById(R.id.breakfast_button);
        lunchButton = findViewById(R.id.lunch_button);
        dinnerButton = findViewById(R.id.dinner_button);
        snacksButton = findViewById(R.id.snacks_button);
        exerciseButton = findViewById(R.id.exercise_button);
        stepsButton = findViewById(R.id.steps_button);
        waterButton = findViewById(R.id.water_button);
        notificationButton = findViewById(R.id.notification_button);
        ProfileActivityButton = findViewById(R.id.btnProfile);
        bluetoothButton = findViewById(R.id.bluethooth_share);


        helper = new Firebase_Helper(HomeActivity.this);
        utilities = new Utilities();

        Intent intent = getIntent();
        setTitle(intent.getStringExtra("Uid"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();

        // Example: Setting up dummy data for the calorie progress.
        int totalCalorieGoal = 2000; // Your daily calorie goal
        int caloriesConsumed = 950;  // Dummy value: calories logged so far
        int caloriesLeft = totalCalorieGoal - caloriesConsumed;
        int progressPercentage = (int) ((float) caloriesConsumed / totalCalorieGoal * 100);

        // Configure the circular progress indicator
        circularProgress.setMax(100);
        circularProgress.setProgress(progressPercentage);
        calorieLeftText.setText(caloriesLeft + " kcal Left");

        // Set click listeners to navigate to detailed activity pages (create these later)
        breakfastButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, FoodEntryActivity.class)));
        lunchButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, FoodEntryActivity.class)));
        dinnerButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, FoodEntryActivity.class)));
        snacksButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, FoodEntryActivity.class)));
        exerciseButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ExerciseTrackerActivity.class)));
        stepsButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, StepsTrackerActivity.class)));
        waterButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, WaterTrackerActivity.class)));
        notificationButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this,ScheduleNotificationActivity.class)));
        ProfileActivityButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));
        bluetoothButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, BluetoothShareActivity.class)));
       // weighInButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, WeighInActivity.class)));

        // Future implementation: Fetch actual data from Firebase or other sources here.
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateCalorieData(); // Your overall update method (if needed)


    }


    private void setTitle(String userId) {
        if (userId != null) {
            helper.getUserName(userId).thenAccept(userName -> {
                if (userName != null) {
                    title.setText("Hello "+userName);
                    utilities.make_snackbar(HomeActivity.this, "Retrieved list name: " + userName);
                } else {
                    utilities.make_snackbar(HomeActivity.this, "List name not found for ID: " + userId);
                }
            }).exceptionally(error -> {
                utilities.make_snackbar(HomeActivity.this, "Failed to retrieve list name: " + error.getMessage());
                return null;
            });
        } else {
            utilities.make_snackbar(HomeActivity.this, "Error: List ID not provided to fragment.");
        }
    }
    private void updateCalorieData() {
        // Get the current user ID. If you already have this stored, reuse it.
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference the calorieConsumed field in Firebase for the current user.
        DatabaseReference calorieRef = FirebaseDatabase.getInstance()
                .getReference("USERS")
                .child(userId)
                .child("calorieConsumed");

        calorieRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Retrieve the calorie count; if none, default to 0.
                Integer caloriesConsumed = snapshot.getValue(Integer.class);
                if (caloriesConsumed == null) {
                    caloriesConsumed = 0;
                }

                // Update your progress bar and text:
                int totalCalorieGoal = 2000; // Your daily calorie goal
                int caloriesLeft = totalCalorieGoal - caloriesConsumed;
                int progressPercentage = (int) (((float) caloriesConsumed / totalCalorieGoal) * 100);

                circularProgress.setProgress(progressPercentage);
                calorieLeftText.setText(caloriesLeft + " kcal Left");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                utilities.make_snackbar(HomeActivity.this,
                        "Failed to load calorie data: " + error.getMessage());
            }
        });
    }
}