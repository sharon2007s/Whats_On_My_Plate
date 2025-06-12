package sharon.soicher.whatsonmyplate;

/*import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private EditText nameInput, ageInput, heightInput, weightInput;
    private Spinner genderSpinner, goalsSpinner, dietSpinner;
    private Switch unitSwitch;
    private Button saveButton, recalculateButton;
    private UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameInput = findViewById(R.id.name_input);
        ageInput = findViewById(R.id.age_input);
        heightInput = findViewById(R.id.height_input);
        weightInput = findViewById(R.id.weight_input);
        genderSpinner = findViewById(R.id.gender_spinner);
        goalsSpinner = findViewById(R.id.goals_spinner);
        dietSpinner = findViewById(R.id.diet_spinner);
        unitSwitch = findViewById(R.id.unit_switch);
        saveButton = findViewById(R.id.save_button);
        recalculateButton = findViewById(R.id.recalculate_button);

        loadUserData();

        saveButton.setOnClickListener(v -> saveUserData());
        recalculateButton.setOnClickListener(v -> recalculateCalorieBudget());
    }

    private void loadUserData() {
        db.collection("users").document("currentUserId").get()
                .addOnSuccessListener(documentSnapshot -> {
                    userProfile = documentSnapshot.toObject(UserProfile.class);
                    if (userProfile != null) {
                        nameInput.setText(userProfile.getName());
                        ageInput.setText(String.valueOf(userProfile.getAge()));
                        heightInput.setText(String.valueOf(userProfile.getHeight()));
                        weightInput.setText(String.valueOf(userProfile.getWeight()));
                        //unitSwitch.setChecked(userProfile.isUseMetric());
                    }
                });
    }

    private void saveUserData() {
        String name = nameInput.getText().toString();
        int age = Integer.parseInt(ageInput.getText().toString());
        float height = Float.parseFloat(heightInput.getText().toString());
        float weight = Float.parseFloat(weightInput.getText().toString());
        boolean useMetric = unitSwitch.isChecked();
        String gender = genderSpinner.getSelectedItem().toString();
        String goals = goalsSpinner.getSelectedItem().toString();
        String dietPreferences = dietSpinner.getSelectedItem().toString();

        userProfile = new UserProfile(name, age, gender, height, weight, goals);
        db.collection("users").document("currentUserId").set(userProfile)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }

    private void recalculateCalorieBudget() {
        float calorieBudget = 2000; // Default value

        if (userProfile != null) {
            if (userProfile.getGoals().equals("Lose Weight")) calorieBudget -= 500;
            else if (userProfile.getGoals().equals("Gain Muscle")) calorieBudget += 500;
        }

        Toast.makeText(this, "New Calorie Budget: " + calorieBudget + " kcal", Toast.LENGTH_LONG).show();
    }
}*/