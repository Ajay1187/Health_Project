package com.example.healthcard_demo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RecommendationEngineTest {

    @Test
    public void mapSeverityLabel_shouldReturnExpectedBands() {
        assertEquals("Low", DiseaseDataRepository.mapSeverityLabel(3));
        assertEquals("Medium", DiseaseDataRepository.mapSeverityLabel(4));
        assertEquals("High", DiseaseDataRepository.mapSeverityLabel(6));
    }
}
