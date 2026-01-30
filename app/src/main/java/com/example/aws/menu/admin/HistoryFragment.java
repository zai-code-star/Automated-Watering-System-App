package com.example.aws.menu.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aws.R;
import com.example.aws.model.History;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private ListView listHistoryAll;
    private TextView textEmptyMessage;
    private ProgressBar progressBar;
    private CustomHistoryAdapter adapter;
    private ArrayList<History> historyList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        listHistoryAll = view.findViewById(R.id.listHistoryAll);
        textEmptyMessage = view.findViewById(R.id.textEmptyMessage);
        progressBar = view.findViewById(R.id.progressBarHistory);

        historyList = new ArrayList<>();
        adapter = new CustomHistoryAdapter();
        listHistoryAll.setAdapter(adapter);

        loadAllUserHistories();

        return view;
    }

    private void loadAllUserHistories() {
        progressBar.setVisibility(View.VISIBLE);
        listHistoryAll.setVisibility(View.GONE);
        textEmptyMessage.setVisibility(View.GONE);

        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("history");

        historyRef.get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful() && task.getResult().exists()) {
                historyList.clear();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    History h = snapshot.getValue(History.class);
                    if (h != null) {
                        historyList.add(h);
                    }
                }

                if (historyList.isEmpty()) {
                    textEmptyMessage.setVisibility(View.VISIBLE);
                    listHistoryAll.setVisibility(View.GONE);
                } else {
                    textEmptyMessage.setVisibility(View.GONE);
                    listHistoryAll.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            } else {
                textEmptyMessage.setText("No Activity.");
                textEmptyMessage.setVisibility(View.VISIBLE);
                listHistoryAll.setVisibility(View.GONE);
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            textEmptyMessage.setText("Gagal memuat data.");
            textEmptyMessage.setVisibility(View.VISIBLE);
            listHistoryAll.setVisibility(View.GONE);
        });
    }

    private class CustomHistoryAdapter extends ArrayAdapter<History> {

        public CustomHistoryAdapter() {
            super(requireContext(), R.layout.item_history, historyList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                row = LayoutInflater.from(getContext()).inflate(R.layout.item_history, parent, false);
            }

            TextView textMessage = row.findViewById(R.id.textMessage);
            TextView textTime = row.findViewById(R.id.textTime);

            History h = historyList.get(position);
            textMessage.setText(h.username + " do " + h.activity);
            textTime.setText(h.timestamp);

            return row;
        }
    }
}
