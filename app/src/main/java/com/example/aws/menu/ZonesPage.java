package com.example.aws.menu;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aws.R;

public class ZonesPage extends AppCompatActivity {

    String[] zones = {
            "Garden A"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zones_page);

        LinearLayout container = findViewById(R.id.zonesContainer);

        for (String zone : zones) {
            View item = createZoneItem(zone);
            container.addView(item);
        }
    }

    private View createZoneItem(String name) {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        wrapper.setPadding(0, 0, 0, 46);

        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                600,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        textView.setPadding(16, 46, 16, 46);
        textView.setBackgroundResource(R.drawable.rounded_green_background);
        textView.setText(name);
        textView.setTextSize(14);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        wrapper.addView(textView);
        return wrapper;
    }
}
