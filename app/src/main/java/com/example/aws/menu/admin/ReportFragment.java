package com.example.aws.menu.admin;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.aws.R;
import com.example.aws.model.Report;
import com.google.firebase.database.*;

import java.util.*;

public class ReportFragment extends Fragment {

    private RecyclerView recyclerReports;
    private ReportAdapter adapter;
    private ArrayList<Report> allReports = new ArrayList<>();
    private ArrayList<Report> filteredReports = new ArrayList<>();
    private ProgressBar progressBar;
    private Spinner spinnerFilter;
    private TextView textEmpty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        recyclerReports = view.findViewById(R.id.recyclerReports);
        progressBar = view.findViewById(R.id.progressBar);
        spinnerFilter = view.findViewById(R.id.spinnerFilter);
        textEmpty = view.findViewById(R.id.textEmpty);

        recyclerReports.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReportAdapter(filteredReports);
        recyclerReports.setAdapter(adapter);

        setupFilter();
        loadReports();

        return view;
    }

    private void setupFilter() {
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"All", "under review", "in progress", "resolved"});
        spinnerFilter.setAdapter(filterAdapter);
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterReports();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadReports() {
        progressBar.setVisibility(View.VISIBLE);
        textEmpty.setVisibility(View.GONE);

        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("report");

        reportRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allReports.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Report report = snap.getValue(Report.class);
                    if (report != null) {
                        report.id = snap.getKey();
                        allReports.add(report);
                    }
                }
                filterReports();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load reports", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterReports() {
        String selected = spinnerFilter.getSelectedItem().toString();
        filteredReports.clear();

        for (Report r : allReports) {
            if (selected.equals("All") || r.status.equals(selected)) {
                filteredReports.add(r);
            }
        }

        adapter.notifyDataSetChanged();

        if (filteredReports.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            textEmpty.setVisibility(View.GONE);
        }
    }

    private class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
        private ArrayList<Report> data;

        ReportAdapter(ArrayList<Report> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
            return new ReportViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
            Report report = data.get(position);
            holder.textUsername.setText(report.username);
            holder.textMessage.setText(report.message);

            holder.itemView.setOnClickListener(v -> showDetailDialog(report));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ReportViewHolder extends RecyclerView.ViewHolder {
            TextView textUsername, textMessage;

            ReportViewHolder(View itemView) {
                super(itemView);
                textUsername = itemView.findViewById(R.id.textUsername);
                textMessage = itemView.findViewById(R.id.textMessage);
            }
        }
    }

    private void showDetailDialog(Report report) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Detail Report");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_report_detail, null);

        TextView txtUser = view.findViewById(R.id.txtUsername);
        TextView txtMessage = view.findViewById(R.id.txtMessage);
        TextView txtTime = view.findViewById(R.id.txtTime);
        Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);

        txtUser.setText("User: " + report.username);
        txtMessage.setText("Message:\n" + report.message);
        txtTime.setText("Time: " + report.timestamp);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"under review", "in progress", "resolved"});
        spinnerStatus.setAdapter(statusAdapter);
        spinnerStatus.setSelection(statusAdapter.getPosition(report.status));

        builder.setView(view);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newStatus = spinnerStatus.getSelectedItem().toString();
            if (!newStatus.equals(report.status)) {
                FirebaseDatabase.getInstance().getReference("report")
                        .child(report.id).child("status").setValue(newStatus)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(getContext(), "Status updated", Toast.LENGTH_SHORT).show());
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
