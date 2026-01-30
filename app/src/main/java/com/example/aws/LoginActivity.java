package com.example.aws;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aws.menu.ResetPasswordActivity;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Spinner roleSpinner;
    private Button loginButton;
    private String selectedRole = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
        }

        TextView forgotPasswordTextView = findViewById(R.id.textViewForgotPassword);
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        roleSpinner = findViewById(R.id.spinnerRole);
        loginButton = findViewById(R.id.buttonLogin);
        TextView textRegister = findViewById(R.id.textViewRegister);
        textRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);
        roleSpinner.setSelection(0);
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString().toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRole = "user";
            }
        });

        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and Password must be filled in", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean loginSuccess = false;
                String matchedUserId = null;
                String dbUsername = "";
                String dbRole = "";

                for (DataSnapshot child : snapshot.getChildren()) {
                    Object value = child.getValue();
                    if (value instanceof java.util.Map) {
                        java.util.Map user = (java.util.Map) value;
                        dbUsername = (String) user.get("username");
                        String dbPassword = (String) user.get("password");
                        dbRole = (String) user.get("role");

                        if (username.equals(dbUsername) &&
                                password.equals(dbPassword) &&
                                selectedRole.equalsIgnoreCase(dbRole)) {
                            loginSuccess = true;
                            matchedUserId = child.getKey();
                            break;
                        }
                    }
                }

                if (loginSuccess && matchedUserId != null) {
                    getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            .edit()
                            .putString("id", matchedUserId)
                            .putString("username", dbUsername)
                            .putString("role", dbRole)
                            .apply();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(new Intent(LoginActivity.this, ForegroundFirebaseService.class));
                    } else {
                        startService(new Intent(LoginActivity.this, ForegroundFirebaseService.class));
                    }

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

            } else {
                    Toast.makeText(LoginActivity.this,
                            "Login failed: Check your username, password, and role",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this,
                        "An error has occurred: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
