package com.example.aws.menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aws.R;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        LinearLayout container = findViewById(R.id.settingsContainer);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String role = prefs.getString("role", "User");

        ArrayList<String> settingsItems = new ArrayList<>(Arrays.asList(
                "Account",
                "Notification",
                "Zones",
                "Scheduling",
                "Sensor Monitoring"
        ));

        if (!"Admin".equalsIgnoreCase(role)) {
            settingsItems.add("Activity History");
            settingsItems.add("System Report");
        }

        for (String item : settingsItems) {
            View settingItem = createSettingItem(item);
            container.addView(settingItem);
        }
    }

    private View createSettingItem(String text) {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        wrapper.setPadding(46, 6, 46, 16);

        TextView textView = new TextView(this);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(tvParams);
        textView.setPadding(24, 24, 24, 24);
        textView.setBackgroundResource(R.drawable.rounded_green_background);
        textView.setText(text);
        textView.setTextSize(16);

        textView.setOnClickListener(v -> {
            switch (text) {
                case "Account":
                    startActivity(new Intent(this, AccountPage.class));
                    break;
                case "Zones":
                    startActivity(new Intent(this, ZonesPage.class));
                    break;
                case "Sensor Monitoring":
                    startActivity(new Intent(this, SensorMonitoringPage.class));
                    break;
                case "Scheduling":
                    startActivity(new Intent(this, SchedulingPage.class));
                    break;
                case "Notification":
                    startActivity(new Intent(this, NotificationsPage.class));
                    break;
                case "Activity History":
                    startActivity(new Intent(this, HistoryPage.class));
                    break;
                case "System Report":
                    startActivity(new Intent(this, ReportSystemPage.class));
                    break;
                default:
                    Toast.makeText(this, text + " clicked", Toast.LENGTH_SHORT).show();
            }
        });

        wrapper.addView(textView);
        return wrapper;
    }
}
