package com.example.healthcard_demo;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TrainedDiseaseClassifierTest {

    @Test
    public void predict_shouldChooseDiseaseWithHigherPosteriorScore() {
        TrainedDiseaseClassifier.ModelData modelData = new TrainedDiseaseClassifier.ModelData();
        modelData.symptoms = Arrays.asList("itching", "cough");

        TrainedDiseaseClassifier.DiseaseWeights fungal = new TrainedDiseaseClassifier.DiseaseWeights();
        fungal.name = "Fungal infection";
        fungal.logPrior = -0.7;
        fungal.logPresent = new double[]{-0.1, -2.5};
        fungal.logAbsent = new double[]{-2.0, -0.1};

        TrainedDiseaseClassifier.DiseaseWeights cold = new TrainedDiseaseClassifier.DiseaseWeights();
        cold.name = "Common Cold";
        cold.logPrior = -0.7;
        cold.logPresent = new double[]{-2.5, -0.1};
        cold.logAbsent = new double[]{-0.1, -2.0};

        modelData.diseases = Arrays.asList(fungal, cold);

        TrainedDiseaseClassifier classifier = new TrainedDiseaseClassifier(modelData);
        TrainedDiseaseClassifier.Prediction prediction = classifier.predict(Arrays.asList("itching"));

        assertNotNull(prediction);
        assertEquals("Fungal infection", prediction.getDiseaseName());
        assertTrue(prediction.getConfidence() > 0.5f);
    }
}
