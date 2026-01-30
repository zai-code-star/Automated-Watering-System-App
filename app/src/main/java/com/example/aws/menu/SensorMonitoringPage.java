package com.example.aws.menu;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aws.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SensorMonitoringPage extends AppCompatActivity {

    private TextView textTemperatureValue, textHumidityValue;
    private TextView textMoistureCondition, textMoistureValue;
    private ProgressBar loadingIndicator;
    private View flexLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_monitoring_page);

        textTemperatureValue = findViewById(R.id.textTemperatureValue);
        textHumidityValue = findViewById(R.id.textHumidityValue);
        textMoistureCondition = findViewById(R.id.textMoistureCondition);
        textMoistureValue = findViewById(R.id.textMoistureValue);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        flexLayout = findViewById(R.id.flexLayout);

        FirebaseDatabase.getInstance().getReference("zones/garden_a/status")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            flexLayout.setVisibility(View.GONE);
                            loadingIndicator.setVisibility(View.VISIBLE);

                            Double temperature = snapshot.child("temperature").getValue(Double.class);
                            Double humidity = snapshot.child("humidity").getValue(Double.class);
                            Integer moistureValue = snapshot.child("moisture/value").getValue(Integer.class);
                            String moistureCondition = snapshot.child("moisture/condition").getValue(String.class);

                            flexLayout.postDelayed(() -> {
                                if (temperature != null)
                                    textTemperatureValue.setText(String.format("%.1f °C", temperature));
                                if (humidity != null)
                                    textHumidityValue.setText(String.format("%.0f %%", humidity));
                                if (moistureValue != null)
                                    textMoistureValue.setText(moistureValue.toString());
                                if (moistureCondition != null)
                                    textMoistureCondition.setText(moistureCondition);

                                loadingIndicator.setVisibility(View.GONE);
                                flexLayout.setVisibility(View.VISIBLE);
                            }, 500);
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingIndicator.setVisibility(View.GONE);
                    }
                });
    }
}
