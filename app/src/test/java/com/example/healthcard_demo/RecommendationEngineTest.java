package com.example.healthcard_demo;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class RecommendationEngineTest {

    @Test
    public void getRecommendation_shouldReturnDefaultForUnknownDisease() {
        RecommendationEngine engine = new RecommendationEngine();
        RecommendationEngine.Recommendation recommendation = engine.getRecommendation("abc_unknown");
        assertFalse(recommendation.getMedicines().isEmpty());
    }
}
