# CNN Model Training (Smart Health Card)

This folder provides a starting pipeline to train a disease-classification CNN and export TensorFlow Lite artifacts for Android.

## 1) Install dependencies

```bash
pip install tensorflow pandas numpy scikit-learn
```

## 2) Train model

```bash
python ml/train_cnn_model.py --data ml/sample_disease_dataset.csv --out-dir ml/output --epochs 30
```

## 3) Expected outputs

- `ml/output/disease_cnn.keras`
- `ml/output/disease_cnn.tflite`
- `ml/output/labels.txt`
- `ml/output/metrics.txt`

> Replace `sample_disease_dataset.csv` with your real dataset for production-grade accuracy targets.


## 4) Lightweight training (no TensorFlow)

```bash
pip install pandas scikit-learn joblib
python ml/train_model_sklearn.py --data ml/sample_disease_dataset.csv --out-dir ml/output
```

Outputs:
- `ml/output/disease_rf_model.joblib`
- `ml/output/label_encoder.joblib`
- `ml/output/metrics_sklearn.txt`
