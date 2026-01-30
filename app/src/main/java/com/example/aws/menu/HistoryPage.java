package com.example.aws.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aws.R;
import com.example.aws.model.History;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HistoryPage extends AppCompatActivity {

    private ListView listHistory;
    private TextView textEmpty;
    private ProgressBar progressBar;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> historyMessages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_page);

        listHistory = findViewById(R.id.listHistory);
        textEmpty = findViewById(R.id.textEmpty);
        progressBar = findViewById(R.id.progressBar);
        historyMessages = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyMessages);
        listHistory.setAdapter(adapter);

        loadUserHistory();
    }

    private void loadUserHistory() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", null);

        if (userId == null) {
            textEmpty.setText("User not logged in.");
            textEmpty.setVisibility(View.VISIBLE);
            listHistory.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        textEmpty.setVisibility(View.GONE);
        listHistory.setVisibility(View.GONE);

        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("history");

        historyRef.get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);

            if (task.isSuccessful() && task.getResult().exists()) {
                historyMessages.clear();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    History h = snapshot.getValue(History.class);
                    if (h != null && userId.equals(h.user_id)) {
                        String msg = "You do " + h.activity + " at " + h.timestamp;
                        historyMessages.add(msg);
                    }
                }

                if (historyMessages.isEmpty()) {
                    textEmpty.setText("No Activity.");
                    textEmpty.setVisibility(View.VISIBLE);
                    listHistory.setVisibility(View.GONE);
                } else {
                    textEmpty.setVisibility(View.GONE);
                    listHistory.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            } else {
                textEmpty.setText("No Activity.");
                textEmpty.setVisibility(View.VISIBLE);
                listHistory.setVisibility(View.GONE);
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            textEmpty.setText("No Activity: " + e.getMessage());
            textEmpty.setVisibility(View.VISIBLE);
            listHistory.setVisibility(View.GONE);
        });
    }
}
