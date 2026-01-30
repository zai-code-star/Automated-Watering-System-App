package com.example.aws.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aws.R;
import com.example.aws.model.Report;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReportSystemPage extends AppCompatActivity {

    private EditText editMessage;
    private Button buttonSendReport;
    private ListView listReport;
    private TextView textLabel, textNoReport;
    private ProgressBar progressLoading;

    private ArrayList<String> reportMessages;
    private ArrayAdapter<String> adapter;

    private String userId, username;

    private DatabaseReference reportRef;
    private ValueEventListener reportListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_system_page);

        editMessage = findViewById(R.id.editMessage);
        buttonSendReport = findViewById(R.id.buttonSendReport);
        listReport = findViewById(R.id.listReport);
        textLabel = findViewById(R.id.textReportLabel);
        textNoReport = findViewById(R.id.textNoReport);
        progressLoading = findViewById(R.id.progressLoading);

        reportMessages = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reportMessages);
        listReport.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = prefs.getString("id", null);
        username = prefs.getString("username", "unknown");

        reportRef = FirebaseDatabase.getInstance().getReference("report");

        buttonSendReport.setOnClickListener(v -> sendReport());

        if (userId != null) {
            listenToReports();
        }
    }

    private void sendReport() {
        String message = editMessage.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(this, "The message cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String reportId = UUID.randomUUID().toString();
        DatabaseReference ref = reportRef.child(reportId);

        Report report = new Report(userId, username, message, "", "under review", timestamp);
        ref.setValue(report).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Sent", Toast.LENGTH_SHORT).show();
            editMessage.setText("");
        });
    }

    private void listenToReports() {
        progressLoading.setVisibility(View.VISIBLE);
        reportListener = reportRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportMessages.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Report report = snap.getValue(Report.class);
                        if (report != null && userId.equals(report.user_id)) {
                            reportMessages.add("Your report: " + report.message + "\nStatus: " + report.status);
                        }
                    }

                    if (!reportMessages.isEmpty()) {
                        textLabel.setVisibility(View.VISIBLE);
                        textNoReport.setVisibility(View.GONE);
                        listReport.setVisibility(View.VISIBLE);
                    } else {
                        textLabel.setVisibility(View.GONE);
                        textNoReport.setVisibility(View.VISIBLE);
                        listReport.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    textLabel.setVisibility(View.GONE);
                    textNoReport.setVisibility(View.VISIBLE);
                    listReport.setVisibility(View.GONE);
                }

                progressLoading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportSystemPage.this, "Failed to load report", Toast.LENGTH_SHORT).show();
                progressLoading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reportRef != null && reportListener != null) {
            reportRef.removeEventListener(reportListener);
        }
    }
}
