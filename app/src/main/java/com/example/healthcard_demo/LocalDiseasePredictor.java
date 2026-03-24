```java
package com.example.healthcard_demo;

import android.content.Context;
import java.util.List;

public class LocalDiseasePredictor {

    private final DiseaseDataRepository repository;

    public LocalDiseasePredictor(Context context) {
        this.repository = new DiseaseDataRepository(context);
    }

    public DiseaseResponse predict(List<String> symptoms, int durationDays, float temperatureF) {
        return repository.predictDisease(symptoms, durationDays, temperatureF);
    }
}
```
