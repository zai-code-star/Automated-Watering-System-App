package com.example.aws;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.aws.menu.admin.HistoryFragment;
import com.example.aws.menu.admin.ManageAccountFragment;
import com.example.aws.menu.admin.ReportFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        requestNotificationPermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, ForegroundFirebaseService.class));
        } else {
            startService(new Intent(this, ForegroundFirebaseService.class));
        }

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = prefs.getString("role", "user");

        if ("admin".equalsIgnoreCase(role)) {
            bottomNavigationView.setVisibility(View.VISIBLE);

            if (savedInstanceState == null) {
                loadFragment(new HomeFragment());
            }

            bottomNavigationView.setOnItemSelectedListener(item -> {
                Fragment selectedFragment;

                int itemId = item.getItemId();
                if (itemId == R.id.menu_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.menu_manage_account) {
                    selectedFragment = new ManageAccountFragment();
                } else if (itemId == R.id.menu_history) {
                    selectedFragment = new HistoryFragment();
                } else if (itemId == R.id.menu_report) {
                    selectedFragment = new ReportFragment();
                } else {
                    return false;
                }

                loadFragment(selectedFragment);
                return true;
            });

        } else {
            bottomNavigationView.setVisibility(View.GONE);
            loadFragment(new HomeFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    // Optional: Tampilkan hasil izin ke user
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
