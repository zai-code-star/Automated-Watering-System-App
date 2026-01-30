package com.example.aws;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ForegroundFirebaseService extends Service {

    private boolean lastPumpState = false;
    private String lastMoistureCondition = "";

    private String userId;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, getForegroundNotification("Monitoring watering system..."));

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = prefs.getString("id", null);

        listenToPumpStatus();
        listenToMoistureCondition();
    }

    private android.app.Notification getForegroundNotification(String message) {
        return new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("AWS")
                .setContentText(message)
                .setSmallIcon(R.drawable.img_splash)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id", "FirebaseChannel", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Monitoring changes from Firebase");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void listenToPumpStatus() {
        FirebaseDatabase.getInstance().getReference("zones/garden_a/status/pump_on")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Boolean currentPumpState = snapshot.getValue(Boolean.class);
                        if (currentPumpState != null && currentPumpState != lastPumpState) {
                            lastPumpState = currentPumpState;
                            String message = currentPumpState ? "Watering started" : "Watering finished";
                            showNotification("Watering Status", message);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    private void listenToMoistureCondition() {
        FirebaseDatabase.getInstance().getReference("zones/garden_a/status/moisture/condition")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String condition = snapshot.getValue(String.class);
                        if (condition != null && !condition.equals(lastMoistureCondition)) {
                            lastMoistureCondition = condition;
                            if (condition.equals("low") || condition.equals("high")) {
                                String message = "Soil moisture is " + condition;
                                showNotification("Soil Moisture Alert", message);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.mipmap.ic_logoaws)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());

        if (userId != null) {
            kirimNotifikasi(userId, message);
        }
    }

    private void kirimNotifikasi(String userId, String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        com.example.aws.model.Notification notif = new com.example.aws.model.Notification(userId, message, timestamp);

        DatabaseReference notifRef = FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(userId)
                .push();

        notifRef.setValue(notif);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
