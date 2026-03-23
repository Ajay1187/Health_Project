"""Train a lightweight 1D CNN for symptom-based disease classification.

Supports two CSV schemas:
1) Numeric features (sample_disease_dataset.csv):
   - Columns: fever,cough,...,duration_days,disease
2) Text features (medical_disease_dataset.csv):
   - Columns: Symptoms,Severity,Duration(days),Predicted_Disease

Usage examples:
python ml/train_cnn_model.py --data ml/sample_disease_dataset.csv --out-dir ml/output
python ml/train_cnn_model.py --data ml/medical_disease_dataset.csv --out-dir ml/output
"""

import argparse
import json
import os

import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder


FEATURE_COLUMNS = [
    "itching",
    "skin_rash",
    "nodal_skin_eruptions",
    "continuous_sneezing",
    "shivering",
    "chills",
    "joint_pain",
    "stomach_pain",
    "acidity",
    "ulcers_on_tongue",
    "muscle_wasting",
    "vomiting",
    "burning_micturition",
    "spotting_urination",
    "fatigue",
    "weight_gain",
    "anxiety",
    "cold_hands_and_feets",
    "mood_swings",
    "weight_loss",
    "restlessness",
    "lethargy",
    "patches_in_throat",
    "irregular_sugar_level",
    "cough",
    "high_fever",
    "sunken_eyes",
    "breathlessness",
    "sweating",
    "dehydration",
    "indigestion",
    "headache",
    "yellowish_skin",
    "dark_urine",
    "nausea",
    "loss_of_appetite",
    "pain_behind_the_eyes",
    "back_pain",
    "constipation",
    "abdominal_pain",
    "diarrhoea",
    "mild_fever",
    "yellow_urine",
    "yellowing_of_eyes",
    "acute_liver_failure",
    "fluid_overload",
    "swelling_of_stomach",
    "swelled_lymph_nodes",
    "malaise",
    "blurred_and_distorted_vision",
    "phlegm",
    "throat_irritation",
    "redness_of_eyes",
    "sinus_pressure",
    "runny_nose",
    "congestion",
    "chest_pain",
    "weakness_in_limbs",
    "fast_heart_rate",
    "pain_during_bowel_movements",
    "pain_in_anal_region",
    "bloody_stool",
    "irritation_in_anus",
    "neck_pain",
    "dizziness",
    "cramps",
    "bruising",
    "obesity",
    "swollen_legs",
    "swollen_blood_vessels",
    "puffy_face_and_eyes",
    "enlarged_thyroid",
    "brittle_nails",
    "swollen_extremeties",
    "excessive_hunger",
    "extra_marital_contacts",
    "drying_and_tingling_lips",
    "slurred_speech",
    "knee_pain",
    "hip_joint_pain",
    "muscle_weakness",
    "stiff_neck",
    "swelling_joints",
    "movement_stiffness",
    "spinning_movements",
    "loss_of_balance",
    "unsteadiness",
    "weakness_of_one_body_side",
    "loss_of_smell",
    "bladder_discomfort",
    "foul_smell_ofurine",
    "continuous_feel_of_urine",
    "passage_of_gases",
    "internal_itching",
    "toxic_look_(typhos)",
    "depression",
    "irritability",
    "muscle_pain",
    "altered_sensorium",
    "red_spots_over_body",
    "belly_pain",
    "abnormal_menstruation",
    "dischromic_patches",
    "watering_from_eyes",
    "increased_appetite",
    "polyuria",
    "family_history",
    "mucoid_sputum",
    "rusty_sputum",
    "lack_of_concentration",
    "visual_disturbances",
    "receiving_blood_transfusion",
    "receiving_unsterile_injections",
    "coma",
    "stomach_bleeding",
    "distention_of_abdomen",
    "history_of_alcohol_consumption",
    "blood_in_sputum",
    "prominent_veins_on_calf",
    "palpitations",
    "painful_walking",
    "pus_filled_pimples",
    "blackheads",
    "scurring",
    "skin_peeling",
    "silver_like_dusting",
    "small_dents_in_nails",
    "inflammatory_nails",
    "blister",
    "red_sore_around_nose",
    "yellow_crust_ooze"
]

