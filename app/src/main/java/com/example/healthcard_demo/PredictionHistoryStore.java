package com.example.healthcard_demo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PredictionHistoryStore {
    private static final String PREFS = "prediction_history_prefs";
    private static final String KEY_ITEMS = "items";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public PredictionHistoryStore(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void addItem(PredictionHistoryItem item) {
        List<PredictionHistoryItem> items = getAllItems();
        items.add(item);
        sharedPreferences.edit().putString(KEY_ITEMS, gson.toJson(items)).apply();
    }

    public List<PredictionHistoryItem> getAllItems() {
        String json = sharedPreferences.getString(KEY_ITEMS, "[]");
        Type type = new TypeToken<List<PredictionHistoryItem>>() {}.getType();
        List<PredictionHistoryItem> items = gson.fromJson(json, type);
        return items == null ? new ArrayList<>() : items;
    }

    public List<PredictionHistoryItem> searchAndSort(String query, boolean newestFirst) {
        List<PredictionHistoryItem> results = new ArrayList<>();
        String safeQuery = query == null ? "" : query.trim().toLowerCase();
        for (PredictionHistoryItem item : getAllItems()) {
            String disease = item.getDisease() == null ? "" : item.getDisease().toLowerCase();
            if (safeQuery.isEmpty() || disease.contains(safeQuery)) {
                results.add(item);
            }
        }

        Comparator<PredictionHistoryItem> comparator = Comparator.comparingLong(PredictionHistoryItem::getTimestamp);
        if (newestFirst) {
            comparator = comparator.reversed();
        }
        Collections.sort(results, comparator);
        return results;
    }
}
