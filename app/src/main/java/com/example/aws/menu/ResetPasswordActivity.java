package com.example.aws.menu;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aws.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextNewPassword;
    private Button buttonCheckUsername, buttonResetPassword;
    private DatabaseReference dbRef;
    private String matchedUserId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonCheckUsername = findViewById(R.id.buttonCheckUsername);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);

        dbRef = FirebaseDatabase.getInstance().getReference("users");

        buttonCheckUsername.setOnClickListener(v -> checkUsername());
        buttonResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void checkUsername() {
        String username = editTextUsername.getText().toString().trim();
        if (username.isEmpty()) {
            Toast.makeText(this, "Username must be filled in", Toast.LENGTH_SHORT).show();
            return;
        }

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Object value = child.getValue();
                    if (value instanceof java.util.Map) {
                        java.util.Map user = (java.util.Map) value;
                        String dbUsername = (String) user.get("username");
                        if (username.equals(dbUsername)) {
                            matchedUserId = child.getKey();
                            found = true;
                            break;
                        }
                    }
                }

                if (found) {
                    Toast.makeText(ResetPasswordActivity.this, "Username found", Toast.LENGTH_SHORT).show();
                    editTextNewPassword.setVisibility(View.VISIBLE);
                    buttonResetPassword.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ResetPasswordActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetPassword() {
        String newPassword = editTextNewPassword.getText().toString().trim();

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "New passwords must not be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (matchedUserId != null) {
            dbRef.child(matchedUserId).child("password").setValue(newPassword)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Password successfully reset", Toast.LENGTH_SHORT).show();
                        finish(); // kembali ke login
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to reset password", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User ID not found, repeat the process", Toast.LENGTH_SHORT).show();
        }
    }
}
