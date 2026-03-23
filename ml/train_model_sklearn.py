"""Train a lightweight ML classifier for disease prediction from ml/dataset.csv.

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

from training_data_utils import FEATURE_COLUMNS, prepare_dataframe_from_dataset


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

    X = df[FEATURE_COLUMNS]
    y = df["disease"]

    label_encoder = LabelEncoder()
    y_encoded = label_encoder.fit_transform(y)

    X_train, X_test, y_train, y_test = train_test_split(
        X, y_encoded, test_size=0.2, random_state=42, stratify=y_encoded
    )

    model = RandomForestClassifier(n_estimators=200, random_state=42)
    model.fit(X_train, y_train)

    preds = model.predict(X_test)
    acc = accuracy_score(y_test, preds)

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
