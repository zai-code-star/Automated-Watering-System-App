package com.example.aws;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aws.menu.SettingPage;
import com.example.aws.model.History;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private ProgressDialog progressDialog;
    private DatabaseReference gardenRef;
    private CountDownTimer countdownTimer;

    private TextView textModeStatus, textModeDescription, textCountdown;

    private String currentMode = "not_set";
    private boolean isWatering = false;
    private String autoDescription = "Auto watering based on soil moisture level";
    private String cycleDescription = "Cycle watering active";

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageButton buttonSettings = view.findViewById(R.id.buttonSettings);
        Button buttonWaterNow = view.findViewById(R.id.buttonWaterNow);

        gardenRef = FirebaseDatabase.getInstance().getReference("zones/garden_a");

        textModeStatus = view.findViewById(R.id.textModeStatus);
        textModeDescription = view.findViewById(R.id.textModeDescription);
        textCountdown = view.findViewById(R.id.textCountdown);

        gardenRef.child("mode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentMode = snapshot.getValue(String.class);

                if (countdownTimer != null) countdownTimer.cancel();
                textCountdown.setVisibility(View.GONE);

                if (currentMode == null || currentMode.equals("not_set")) {
                    textModeStatus.setText("Mode: Not Set");
                    textModeDescription.setText("No watering mode is active");
                    return;
                }

                switch (currentMode) {
                    case "auto":
                        textModeStatus.setText("Mode: Auto");
                        textModeDescription.setText(autoDescription);
                        break;

                    case "cycle":
                        textModeStatus.setText("Mode: Cycle");
                        gardenRef.child("cycle_interval").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Long interval = snapshot.getValue(Long.class);
                                if (interval != null) {
                                    long seconds = interval / 1000;
                                    cycleDescription = "Watering every " + seconds + " second(s)";
                                    textModeDescription.setText(cycleDescription);
                                } else {
                                    textModeDescription.setText(cycleDescription);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                textModeDescription.setText(cycleDescription);
                            }
                        });
                        break;

                    case "time":
                        textModeStatus.setText("Mode: Time");
                        textModeDescription.setText("Time watering based on clock");
                        textCountdown.setVisibility(View.VISIBLE);
                        mulaiCountdownDariWaktu();
                        break;

                    default:
                        textModeStatus.setText("Mode: " + currentMode);
                        textModeDescription.setText("");
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to read status mode", Toast.LENGTH_SHORT).show();
            }
        });

        gardenRef.child("status").child("pump_on").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean pumpOn = snapshot.getValue(Boolean.class);
                if (pumpOn == null) return;

                isWatering = pumpOn;

                if (isWatering) {
                    if (currentMode.equals("auto") || currentMode.equals("cycle")) {
                        textModeDescription.setText("Watering plants...");
                    }
                } else {
                    if (currentMode.equals("auto")) {
                        textModeDescription.setText(autoDescription);
                    } else if (currentMode.equals("cycle")) {
                        textModeDescription.setText(cycleDescription);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        buttonWaterNow.setOnClickListener(v -> {
            aktifkanWaterNow();
            simpanHistoryWaterNow();
        });

        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SettingPage.class);
            startActivity(intent);
        });

        return view;
    }

    private void mulaiCountdownDariWaktu() {
        gardenRef.child("watering_time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long hour = snapshot.child("hour").getValue(Long.class);
                Long minute = snapshot.child("minute").getValue(Long.class);

                if (hour == null || minute == null) return;

                long now = System.currentTimeMillis();
                long targetMillis = getNextTimeMillis(hour.intValue(), minute.intValue());

                long diff = targetMillis - now;
                if (diff < 0) return;

                if (countdownTimer != null) countdownTimer.cancel();

                countdownTimer = new CountDownTimer(diff, 1000) {
                    public void onTick(long millisUntilFinished) {
                        long seconds = millisUntilFinished / 1000;
                        long h = seconds / 3600;
                        long m = (seconds % 3600) / 60;
                        long s = seconds % 60;
                        textCountdown.setText(String.format("Next watering in: %02d:%02d:%02d", h, m, s));
                    }

                    public void onFinish() {
                        textCountdown.setText("Watering time now!");
                        new Handler().postDelayed(() -> mulaiCountdownDariWaktu(), 5000);
                    }

                }.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to read watering time", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private long getNextTimeMillis(int hour, int minute) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
        cal.set(java.util.Calendar.MINUTE, minute);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);

        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
        }

        return cal.getTimeInMillis();
    }

    private void aktifkanWaterNow() {
        gardenRef.child("mode").setValue("manual");
        gardenRef.child("manual_request").setValue(true);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Watering plants...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Watering completed", Toast.LENGTH_SHORT).show();
            }

            gardenRef.child("mode").setValue("not_set");
            gardenRef.child("manual_request").setValue(false);

        }, 8000);
    }

    private void simpanHistoryWaterNow() {
        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", null);
        String username = prefs.getString("username", "unknown");
        String role = prefs.getString("role", "user");

        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String activity = "Watering";
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        History history = new History(userId, username, role, activity, timestamp);

        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("history").push();
        historyRef.setValue(history)
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to save history: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
