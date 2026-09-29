package com.agritech.recommendation.dto;

import com.agritech.recommendation.model.Recommendation;
import java.time.LocalDateTime;

public record RecommendationResponse(
        String id,
        String farmerId,
        String recommendedProductId,
        String suggestedGroupOrderId,
        String reason,
        LocalDateTime createdAt) {

    public static RecommendationResponse from(Recommendation recommendation) {
        return new RecommendationResponse(
                recommendation.getId(),
                recommendation.getFarmerId(),
                recommendation.getRecommendedProductId(),
                recommendation.getSuggestedGroupOrderId(),
                recommendation.getReason(),
                recommendation.getCreatedAt());
    }
}

