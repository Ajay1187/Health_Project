# Disease Model Training

This folder contains multiple training options for disease prediction.

## Dataset used for training

The training scripts now build features directly from `ml/dataset.csv`.
Each `Symptom_*` column is converted into a multi-hot vector across the shared
131-symptom feature schema used by the project.

## Recommended app-linked trainer

The Android app is currently linked to the exported JSON model produced by:

```bash
python ml/train_symptom_nb_model.py \
  --data ml/dataset.csv \
  --out app/src/main/assets/disease_data/disease_nb_model.json \
  --metrics ml/output/disease_nb_metrics.txt
```

## Scikit-learn trainer

```bash
python ml/train_model_sklearn.py --data ml/dataset.csv --out-dir ml/output
```

Outputs:
- `ml/output/disease_rf_model.joblib`
- `ml/output/label_encoder.joblib`
- `ml/output/feature_config_sklearn.json`
- `ml/output/metrics_sklearn.txt`

## CNN trainer

```bash
python ml/train_cnn_model.py --data ml/dataset.csv --out-dir ml/output --epochs 30
```

Outputs:
- `ml/output/disease_cnn.keras`
- `ml/output/disease_cnn.tflite`
- `ml/output/labels.txt`
- `ml/output/feature_config.json`
- `ml/output/metrics.txt`
