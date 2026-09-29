package com.agritech.recommendation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_recommendations")
public class Recommendation {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "farmer_id", nullable = false, length = 36)
    private String farmerId;

    @Column(name = "recommended_product_id", length = 36)
    private String recommendedProductId;

    @Column(name = "suggested_group_order_id", length = 36)
    private String suggestedGroupOrderId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void initialize() {
        if (id == null) id = UUID.randomUUID().toString();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }
    public String getRecommendedProductId() { return recommendedProductId; }
    public void setRecommendedProductId(String recommendedProductId) { this.recommendedProductId = recommendedProductId; }
    public String getSuggestedGroupOrderId() { return suggestedGroupOrderId; }
    public void setSuggestedGroupOrderId(String suggestedGroupOrderId) { this.suggestedGroupOrderId = suggestedGroupOrderId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

