package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PredictionHistoryActivity extends AppCompatActivity {

    private PredictionHistoryStore historyStore;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction_history);

        historyStore = new PredictionHistoryStore(this);

        EditText etSearch = findViewById(R.id.et_history_search);
        Switch swSort = findViewById(R.id.sw_sort_newest);
        ListView lvHistory = findViewById(R.id.lv_history);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvHistory.setAdapter(adapter);

        refreshList(etSearch.getText().toString(), swSort.isChecked());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshList(s.toString(), swSort.isChecked());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        swSort.setOnCheckedChangeListener((buttonView, isChecked) -> refreshList(etSearch.getText().toString(), isChecked));
    }

    private void refreshList(String query, boolean newestFirst) {
        List<PredictionHistoryItem> items = historyStore.searchAndSort(query, newestFirst);
        List<String> rows = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US);
        for (PredictionHistoryItem item : items) {
            rows.add(String.format(Locale.US,
                    "%s | Conf: %.2f%% | Severity: %s\n%s",
                    item.getDisease(),
                    item.getConfidence() * 100f,
                    item.getSeverity(),
                    sdf.format(new Date(item.getTimestamp()))));
        }
        adapter.clear();
        adapter.addAll(rows);
    }
}
