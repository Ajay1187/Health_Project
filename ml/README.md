# Disease Model Training

This folder now contains multiple training options for disease prediction.

## Recommended app-linked trainer

The Android app is linked to the exported JSON model produced by:

```bash
python ml/train_symptom_nb_model.py \
  --data ml/dataset.csv \
  --out app/src/main/assets/disease_data/disease_nb_model.json \
  --metrics ml/output/disease_nb_metrics.txt
```

What it does:
- reads the symptom columns from `ml/dataset.csv`
- trains a lightweight Bernoulli Naive Bayes model without external ML dependencies
- exports `disease_nb_model.json` that the Android app can load directly
- writes a metrics file for the training run

## Optional experimental trainers

These scripts are still available for experimentation, but they are **not** the runtime model currently used by the Android app unless you add extra integration code.

### CNN trainer

```bash
python ml/train_cnn_model.py --data ml/sample_disease_dataset.csv --out-dir ml/output --epochs 30
```

Outputs:
- `ml/output/disease_cnn.keras`
- `ml/output/disease_cnn.tflite`
- `ml/output/labels.txt`
- `ml/output/metrics.txt`

### Scikit-learn trainer

```bash
python ml/train_model_sklearn.py --data ml/sample_disease_dataset.csv --out-dir ml/output
```

Outputs:
- `ml/output/disease_rf_model.joblib`
- `ml/output/label_encoder.joblib`
- `ml/output/metrics_sklearn.txt`
