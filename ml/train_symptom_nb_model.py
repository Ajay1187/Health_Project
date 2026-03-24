"""Train a lightweight symptom-based disease model from ml/dataset.csv.

This script builds a Bernoulli Naive Bayes classifier directly from the symptom
columns in dataset.csv and exports a JSON artifact that can be loaded by the
Android app without TensorFlow, scikit-learn, pandas, or numpy.

Usage:
python ml/train_symptom_nb_model.py \
  --data ml/dataset.csv \
  --out app/src/main/assets/disease_data/disease_nb_model.json \
  --metrics ml/output/disease_nb_metrics.txt
"""

from __future__ import annotations

import argparse
import csv
import json
import math
import random
from collections import defaultdict
from pathlib import Path
from typing import Dict, List, Sequence, Set, Tuple


DEFAULT_TEST_RATIO = 0.2
DEFAULT_SEED = 42
SMOOTHING_ALPHA = 1.0


class DatasetRow(dict):
    disease: str
    symptoms: Set[str]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--data", required=True, help="Path to dataset.csv")
    parser.add_argument("--out", required=True, help="Output JSON model path")
    parser.add_argument("--metrics", required=True, help="Metrics output path")
    parser.add_argument("--test-ratio", type=float, default=DEFAULT_TEST_RATIO)
    parser.add_argument("--seed", type=int, default=DEFAULT_SEED)
    return parser.parse_args()


def normalize_symptom(value: str) -> str:
    return " ".join((value or "").strip().replace("_", " ").split()).lower()


def load_dataset(path: Path) -> Tuple[List[dict], List[str]]:
    rows: List[dict] = []
    vocabulary: Set[str] = set()

    with path.open("r", encoding="utf-8", newline="") as handle:
        reader = csv.DictReader(handle)
        symptom_columns = [column for column in reader.fieldnames or [] if column != "Disease"]
        for raw_row in reader:
            disease_name = (raw_row.get("Disease") or "").strip()
            if not disease_name:
                continue
            symptoms = set()
            for column in symptom_columns:
                symptom = normalize_symptom(raw_row.get(column, ""))
                if symptom:
                    symptoms.add(symptom)
                    vocabulary.add(symptom)
            rows.append({"disease": disease_name, "symptoms": symptoms})

    return rows, sorted(vocabulary)


def stratified_split(rows: Sequence[dict], test_ratio: float, seed: int) -> Tuple[List[dict], List[dict]]:
    grouped: Dict[str, List[dict]] = defaultdict(list)
    for row in rows:
        grouped[row["disease"]].append(row)

    rng = random.Random(seed)
    train_rows: List[dict] = []
    test_rows: List[dict] = []

    for disease_rows in grouped.values():
        disease_rows = list(disease_rows)
        rng.shuffle(disease_rows)
        if len(disease_rows) <= 1:
            train_rows.extend(disease_rows)
            continue

        test_count = max(1, int(round(len(disease_rows) * test_ratio)))
        if test_count >= len(disease_rows):
            test_count = len(disease_rows) - 1

        test_rows.extend(disease_rows[:test_count])
        train_rows.extend(disease_rows[test_count:])

    return train_rows, test_rows


def train_model(rows: Sequence[dict], vocabulary: Sequence[str]) -> dict:
    disease_rows: Dict[str, List[dict]] = defaultdict(list)
    for row in rows:
        disease_rows[row["disease"]].append(row)

    total_rows = len(rows)
    disease_names = sorted(disease_rows.keys())
    disease_count = len(disease_names)

    model_diseases = []
    for disease_name in disease_names:
        class_rows = disease_rows[disease_name]
        class_total = len(class_rows)
        prior = math.log((class_total + SMOOTHING_ALPHA) / (total_rows + (SMOOTHING_ALPHA * disease_count)))

        symptom_presence_counts = {symptom: 0 for symptom in vocabulary}
        for row in class_rows:
            for symptom in row["symptoms"]:
                symptom_presence_counts[symptom] += 1

        log_present: List[float] = []
        log_absent: List[float] = []
        for symptom in vocabulary:
            present_probability = (symptom_presence_counts[symptom] + SMOOTHING_ALPHA) / (class_total + (2 * SMOOTHING_ALPHA))
            absent_probability = 1.0 - present_probability
            log_present.append(math.log(present_probability))
            log_absent.append(math.log(absent_probability))

        model_diseases.append({
            "name": disease_name,
            "logPrior": prior,
            "logPresent": log_present,
            "logAbsent": log_absent,
            "sampleCount": class_total,
        })

    return {
        "modelType": "bernoulli_naive_bayes",
        "symptoms": list(vocabulary),
        "diseases": model_diseases,
        "trainingSampleCount": total_rows,
        "smoothingAlpha": SMOOTHING_ALPHA,
    }


def predict(model: dict, symptoms: Set[str]) -> Tuple[str, float]:
    symptom_index = {symptom: index for index, symptom in enumerate(model["symptoms"])}
    active_indices = {symptom_index[symptom] for symptom in symptoms if symptom in symptom_index}

    scores: List[Tuple[str, float]] = []
    for disease in model["diseases"]:
        score = disease["logPrior"]
        for index in range(len(model["symptoms"])):
            score += disease["logPresent"][index] if index in active_indices else disease["logAbsent"][index]
        scores.append((disease["name"], score))

    best_name, best_score = max(scores, key=lambda item: item[1])
    max_score = max(score for _, score in scores)
    exp_scores = [math.exp(score - max_score) for _, score in scores]
    total_exp = sum(exp_scores) or 1.0
    best_confidence = math.exp(best_score - max_score) / total_exp
    return best_name, best_confidence


def evaluate(model: dict, rows: Sequence[dict]) -> float:
    if not rows:
        return 1.0
    correct = 0
    for row in rows:
        predicted_name, _ = predict(model, row["symptoms"])
        if predicted_name == row["disease"]:
            correct += 1
    return correct / len(rows)


def main() -> None:
    args = parse_args()
    dataset_path = Path(args.data)
    output_path = Path(args.out)
    metrics_path = Path(args.metrics)

    rows, vocabulary = load_dataset(dataset_path)
    train_rows, test_rows = stratified_split(rows, args.test_ratio, args.seed)
    model = train_model(train_rows, vocabulary)
    accuracy = evaluate(model, test_rows)

    output_path.parent.mkdir(parents=True, exist_ok=True)
    metrics_path.parent.mkdir(parents=True, exist_ok=True)

    model["sourceDataset"] = str(dataset_path)
    model["testSampleCount"] = len(test_rows)
    model["testAccuracy"] = accuracy

    with output_path.open("w", encoding="utf-8") as handle:
        json.dump(model, handle, indent=2)

    with metrics_path.open("w", encoding="utf-8") as handle:
        handle.write(f"training_samples={len(train_rows)}\n")
        handle.write(f"test_samples={len(test_rows)}\n")
        handle.write(f"vocabulary_size={len(vocabulary)}\n")
        handle.write(f"test_accuracy={accuracy:.4f}\n")

    print(f"Model written to {output_path}")
    print(f"Metrics written to {metrics_path}")
    print(f"Test accuracy: {accuracy:.4f}")


if __name__ == "__main__":
    main()
