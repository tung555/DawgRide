package edu.uga.cs.dawgride;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameET, emailET, passwordET, confirmPasswordET;
    private Button btnRegister;
    private TextView loginLink;
    private FirebaseAuth auth;
    private DatabaseReference databaseRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        usernameET = findViewById(R.id.registerUsername);
        emailET = findViewById(R.id.registerEmail);
        passwordET = findViewById(R.id.registerPassword);
        confirmPasswordET = findViewById(R.id.registerConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        loginLink = findViewById(R.id.txtLoginLink);

        btnRegister.setOnClickListener(v -> registerUser());

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String username = usernameET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String confirmPassword = confirmPasswordET.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                String userId = firebaseUser.getUid();

                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("email", email);
                userMap.put("username", username);
                userMap.put("ridePoints", 100); // initial points

                databaseRef.child("users").child(userId).setValue(userMap)
                        .addOnCompleteListener(dbTask -> {
                            if(dbTask.isSuccessful()) {
                                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Failed to store user data!", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Registration failed: "
                        + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
