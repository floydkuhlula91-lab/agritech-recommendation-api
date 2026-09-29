package com.agritech.recommendation.service;

import com.agritech.recommendation.dto.RecommendationResponse;
import com.agritech.recommendation.model.Recommendation;
import com.agritech.recommendation.repository.RecommendationDataGateway;
import com.agritech.recommendation.repository.RecommendationRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RecommendationService {
    private final RecommendationRepository recommendations;
    private final RecommendationDataGateway dataGateway;

    public RecommendationService(
            RecommendationRepository recommendations,
            RecommendationDataGateway dataGateway) {
        this.recommendations = recommendations;
        this.dataGateway = dataGateway;
    }

    @Transactional
    public List<RecommendationResponse> generate(String farmerId) {
        ensureFarmerExists(farmerId);
        String expenseText = dataGateway.expenseText(farmerId);
        List<RecommendationResponse> result = new ArrayList<>();

        for (RecommendationDataGateway.Candidate candidate : dataGateway.openOrderCandidates(farmerId)) {
            boolean matchesExpense = expenseText.contains(candidate.productName().toLowerCase());
            if (!matchesExpense && candidate.otherFarmerCount() == 0) continue;

            Recommendation recommendation = recommendations
                    .findFirstByFarmerIdAndRecommendedProductIdAndSuggestedGroupOrderIdOrderByCreatedAtDesc(
                            farmerId, candidate.productId(), candidate.groupOrderId())
                    .orElseGet(() -> recommendations.save(createRecommendation(farmerId, candidate, matchesExpense)));
            result.add(RecommendationResponse.from(recommendation));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> forFarmer(String farmerId) {
        ensureFarmerExists(farmerId);
        return recommendations.findAllByFarmerIdOrderByCreatedAtDesc(farmerId).stream()
                .map(RecommendationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecommendationResponse get(String id) {
        return recommendations.findById(id)
                .map(RecommendationResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recommendation not found"));
    }

    private Recommendation createRecommendation(
            String farmerId,
            RecommendationDataGateway.Candidate candidate,
            boolean matchesExpense) {
        Recommendation recommendation = new Recommendation();
        recommendation.setFarmerId(farmerId);
        recommendation.setRecommendedProductId(candidate.productId());
        recommendation.setSuggestedGroupOrderId(candidate.groupOrderId());
        recommendation.setReason(matchesExpense
                ? "Your recorded expenses suggest that you use " + candidate.productName()
                        + ". Joining this group order may reduce the purchase price."
                : "Other farmers are joining a group order for " + candidate.productName()
                        + ". Consider joining to coordinate a bulk purchase.");
        return recommendation;
    }

    private void ensureFarmerExists(String farmerId) {
        if (!dataGateway.farmerExists(farmerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
        }
    }
}

