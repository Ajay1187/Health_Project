"""
Train a lightweight 1D CNN for symptom-based disease classification.

Supports two CSV schemas:
1) Numeric features
2) Text features

Supports:
1) dataset.csv disease/symptom-column schema
2) textual Symptoms/Severity/Duration(days)/Predicted_Disease schema

Supports:
1) dataset.csv disease/symptom-column schema
2) textual Symptoms/Severity/Duration(days)/Predicted_Disease schema

Supports:
1) dataset.csv disease/symptom-column schema
2) textual Symptoms/Severity/Duration(days)/Predicted_Disease schema

Supports:
1) dataset.csv disease/symptom-column schema
2) textual Symptoms/Severity/Duration(days)/Predicted_Disease schema

Usage:
python ml/train_cnn_model.py --data ml/dataset.csv --out-dir ml/output
"""

import argparse
import json
import os

import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder

from training_data_utils import FEATURE_COLUMNS, prepare_dataframe_from_dataset

SYMPTOM_TEXT_COLUMN = "Symptoms"
SEVERITY_TEXT_COLUMN = "Severity"
DURATION_TEXT_COLUMN = "Duration(days)"
PREDICTED_DISEASE_COLUMN = "Predicted_Disease"


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--data", required=True)
    parser.add_argument("--out-dir", required=True)
    parser.add_argument("--epochs", type=int, default=30)
    parser.add_argument("--batch-size", type=int, default=32)
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
    model.compile(optimizer="adam",
                  loss="sparse_categorical_crossentropy",
                  metrics=["accuracy"])
    return model


def prepare_dataset(df):
    if SYMPTOM_TEXT_COLUMN in df.columns:
        severity_map = {"Low": 0.33, "Medium": 0.66, "High": 1.0}

        vocab = set()
        for value in df[SYMPTOM_TEXT_COLUMN].dropna():
            for token in str(value).split("|"):
                normalized = token.strip().lower().replace(" ", "_")
                if normalized:
                    vocab.add(normalized)
        vocab = sorted(vocab)

        def encode(symptom_text):
            tokens = {
                token.strip().lower().replace(" ", "_")
                for token in str(symptom_text).split("|")
                if token.strip()
            }
            return [1.0 if vocab_item in tokens else 0.0 for vocab_item in vocab]

        symptom_matrix = np.array([encode(value) for value in df[SYMPTOM_TEXT_COLUMN]], dtype=np.float32)
        severity = df[SEVERITY_TEXT_COLUMN].map(severity_map).astype(np.float32).to_numpy().reshape(-1, 1)
        duration = df[DURATION_TEXT_COLUMN].astype(np.float32).to_numpy().reshape(-1, 1)

        X = np.concatenate([symptom_matrix, severity, duration], axis=1)
        y = df[PREDICTED_DISEASE_COLUMN].to_numpy()
        feature_names = vocab + ["severity", "duration_days"]
        return X, y, feature_names

    if "Disease" in df.columns:
        prepared_df = prepare_dataframe_from_dataset(df)
        X = prepared_df[FEATURE_COLUMNS].values.astype(np.float32)
        y = prepared_df["disease"].values
        return X, y, FEATURE_COLUMNS

    X = df[FEATURE_COLUMNS].values.astype(np.float32)
    y = df["disease"].values
    return X, y, FEATURE_COLUMNS


def main():
    args = parse_args()
    os.makedirs(args.out_dir, exist_ok=True)

    df = pd.read_csv(args.data)
    X, y, feature_names = prepare_dataset(df)

    encoder = LabelEncoder()
    y_encoded = encoder.fit_transform(y)

    X_train, X_test, y_train, y_test = train_test_split(
        X, y_encoded, test_size=0.2, random_state=42
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

    model.fit(
        X_train, y_train,
        epochs=args.epochs,
        batch_size=args.batch_size,
        validation_split=0.1,
        verbose=2
    )

    model.save(os.path.join(args.out_dir, "disease_cnn.keras"))

    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()
    with open(os.path.join(args.out_dir, "disease_cnn.tflite"), "wb") as handle:
        handle.write(tflite_model)

    with open(os.path.join(args.out_dir, "labels.txt"), "w", encoding="utf-8") as handle:
        for label in encoder.classes_:
            handle.write(label + "\n")

    with open(os.path.join(args.out_dir, "feature_config.json"), "w", encoding="utf-8") as handle:
        json.dump({"feature_names": feature_names}, handle, indent=2)

    with open(os.path.join(args.out_dir, "metrics.txt"), "w", encoding="utf-8") as handle:
        handle.write(f"test_accuracy={acc:.4f}\n")
        handle.write(f"test_loss={loss:.4f}\n")
        handle.write(f"training_samples={len(X_train)}\n")
        handle.write(f"test_samples={len(X_test)}\n")


if __name__ == "__main__":
    main()