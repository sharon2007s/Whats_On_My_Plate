package sharon.soicher.whatsonmyplate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class RegisterActivity extends AppCompatActivity {

    EditText firstNameET, lastNameET, ageET, genderET, weightET, heightET, emailET, passwordET;
    Button registerButton;
    CheckBox loseWeightCB, improveCompositionCB, beFitCB, healthCB, eatBetterCB, feelBetterCB;

    private Firebase_Helper helper;
    private Utilities utilities;

    private void init()
    {
        firstNameET = findViewById(R.id.register_first_name);
        lastNameET = findViewById(R.id.register_last_name);
        ageET = findViewById(R.id.register_age);
        genderET = findViewById(R.id.register_gender);
        weightET = findViewById(R.id.register_weight);
        heightET = findViewById(R.id.register_height);
        emailET = findViewById(R.id.register_email);
        passwordET = findViewById(R.id.register_password);

        loseWeightCB = findViewById(R.id.goal_lose_weight);
        improveCompositionCB = findViewById(R.id.goal_body_composition);
        beFitCB = findViewById(R.id.goal_be_fit);
        healthCB = findViewById(R.id.goal_health);
        eatBetterCB = findViewById(R.id.goal_eat_better);
        feelBetterCB = findViewById(R.id.goal_feel_better);

        registerButton = findViewById(R.id.register_button);

        helper = new Firebase_Helper(RegisterActivity.this);
        utilities = new Utilities();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String firstName = firstNameET.getText().toString().trim();
        String lastName = lastNameET.getText().toString().trim();
        String ageString = ageET.getText().toString().trim();
        String gender = genderET.getText().toString().trim();
        String weightStr = weightET.getText().toString().trim();
        String heightStr = heightET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        int age = 0;
        if (!ageString.isEmpty()) {
            age = Integer.parseInt(ageString);
        }
        float weight = 0f;
        float height = 0f;

        if (!weightStr.isEmpty()) {
            weight = Float.parseFloat(weightStr);
        }

        if (!heightStr.isEmpty()) {
            height = Float.parseFloat(heightStr);
        }

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email and Password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder goalsBuilder = new StringBuilder();

        if (loseWeightCB.isChecked()) goalsBuilder.append("Lose or gain weight, ");
        if (improveCompositionCB.isChecked()) goalsBuilder.append("Improve body composition, ");
        if (beFitCB.isChecked()) goalsBuilder.append("Become more fit, ");
        if (healthCB.isChecked()) goalsBuilder.append("Improve overall health, ");
        if (eatBetterCB.isChecked()) goalsBuilder.append("Eat better, ");
        if (feelBetterCB.isChecked()) goalsBuilder.append("Feel better");

// Convert to String
        String goals = goalsBuilder.toString();
        String name = String.format("%s %s", firstName, lastName);

        UserProfile profile = new UserProfile(name, age, gender, weight, height, goals);

        CompletableFuture<String> registerFuture = helper.Register(profile, email, password);

        registerFuture.thenAccept(uid -> {
            utilities.make_snackbar(RegisterActivity.this, "succeed");

            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            intent.putExtra("Uid", uid);
            startActivity(intent);
        }).exceptionally(ex -> {
            utilities.make_snackbar(RegisterActivity.this,ex.getMessage());
            return null;
        });
    }
}