# Textual dataset schema (medical_disease_dataset.csv)
SYMPTOM_TEXT_COLUMN = "Symptoms"
SEVERITY_TEXT_COLUMN = "Severity"
DURATION_TEXT_COLUMN = "Duration(days)"
PREDICTED_DISEASE_COLUMN = "Predicted_Disease"


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--data", required=True, help="CSV dataset path")
    parser.add_argument("--out-dir", required=True, help="Output directory")
    parser.add_argument("--epochs", type=int, default=30)
    parser.add_argument(
        "--batch-size",
        type=int,
        default=32,
        help="Training batch size (default: 32)",
    )
    return parser.parse_args()


def build_model(input_length, num_classes):
    model = tf.keras.Sequential([
        tf.keras.layers.Input(shape=(input_length, 1)),
        tf.keras.layers.Conv1D(32, 3, activation="relu", padding="same"),
        tf.keras.layers.MaxPooling1D(2),
        tf.keras.layers.Conv1D(64, 3, activation="relu", padding="same"),
        tf.keras.layers.GlobalAveragePooling1D(),
        tf.keras.layers.Dense(64, activation="relu"),
        tf.keras.layers.Dropout(0.2),
        tf.keras.layers.Dense(num_classes, activation="softmax"),
    ])
    model.compile(optimizer="adam", loss="sparse_categorical_crossentropy", metrics=["accuracy"])
    return model


def _prepare_numeric_dataset(df: pd.DataFrame):
    """Prepare data when CSV already has numeric feature columns."""
    missing = [c for c in FEATURE_COLUMNS + ["disease"] if c not in df.columns]
    if missing:
        raise ValueError(f"Numeric dataset is missing required columns: {missing}")

    df = df.dropna(subset=FEATURE_COLUMNS + ["disease"])

    X = df[FEATURE_COLUMNS].values.astype(np.float32)
    y = df["disease"].values

    feature_names = FEATURE_COLUMNS
    metadata = {"feature_names": feature_names}
    return X, y, feature_names, metadata


def _prepare_textual_dataset(df: pd.DataFrame):
    """Prepare data from textual symptoms/severity schema."""
    required = [
        SYMPTOM_TEXT_COLUMN,
        SEVERITY_TEXT_COLUMN,
        DURATION_TEXT_COLUMN,
        PREDICTED_DISEASE_COLUMN,
    ]
    missing = [c for c in required if c not in df.columns]
    if missing:
        raise ValueError(f"Textual dataset is missing required columns: {missing}")

    df = df.dropna(subset=required).copy()

    # Normalize label and duration column names to align with numeric schema.
    df.rename(
        columns={
            PREDICTED_DISEASE_COLUMN: "disease",
            DURATION_TEXT_COLUMN: "duration_days",
        },
        inplace=True,
    )

    # Map textual severity to a numeric score similar to the sample dataset.
    severity_map = {"Low": 0.33, "Medium": 0.66, "High": 1.0}
    df["severity_numeric"] = df[SEVERITY_TEXT_COLUMN].map(severity_map)
    if df["severity_numeric"].isna().any():
        unknown_values = sorted(df.loc[df["severity_numeric"].isna(), SEVERITY_TEXT_COLUMN].unique())
        raise ValueError(f"Unexpected severity values encountered: {unknown_values}")

    # Build a symptom vocabulary from all rows.
    symptom_series = df[SYMPTOM_TEXT_COLUMN].astype(str)
    vocab_set = set()
    for val in symptom_series:
        for token in val.split("|"):
            token_clean = token.strip()
            if token_clean:
                vocab_set.add(token_clean.lower().replace(" ", "_"))

    symptom_vocab = sorted(vocab_set)

    def encode_symptoms(symptom_str: str):
        tokens = {
            t.strip().lower().replace(" ", "_")
            for t in symptom_str.split("|")
            if t.strip()
        }
        return [1.0 if v in tokens else 0.0 for v in symptom_vocab]

    symptom_matrix = np.array(
        [encode_symptoms(s) for s in symptom_series], dtype=np.float32
    )

    severity_col = df["severity_numeric"].astype(np.float32).to_numpy().reshape(-1, 1)
    duration_col = df["duration_days"].astype(np.float32).to_numpy().reshape(-1, 1)

    X = np.concatenate([symptom_matrix, severity_col, duration_col], axis=1)
    y = df["disease"].to_numpy()

    feature_names = [f"symptom_{s}" for s in symptom_vocab] + [
        "severity",
        "duration_days",
    ]

    metadata = {
        "feature_names": feature_names,
        "symptom_vocabulary": symptom_vocab,
        "severity_mapping": severity_map,
        "schema_type": "textual",
    }
    return X, y, feature_names, metadata


