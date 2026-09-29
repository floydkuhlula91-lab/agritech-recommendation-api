package com.agritech.recommendation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agritech.recommendation.model.Recommendation;
import com.agritech.recommendation.repository.RecommendationDataGateway;
import com.agritech.recommendation.repository.RecommendationRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock RecommendationRepository repository;
    @Mock RecommendationDataGateway gateway;

    private RecommendationService service;

    @BeforeEach
    void setUp() {
        service = new RecommendationService(repository, gateway);
    }

    @Test
    void generatesRecommendationWhenExpenseMatchesProduct() {
        var candidate = new RecommendationDataGateway.Candidate("order-1", "product-1", "Fertilizer", 0);
        when(gateway.farmerExists("farmer-1")).thenReturn(true);
        when(gateway.expenseText("farmer-1")).thenReturn("fertilizer seeds");
        when(gateway.openOrderCandidates("farmer-1")).thenReturn(List.of(candidate));
        when(repository.findFirstByFarmerIdAndRecommendedProductIdAndSuggestedGroupOrderIdOrderByCreatedAtDesc(
                "farmer-1", "product-1", "order-1")).thenReturn(Optional.empty());
        when(repository.save(org.mockito.ArgumentMatchers.any(Recommendation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.generate("farmer-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).reason()).contains("recorded expenses");
        verify(repository).save(org.mockito.ArgumentMatchers.any(Recommendation.class));
    }

    @Test
    void doesNotRecommendUnrelatedProductWithNoGroupInterest() {
        var candidate = new RecommendationDataGateway.Candidate("order-1", "product-1", "Fertilizer", 0);
        when(gateway.farmerExists("farmer-1")).thenReturn(true);
        when(gateway.expenseText("farmer-1")).thenReturn("tractor fuel");
        when(gateway.openOrderCandidates("farmer-1")).thenReturn(List.of(candidate));

        assertThat(service.generate("farmer-1")).isEmpty();
    }
}

