package com.agritech.recommendation.dto;

import jakarta.validation.constraints.NotBlank;

public record GenerateRecommendationRequest(
        @NotBlank(message = "farmerId is required") String farmerId) {
}

