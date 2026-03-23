"""
Train a lightweight 1D CNN for symptom-based disease classification.

Supports two CSV schemas:
1) Numeric features
2) Text features

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

# Text dataset columns
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
    # Detect type
    if SYMPTOM_TEXT_COLUMN in df.columns:
        # TEXT DATASET
        severity_map = {"Low": 0.33, "Medium": 0.66, "High": 1.0}

        vocab = set()
        for val in df[SYMPTOM_TEXT_COLUMN].dropna():
            for s in str(val).split("|"):
                token = s.strip().lower().replace(" ", "_")
                if token:
                    vocab.add(token)

        vocab = sorted(vocab)

        def encode(symptoms):
            tokens = {t.strip().lower().replace(" ", "_") for t in str(symptoms).split("|") if t.strip()}
            return [1 if v in tokens else 0 for v in vocab]

        symptom_matrix = np.array([encode(s) for s in df[SYMPTOM_TEXT_COLUMN]], dtype=np.float32)

        severity = df[SEVERITY_TEXT_COLUMN].map(severity_map).values.reshape(-1, 1)
        duration = df[DURATION_TEXT_COLUMN].values.reshape(-1, 1)

        X = np.concatenate([symptom_matrix, severity, duration], axis=1)
        y = df[PREDICTED_DISEASE_COLUMN].values

        return X, y, vocab

    # NUMERIC / dataset.csv SCHEMA
    if "Disease" in df.columns:
        prepared_df = prepare_dataframe_from_dataset(df)
        X = prepared_df[FEATURE_COLUMNS].values.astype(np.float32)
        y = prepared_df["disease"].values
        return X, y, FEATURE_COLUMNS

    # Already-encoded numeric schema
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
        X_train, y_train,
        epochs=args.epochs,
        batch_size=args.batch_size,
        validation_split=0.1,
        verbose=2
    )

    loss, acc = model.evaluate(X_test, y_test)
    print("Accuracy:", acc)

    model.save(os.path.join(args.out_dir, "model.keras"))

    # Save labels
    with open(os.path.join(args.out_dir, "labels.txt"), "w") as f:
        for label in encoder.classes_:
            f.write(label + "\n")

    # Save config
    with open(os.path.join(args.out_dir, "feature_config.json"), "w") as f:
        json.dump({"features": feature_names}, f, indent=2)

    with open(os.path.join(args.out_dir, "metrics.txt"), "w") as f:
        f.write(f"test_accuracy={acc:.4f}\n")
        f.write(f"test_loss={loss:.4f}\n")
        f.write(f"training_samples={len(X_train)}\n")
        f.write(f"test_samples={len(X_test)}\n")


if __name__ == "__main__":
    main()
