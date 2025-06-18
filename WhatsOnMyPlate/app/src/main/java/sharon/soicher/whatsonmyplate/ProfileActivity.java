package sharon.soicher.whatsonmyplate;

import android.os.Bundle;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private Button logoutButton; // Add this



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameInput = findViewById(R.id.name_input);
        ageInput = findViewById(R.id.age_input);
        heightInput = findViewById(R.id.height_input);
        weightInput = findViewById(R.id.weight_input);

        saveButton = findViewById(R.id.save_button);
        recalculateButton = findViewById(R.id.recalculate_button);
        logoutButton = findViewById(R.id.btn_logout);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("USERS").child(currentUser.getUid());


        loadUserData();

        saveButton.setOnClickListener(v -> saveUserData());
        recalculateButton.setOnClickListener(v -> recalculateCalorieBudget());


        logoutButton.setOnClickListener(v -> {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Create an intent to the login activity (or whatever activity you use for signing in)
            Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);

            // Clear the activity stack so the user cannot navigate back after logging out
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Start the login activity
            startActivity(loginIntent);

            // Finish the current activity
            finish();
        });
    }




private void loadUserData() {
    databaseReference.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            if (snapshot.exists()) {
                userProfile = snapshot.getValue(UserProfile.class);
                if (userProfile != null) {
                    nameInput.setText(userProfile.getName());
                    ageInput.setText(String.valueOf(userProfile.getAge()));
                    heightInput.setText(String.valueOf(userProfile.getHeight()));
                    weightInput.setText(String.valueOf(userProfile.getWeight()));
                    // Set gender, goals, etc. if needed
                }
            }
        }
        @Override
        public void onCancelled(DatabaseError error) {
            Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
        }
    });
}




    private void saveUserData() {
        // Retrieve and trim inputs from EditTexts
        String name = nameInput.getText().toString().trim();
        String ageText = ageInput.getText().toString().trim();
        String heightText = heightInput.getText().toString().trim();
        String weightText = weightInput.getText().toString().trim();

        // Validate inputs
        if(name.isEmpty() || ageText.isEmpty() || heightText.isEmpty() || weightText.isEmpty()){
            Toast.makeText(this, "Please fill in all profile fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse age, height, and weight safely
        int age;
        float height, weight;
        try {
            age = Integer.parseInt(ageText);
            height = Float.parseFloat(heightText);
            weight = Float.parseFloat(weightText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format in one of the fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Since spinners are removed, set default values for gender & goals
        String gender = "Not Specified";
        String goals = "Not Specified";

        // Create and update the profile object
        userProfile = new UserProfile(name, age, gender, height, weight, goals);
        databaseReference.child("profile").setValue(userProfile)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(ProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void recalculateCalorieBudget() {
        float calorieBudget = 2000; // Default value

        if (userProfile != null) {
            if (userProfile.getGoals().equals("Lose Weight")) calorieBudget -= 500;
            else if (userProfile.getGoals().equals("Gain Muscle")) calorieBudget += 500;
        }

        Toast.makeText(this, "New Calorie Budget: " + calorieBudget + " kcal", Toast.LENGTH_LONG).show();
    }
}