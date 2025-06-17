package sharon.soicher.whatsonmyplate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class WaterTrackerActivity extends AppCompatActivity {

    private TextView tvWaterIntake, tvHistory;
    private Button btnAddGlass, btnAddML, btnSave;
    private ProgressBar progressBar;

    private int dailyGoal = 2000; // Daily goal in ml (e.g., 2 liters)
    private int currentIntake = 0;
    private List<String> history = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tracker);

        tvWaterIntake = findViewById(R.id.tv_water_intake);
        tvHistory = findViewById(R.id.tv_history);
        btnAddGlass = findViewById(R.id.btn_add_glass);
        btnAddML = findViewById(R.id.btn_add_ml);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);

        updateUI();

        btnAddGlass.setOnClickListener(v -> addWater(250)); // Each glass = 250ml
        btnAddML.setOnClickListener(v -> addWater(100)); // Add 100ml manually
        btnSave.setOnClickListener(v -> saveProgress());
    }

    private void addWater(int amount) {
        currentIntake += amount;
        history.add(amount + "ml added");

        updateUI();
    }

    private void updateUI() {
        tvWaterIntake.setText("Water Intake: " + currentIntake + " / " + dailyGoal + "ml");
        tvHistory.setText("History:\n" + String.join("\n", history));

        // Update progress bar
        progressBar.setMax(dailyGoal);
        progressBar.setProgress(currentIntake);
    }

    private void saveProgress() {
        // Obtain user ID from FirebaseAuth
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Save current water intake in Firebase
        Firebase_Helper.addWaterIntake(userId, currentIntake);

        Toast.makeText(this, "Water intake saved & updated on Home Page!", Toast.LENGTH_SHORT).show();
        finish(); // Close activity, returning to Home Page
    }
}