package sharon.soicher.whatsonmyplate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.CompletableFuture;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    private ProgressBar progressBar;
    
    private Firebase_Helper helper;
    private Utilities utilities;

    private void init() {
        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        registerTextView = findViewById(R.id.login_register_text);
        progressBar = findViewById(R.id.login_progress_bar);
        
        helper = new Firebase_Helper(LoginActivity.this);
        utilities = new Utilities();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        loginButton.setOnClickListener(v -> loginUser());

        registerTextView.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        CompletableFuture<String> login_future = helper.login(email, password);

        login_future.thenAccept(uid -> {
            utilities.make_snackbar(LoginActivity.this, "success");

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("Uid", uid);
            startActivity(intent);
        }).exceptionally(ex -> {
            utilities.make_snackbar(LoginActivity.this, ex.getMessage());
            return null;
        });
    }
}