"""Train a lightweight 1D CNN for symptom-based disease classification.

Usage:
python ml/train_cnn_model.py --data ml/sample_disease_dataset.csv --out-dir ml/output
"""

import argparse
import os
import pandas as pd
import numpy as np
import tensorflow as tf
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder


FEATURE_COLUMNS = [
    "fever", "cough", "headache", "fatigue", "vomiting",
    "chest_pain", "sore_throat", "body_pain", "nausea", "breathlessness",
    "severity", "duration_days"
]


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--data", required=True, help="CSV dataset path")
    parser.add_argument("--out-dir", required=True, help="Output directory")
    parser.add_argument("--epochs", type=int, default=30)
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


def main():
    args = parse_args()
    os.makedirs(args.out_dir, exist_ok=True)

    df = pd.read_csv(args.data)
    df = df.dropna(subset=FEATURE_COLUMNS + ["disease"])

    X = df[FEATURE_COLUMNS].values.astype(np.float32)
    y = df["disease"].values

    encoder = LabelEncoder()
    y_encoded = encoder.fit_transform(y)

    X_train, X_test, y_train, y_test = train_test_split(
        X, y_encoded, test_size=0.2, random_state=42, stratify=y_encoded
    )

    X_train = np.expand_dims(X_train, axis=-1)
    X_test = np.expand_dims(X_test, axis=-1)

    model = build_model(X_train.shape[1], len(encoder.classes_))
    model.fit(X_train, y_train, epochs=args.epochs, batch_size=32, validation_split=0.1, verbose=2)

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

    metrics_path = os.path.join(args.out_dir, "metrics.txt")
    with open(metrics_path, "w", encoding="utf-8") as f:
        f.write(f"test_accuracy={acc:.4f}\n")
        f.write(f"test_loss={loss:.4f}\n")


if __name__ == "__main__":
    main()
