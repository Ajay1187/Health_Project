"""Train a Naive Bayes symptom model from dataset.csv without third-party deps.

Usage:
  python ml/train_naive_bayes_model.py \
    --data ml/dataset.csv \
    --out app/src/main/assets/disease_data/disease_nb_model.json
"""

from __future__ import annotations

import argparse
import csv
import json
import math
from collections import Counter
from pathlib import Path


def normalize_symptom(value: str) -> str:
    return (value or "").strip().lower().replace("_", " ").replace("-", " ")


def parse_rows(path: Path):
    with path.open("r", encoding="utf-8", newline="") as handle:
        reader = csv.DictReader(handle)
        if not reader.fieldnames or "Disease" not in reader.fieldnames:
            raise ValueError("Expected a Disease column in dataset file")
        symptom_columns = [name for name in reader.fieldnames if name.startswith("Symptom_")]
        if not symptom_columns:
            raise ValueError("Expected Symptom_1..Symptom_N columns in dataset file")

        rows = []
        for row in reader:
            disease = (row.get("Disease") or "").strip()
            if not disease:
                continue
            symptoms = {
                normalize_symptom(row.get(col, ""))
                for col in symptom_columns
                if normalize_symptom(row.get(col, ""))
            }
            rows.append((disease, symptoms))
    if not rows:
        raise ValueError("No valid training rows found in dataset file")
    return rows


def train(rows, alpha: float = 1.0):
    disease_counts = Counter(disease for disease, _ in rows)
    diseases = sorted(disease_counts)

    symptom_vocab = sorted({symptom for _, symptoms in rows for symptom in symptoms})
    symptom_index = {symptom: idx for idx, symptom in enumerate(symptom_vocab)}

    present_counts = {d: [0] * len(symptom_vocab) for d in diseases}
    for disease, symptoms in rows:
        counts = present_counts[disease]
        for symptom in symptoms:
            counts[symptom_index[symptom]] += 1

    total_samples = len(rows)
    model_diseases = []

    for disease in diseases:
        disease_total = disease_counts[disease]
        log_prior = math.log(disease_total / total_samples)

        log_present = []
        log_absent = []
        for present in present_counts[disease]:
            p_present = (present + alpha) / (disease_total + 2.0 * alpha)
            p_absent = 1.0 - p_present
            log_present.append(math.log(p_present))
            log_absent.append(math.log(p_absent))

        model_diseases.append(
            {
                "name": disease,
                "logPrior": log_prior,
                "logPresent": log_present,
                "logAbsent": log_absent,
                "sampleCount": disease_total,
            }
        )

    return {
        "modelType": "bernoulli_naive_bayes",
        "symptoms": symptom_vocab,
        "diseases": model_diseases,
        "trainingSampleCount": total_samples,
        "smoothingAlpha": alpha,
        "sourceDataset": "dataset.csv",
    }


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--data", required=True)
    parser.add_argument("--out", required=True)
    parser.add_argument("--alpha", type=float, default=1.0)
    return parser.parse_args()


def main():
    args = parse_args()
    rows = parse_rows(Path(args.data))
    payload = train(rows, alpha=args.alpha)

    out_path = Path(args.out)
    out_path.parent.mkdir(parents=True, exist_ok=True)
    with out_path.open("w", encoding="utf-8") as handle:
        json.dump(payload, handle, ensure_ascii=False)

    print(f"Wrote model to {out_path} with {payload['trainingSampleCount']} rows.")


if __name__ == "__main__":
    main()
