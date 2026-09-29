package com.agritech.recommendation.controller;

import com.agritech.recommendation.dto.GenerateRecommendationRequest;
import com.agritech.recommendation.dto.RecommendationResponse;
import com.agritech.recommendation.service.RecommendationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public List<RecommendationResponse> generate(@Valid @RequestBody GenerateRecommendationRequest request) {
        return service.generate(request.farmerId());
    }

    @GetMapping("/farmer/{farmerId}")
    public List<RecommendationResponse> forFarmer(@PathVariable String farmerId) {
        return service.forFarmer(farmerId);
    }

    @GetMapping("/{id}")
    public RecommendationResponse get(@PathVariable String id) {
        return service.get(id);
    }
}

