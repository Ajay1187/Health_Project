package com.example.healthcard_demo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class HealthInsuranceManager {

    private static final String PREFS = "health_insurance_prefs";
    private static final String KEY_PREFIX = "policy_";

    public static HealthInsuranceDocument getOrCreateDocument(Context context, String medicalId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String key = KEY_PREFIX + medicalId;

        String json = prefs.getString(key, null);
        if (json != null) {
            HealthInsuranceDocument existing = gson.fromJson(json, HealthInsuranceDocument.class);
            if (existing != null) {
                return existing;
            }
        }

        HealthInsuranceDocument generated = generateDocument(medicalId);
        prefs.edit().putString(key, gson.toJson(generated)).apply();
        return generated;
    }

    private static HealthInsuranceDocument generateDocument(String medicalId) {
        String safeId = medicalId == null ? "UNKNOWN" : medicalId;
        int seed = Math.abs(safeId.hashCode());
        Random random = new Random(seed);

        String[] providers = {"Star Health", "HDFC Ergo", "ICICI Lombard", "Niva Bupa", "Care Health"};
        String[] plans = {"Silver Care", "Gold Shield", "Family Secure", "Wellness Plus", "Premium Protect"};
        String[] insured = {"₹2,00,000", "₹3,00,000", "₹5,00,000", "₹7,00,000", "₹10,00,000"};

        HealthInsuranceDocument doc = new HealthInsuranceDocument();
        doc.setMedicalId(safeId);
        doc.setPolicyNumber(String.format(Locale.US, "SHC-%s-%04d", safeId, random.nextInt(10000)));
        doc.setProviderName(providers[random.nextInt(providers.length)]);
        doc.setPlanName(plans[random.nextInt(plans.length)]);
        doc.setSumInsured(insured[random.nextInt(insured.length)]);
        doc.setPremium(String.format(Locale.US, "₹%d / year", 2500 + random.nextInt(8500)));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1 + random.nextInt(3));
        doc.setValidTill(String.format(Locale.US, "%02d-%02d-%04d",
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR)));
        doc.setSupportNumber(String.format(Locale.US, "+91-1800-10%03d", random.nextInt(1000)));

        return doc;
    }
}
