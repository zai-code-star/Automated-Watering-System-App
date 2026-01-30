package com.example.aws.menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aws.R;
import com.example.aws.adapter.NotificationAdapter;
import com.example.aws.model.Notification;
import com.google.firebase.database.*;

import java.util.*;

public class NotificationsPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textNoNotification;
    private NotificationAdapter adapter;
    private List<Notification> notificationList = new ArrayList<>();
    private DatabaseReference notifRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_page);

        recyclerView = findViewById(R.id.recyclerNotifications);
        textNoNotification = findViewById(R.id.textNoNotification);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = prefs.getString("id", null);

        if (userId != null) {
            notifRef = FirebaseDatabase.getInstance().getReference("notifications").child(userId);

            notifRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    notificationList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Notification notif = dataSnapshot.getValue(Notification.class);
                        notificationList.add(notif);
                    }

                    Collections.reverse(notificationList);
                    adapter.notifyDataSetChanged();

                    if (notificationList.isEmpty()) {
                        textNoNotification.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        textNoNotification.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}
