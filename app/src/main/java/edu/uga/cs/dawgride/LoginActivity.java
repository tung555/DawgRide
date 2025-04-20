package edu.uga.cs.dawgride;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailET, passwordET;
    private Button btnLogin;
    private TextView txtRegisterLink;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseAuth.getInstance().signOut(); //just for testing purposes
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.loginEmail);
        passwordET = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegisterLink = findViewById(R.id.txtRegisterLink);

        btnLogin.setOnClickListener(v -> loginUser());

        txtRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Login failed: "
                                + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}
