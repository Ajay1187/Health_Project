"""Train a scikit-learn disease classifier from ml/dataset.csv.

Usage:
python ml/train_model_sklearn.py --data ml/dataset.csv --out-dir ml/output
"""

import argparse
import json
import os

import joblib
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder

FEATURE_COLUMNS = [
    "itching", "skin_rash", "nodal_skin_eruptions", "continuous_sneezing", "shivering",
    "chills", "joint_pain", "stomach_pain", "acidity", "ulcers_on_tongue",
    "muscle_wasting", "vomiting", "burning_micturition", "spotting_urination", "fatigue",
    "weight_gain", "anxiety", "cold_hands_and_feets", "mood_swings", "weight_loss",
    "restlessness", "lethargy", "patches_in_throat", "irregular_sugar_level", "cough",
    "high_fever", "sunken_eyes", "breathlessness", "sweating", "dehydration",
    "indigestion", "headache", "yellowish_skin", "dark_urine", "nausea",
    "loss_of_appetite", "pain_behind_the_eyes", "back_pain", "constipation", "abdominal_pain",
    "diarrhoea", "mild_fever", "yellow_urine", "yellowing_of_eyes", "acute_liver_failure",
    "fluid_overload", "swelling_of_stomach", "swelled_lymph_nodes", "malaise",
    "blurred_and_distorted_vision", "phlegm", "throat_irritation", "redness_of_eyes", "sinus_pressure",
    "runny_nose", "congestion", "chest_pain", "weakness_in_limbs", "fast_heart_rate",
    "pain_during_bowel_movements", "pain_in_anal_region", "bloody_stool", "irritation_in_anus",
    "neck_pain", "dizziness", "cramps", "bruising", "obesity", "swollen_legs",
    "swollen_blood_vessels", "puffy_face_and_eyes", "enlarged_thyroid", "brittle_nails",
    "swollen_extremeties", "excessive_hunger", "extra_marital_contacts", "drying_and_tingling_lips",
    "slurred_speech", "knee_pain", "hip_joint_pain", "muscle_weakness", "stiff_neck",
    "swelling_joints", "movement_stiffness", "spinning_movements", "loss_of_balance", "unsteadiness",
    "weakness_of_one_body_side", "loss_of_smell", "bladder_discomfort", "foul_smell_ofurine",
    "continuous_feel_of_urine", "passage_of_gases", "internal_itching", "toxic_look_(typhos)",
    "depression", "irritability", "muscle_pain", "altered_sensorium", "red_spots_over_body",
    "belly_pain", "abnormal_menstruation", "dischromic_patches", "watering_from_eyes", "increased_appetite",
    "polyuria", "family_history", "mucoid_sputum", "rusty_sputum", "lack_of_concentration",
    "visual_disturbances", "receiving_blood_transfusion", "receiving_unsterile_injections", "coma",
    "stomach_bleeding", "distention_of_abdomen", "history_of_alcohol_consumption", "blood_in_sputum",
    "prominent_veins_on_calf", "palpitations", "painful_walking", "pus_filled_pimples", "blackheads",
    "scurring", "skin_peeling", "silver_like_dusting", "small_dents_in_nails", "inflammatory_nails",
    "blister", "red_sore_around_nose", "yellow_crust_ooze",
]
FEATURE_SET = set(FEATURE_COLUMNS)


def normalize_symptom(value: str) -> str:
    return str(value or "").strip().lower().replace(" ", "_")


def prepare_dataframe_from_dataset(df: pd.DataFrame) -> pd.DataFrame:
    if "Disease" not in df.columns:
        raise ValueError("Expected dataset.csv schema with a 'Disease' column.")

    symptom_columns = [column for column in df.columns if column.startswith("Symptom_")]
    if not symptom_columns:
        raise ValueError("Expected dataset.csv schema with Symptom_1..Symptom_N columns.")

    rows = []
    for _, row in df.iterrows():
        disease_name = str(row.get("Disease", "")).strip()
        if not disease_name:
            continue

        encoded = {feature: 0.0 for feature in FEATURE_COLUMNS}
        for column in symptom_columns:
            symptom = normalize_symptom(row.get(column, ""))
            if symptom in FEATURE_SET:
                encoded[symptom] = 1.0
        encoded["disease"] = disease_name
        rows.append(encoded)

    prepared_df = pd.DataFrame(rows)
    if prepared_df.empty:
        raise ValueError("No training rows were produced from dataset.csv")
    return prepared_df


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--data", required=True)
    parser.add_argument("--out-dir", required=True)
    return parser.parse_args()


def main():
    args = parse_args()
    os.makedirs(args.out_dir, exist_ok=True)

    raw_df = pd.read_csv(args.data)
    df = prepare_dataframe_from_dataset(raw_df)

    # ✅ Normalize dataset
    df = prepare_dataframe_from_dataset(raw_df)

    # Features & target
    X = df[FEATURE_COLUMNS]
    y = df["disease"]

    # Encode labels
    label_encoder = LabelEncoder()
    y_encoded = label_encoder.fit_transform(y)

    # Train/test split
    X_train, X_test, y_train, y_test = train_test_split(
        X, y_encoded, test_size=0.2, random_state=42, stratify=y_encoded
    )

    # Train model
    model = RandomForestClassifier(n_estimators=200, random_state=42)
    model.fit(X_train, y_train)

    # Evaluate
    preds = model.predict(X_test)
    acc = accuracy_score(y_test, preds)

    # Save model
    joblib.dump(model, os.path.join(args.out_dir, "disease_rf_model.joblib"))
    joblib.dump(label_encoder, os.path.join(args.out_dir, "label_encoder.joblib"))

    with open(os.path.join(args.out_dir, "feature_config_sklearn.json"), "w", encoding="utf-8") as handle:
        json.dump({"feature_names": FEATURE_COLUMNS}, handle, indent=2)

    with open(os.path.join(args.out_dir, "metrics_sklearn.txt"), "w", encoding="utf-8") as handle:
        handle.write(f"test_accuracy={acc:.4f}\n")
        handle.write(f"training_samples={len(X_train)}\n")
        handle.write(f"test_samples={len(X_test)}\n")

    print(f"Training complete. Accuracy={acc:.4f}")


if __name__ == "__main__":
    main()