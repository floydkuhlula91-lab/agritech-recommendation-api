package com.agritech.recommendation.repository;

import com.agritech.recommendation.model.Recommendation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, String> {
    List<Recommendation> findAllByFarmerIdOrderByCreatedAtDesc(String farmerId);

    Optional<Recommendation> findFirstByFarmerIdAndRecommendedProductIdAndSuggestedGroupOrderIdOrderByCreatedAtDesc(
            String farmerId, String productId, String groupOrderId);
}