def _prepare_dataset(df: pd.DataFrame):
    """Auto-detect schema and return (X, y, feature_names, metadata)."""
    if SYMPTOM_TEXT_COLUMN in df.columns and PREDICTED_DISEASE_COLUMN in df.columns:
        return _prepare_textual_dataset(df)
    if "disease" in df.columns and all(c in df.columns for c in FEATURE_COLUMNS):
        return _prepare_numeric_dataset(df)
    raise ValueError(
        "Unrecognized dataset schema. Expected either numeric FEATURE_COLUMNS with "
        "'disease' or textual columns "
        f"{[SYMPTOM_TEXT_COLUMN, SEVERITY_TEXT_COLUMN, DURATION_TEXT_COLUMN, PREDICTED_DISEASE_COLUMN]}."
    )


def main():
    args = parse_args()
    os.makedirs(args.out_dir, exist_ok=True)

    df = pd.read_csv(args.data)

    X, y, feature_names, metadata = _prepare_dataset(df)

    encoder = LabelEncoder()
    y_encoded = encoder.fit_transform(y)

    X_train, X_test, y_train, y_test = train_test_split(
        X, y_encoded, test_size=0.2, random_state=42, stratify=y_encoded
    )

    X_train = np.expand_dims(X_train, axis=-1)
    X_test = np.expand_dims(X_test, axis=-1)

    model = build_model(X_train.shape[1], len(encoder.classes_))
    model.fit(
        X_train,
        y_train,
        epochs=args.epochs,
        batch_size=args.batch_size,
        validation_split=0.1,
        verbose=2,
    )

    loss, acc = model.evaluate(X_test, y_test, verbose=0)
    print(f"Test accuracy: {acc:.4f}")

    model_path = os.path.join(args.out_dir, "disease_cnn.keras")
    model.save(model_path)

    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()
    tflite_path = os.path.join(args.out_dir, "disease_cnn.tflite")
    with open(tflite_path, "wb") as f:
        f.write(tflite_model)

    labels_path = os.path.join(args.out_dir, "labels.txt")
    with open(labels_path, "w", encoding="utf-8") as f:
        for item in encoder.classes_:
            f.write(item + "\n")

    # Persist basic feature configuration so Android or other clients can
    # construct input tensors consistently with training.
    feature_config = {"feature_names": feature_names}
    feature_config.update(metadata or {})
    feature_config_path = os.path.join(args.out_dir, "feature_config.json")
    with open(feature_config_path, "w", encoding="utf-8") as f:
        json.dump(feature_config, f, indent=2)

    metrics_path = os.path.join(args.out_dir, "metrics.txt")
    with open(metrics_path, "w", encoding="utf-8") as f:
        f.write(f"test_accuracy={acc:.4f}\n")
        f.write(f"test_loss={loss:.4f}\n")


if __name__ == "__main__":
    main()
