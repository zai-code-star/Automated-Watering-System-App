package com.example.aws.menu.admin;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aws.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditDeleteUserActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Spinner spinnerRole;
    private Button btnUpdate, btnDelete;

    private String userId;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete_user);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        userId = getIntent().getStringExtra("id");
        etUsername.setText(getIntent().getStringExtra("username"));
        etPassword.setText(getIntent().getStringExtra("password"));
        String role = getIntent().getStringExtra("role");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        if (role != null) {
            int spinnerPosition = adapter.getPosition(role);
            spinnerRole.setSelection(spinnerPosition);
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        btnUpdate.setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();
            String newPassword = etPassword.getText().toString().trim();
            String newRole = spinnerRole.getSelectedItem().toString();

            if (newUsername.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            userRef.child("username").setValue(newUsername);
            userRef.child("password").setValue(newPassword);
            userRef.child("role").setValue(newRole);

            Toast.makeText(this, "User berhasil diperbarui", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnDelete.setOnClickListener(v -> {
            userRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "User dihapus", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Gagal hapus: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }
}
