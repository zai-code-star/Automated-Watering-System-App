package com.example.aws.menu.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aws.R;
import com.example.aws.adapter.UserAdapter;
import com.example.aws.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ManageAccountFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private UserAdapter userAdapter;
    private List<User> userList;
    private DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_account, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = view.findViewById(R.id.progressBarLoading);

        FloatingActionButton fabAddUser = view.findViewById(R.id.fabAddUser);
        fabAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddUserActivity.class);
            startActivity(intent);
        });

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, user -> {
            Intent intent = new Intent(requireContext(), EditDeleteUserActivity.class);
            intent.putExtra("username", user.username);
            intent.putExtra("password", user.password);
            intent.putExtra("role", user.role);
            intent.putExtra("id", user.id);
            startActivity(intent);
        });

        recyclerView.setAdapter(userAdapter);
        userRef = FirebaseDatabase.getInstance().getReference("users");

        loadUserData();
        return view;
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user != null) {
                        user.id = child.getKey();
                        userList.add(user);
                    }
                }

                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),
                        "Gagal memuat data: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }
}
