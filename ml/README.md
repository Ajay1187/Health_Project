# Disease Model Training

This folder contains the CNN training pipeline used by the app prediction flow.

## Runtime prediction path

The Android app prefers a TensorFlow Lite CNN model for disease prediction and falls back to a dataset-overlap matcher when the TFLite asset has not yet been bundled.
Train the model from `ml/dataset.csv` and copy the generated `disease_cnn.tflite` file into:

`app/src/main/assets/disease_data/disease_cnn.tflite`

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


## Optional sklearn trainer

```bash
python ml/train_model_sklearn.py --data ml/dataset.csv --out-dir ml/output
```

Outputs:
- `ml/output/disease_rf_model.joblib`
- `ml/output/label_encoder.joblib`
- `ml/output/feature_config_sklearn.json`
- `ml/output/metrics_sklearn.txt`
