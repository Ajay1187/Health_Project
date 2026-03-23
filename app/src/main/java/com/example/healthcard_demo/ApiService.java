package com.example.healthcard_demo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/predict")
    Call<DiseaseResponse> predictDisease(@Body DiseaseRequest request);
}
