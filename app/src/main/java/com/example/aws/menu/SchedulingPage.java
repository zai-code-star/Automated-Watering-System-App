package com.example.aws.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.aws.R;
import com.example.aws.model.History;
import com.example.aws.model.Notification;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SchedulingPage extends AppCompatActivity {

    private SwitchCompat switchAuto, switchCycle, switchTime;
    private EditText editCycleInterval, editHour, editMinute;
    private DatabaseReference zoneRef;
    private boolean isLoading = false;

    private String userId, username, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduling_page);

        switchAuto = findViewById(R.id.switchAuto);
        switchCycle = findViewById(R.id.switchCycle);
        switchTime = findViewById(R.id.switchTime);

        editCycleInterval = findViewById(R.id.editCycleInterval);
        editHour = findViewById(R.id.editHour);
        editMinute = findViewById(R.id.editMinute);

        zoneRef = FirebaseDatabase.getInstance().getReference("zones/garden_a");

        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = prefs.getString("id", null);
        username = prefs.getString("username", "unknown");
        role = prefs.getString("role", "user");

        // Auto Watering
        switchAuto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isLoading) return;
            if (isChecked) {
                zoneRef.child("mode").setValue("auto");
                showToast("Auto watering active");
                simpanHistory("Enable Auto Watering");
                kirimNotifikasi("Automated watering enabled");
            } else {
                zoneRef.child("mode").setValue("not_set");
                showToast("Auto watering nonactive");
                simpanHistory("Disable Auto Watering");
                kirimNotifikasi("Automated watering disabled");
            }
        });

        // Watering Cycle
        switchCycle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isLoading) return;
            if (isChecked) {
                String intervalStr = editCycleInterval.getText().toString().trim();
                if (!intervalStr.isEmpty()) {
                    int interval = Integer.parseInt(intervalStr);
                    zoneRef.child("mode").setValue("cycle");
                    zoneRef.child("cycle_interval").setValue(interval);
                    showToast("Cycle watering active");
                    simpanHistory("Enable the water cycle (" + interval + " second)");
                    kirimNotifikasi("Water cycle enabled");
                } else {
                    switchCycle.setChecked(false);
                    showToast("Enter the interval first");
                }
            } else {
                zoneRef.child("mode").setValue("not_set");
                zoneRef.child("cycle_interval").removeValue();
                showToast("Cycle watering nonactive");
                simpanHistory("Disable the water cycle");
                kirimNotifikasi("Water cycle disabled");
            }
        });

        // Watering Time
        switchTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isLoading) return;
            if (isChecked) {
                String hourStr = editHour.getText().toString().trim();
                String minuteStr = editMinute.getText().toString().trim();

                if (!hourStr.isEmpty() && !minuteStr.isEmpty()) {
                    int hour = Integer.parseInt(hourStr);
                    int minute = Integer.parseInt(minuteStr);

                    zoneRef.child("mode").setValue("time");
                    zoneRef.child("watering_time").child("hour").setValue(hour);
                    zoneRef.child("watering_time").child("minute").setValue(minute);
                    showToast("Time watering active");
                    simpanHistory(String.format("Enable water time (%02d:%02d)", hour, minute));
                    kirimNotifikasi(String.format("Time watering enabled at %02d:%02d", hour, minute));
                } else {
                    switchTime.setChecked(false);
                    showToast("Enter the hour and minute first.");
                }
            } else {
                zoneRef.child("mode").setValue("not_set");
                zoneRef.child("watering_time").removeValue();
                showToast("Time watering nonactive");
                simpanHistory("Disable water time");
                kirimNotifikasi("Time watering disabled");
            }
        });

        loadInitialValues();
    }

    private void loadInitialValues() {
        isLoading = true;
        zoneRef.child("mode").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String mode = String.valueOf(task.getResult().getValue());
                switchAuto.setChecked("auto".equals(mode));
                switchCycle.setChecked("cycle".equals(mode));
                switchTime.setChecked("time".equals(mode));
            }
            isLoading = false;
        });

        zoneRef.child("cycle_interval").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                editCycleInterval.setText(snapshot.getValue().toString());
            }
        });

        zoneRef.child("watering_time").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                if (snapshot.hasChild("hour")) {
                    editHour.setText(snapshot.child("hour").getValue().toString());
                }
                if (snapshot.hasChild("minute")) {
                    editMinute.setText(snapshot.child("minute").getValue().toString());
                }
            }
        });
    }

    private void simpanHistory(String activity) {
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        History history = new History(userId, username, role, activity, timestamp);

        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("history").push();
        historyRef.setValue(history).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to save history: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    private void kirimNotifikasi(String message) {
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Notification notif = new Notification(userId, message, timestamp);

        DatabaseReference notifRef = FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(userId)
                .push();

        notifRef.setValue(notif).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to send notification: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
