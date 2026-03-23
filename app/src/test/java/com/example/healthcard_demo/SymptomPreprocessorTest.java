package com.example.healthcard_demo;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SymptomPreprocessorTest {

    @Test
    public void toFeatureVector_shouldReturnExpectedSizeAndValues() {
        SymptomPreprocessor preprocessor = new SymptomPreprocessor();
        List<Float> features = preprocessor.toFeatureVector(
                Arrays.asList(" Fever ", "cough", "unknown"),
                "High",
                15
        );

        assertEquals(SymptomPreprocessor.FEATURE_SIZE, features.size());
        assertEquals(1f, features.get(0), 0.001f); // fever
        assertEquals(1f, features.get(1), 0.001f); // cough
        assertEquals(1f, features.get(10), 0.001f); // severity high
        assertEquals(0.5f, features.get(11), 0.001f); // duration normalized
    }
}
