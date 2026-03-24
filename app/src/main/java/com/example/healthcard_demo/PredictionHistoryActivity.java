package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PredictionHistoryActivity extends AppCompatActivity {

    private PredictionHistoryStore historyStore;
    private ArrayAdapter<String> adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction_history);

        historyStore = new PredictionHistoryStore(this);

        EditText etSearch = findViewById(R.id.et_history_search);
        Switch swSort = findViewById(R.id.sw_sort_newest);
        ListView lvHistory = findViewById(R.id.lv_history);
        tvEmpty = findViewById(R.id.tv_history_empty);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvHistory.setAdapter(adapter);

        refreshList(etSearch.getText().toString(), swSort.isChecked());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshList(s.toString(), swSort.isChecked());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        swSort.setOnCheckedChangeListener((buttonView, isChecked) -> refreshList(etSearch.getText().toString(), isChecked));
    }

    private void refreshList(String query, boolean newestFirst) {
        List<PredictionHistoryItem> items = historyStore.searchAndSort(query, newestFirst);
        List<String> rows = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.US);
        for (PredictionHistoryItem item : items) {
            String description = item.getDescription();
            if (TextUtils.isEmpty(description)) {
                description = "Description unavailable.";
            }
            rows.add(String.format(Locale.US,
                    "%s\nSeverity: %s (Score %d/7)\nPredicted on: %s\n%s",
                    item.getDisease(),
                    item.getSeverity(),
                    item.getSeverityScore(),
                    sdf.format(new Date(item.getTimestamp())),
                    description));
        }
        adapter.clear();
        adapter.addAll(rows);
        tvEmpty.setText(items.isEmpty() ? "No prediction history found yet." : "");
    }
}
