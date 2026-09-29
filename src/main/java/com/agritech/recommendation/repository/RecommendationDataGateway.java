package com.agritech.recommendation.repository;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RecommendationDataGateway {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationDataGateway(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean farmerExists(String farmerId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM farmers WHERE id = ?", Integer.class, farmerId);
        return count != null && count > 0;
    }

    public String expenseText(String farmerId) {
        return String.join(" ", jdbcTemplate.queryForList(
                "SELECT COALESCE(item, '') FROM expenses WHERE farmer_id = ?",
                String.class,
                farmerId)).toLowerCase();
    }

    public List<Candidate> openOrderCandidates(String farmerId) {
        return jdbcTemplate.query("""
                SELECT go.id AS group_order_id,
                       sp.id AS product_id,
                       sp.product_name,
                       COUNT(goi.id) FILTER (WHERE goi.farmer_id <> ?) AS other_farmer_count
                FROM group_orders go
                JOIN supplier_products sp ON sp.id = go.product_id
                LEFT JOIN group_order_items goi ON goi.group_order_id = go.id
                WHERE LOWER(go.status) = 'open'
                GROUP BY go.id, sp.id, sp.product_name
                ORDER BY go.created_at DESC
                """, (rs, rowNum) -> new Candidate(
                rs.getString("group_order_id"),
                rs.getString("product_id"),
                rs.getString("product_name"),
                rs.getLong("other_farmer_count")), farmerId);
    }

    public record Candidate(String groupOrderId, String productId, String productName, long otherFarmerCount) {
    }
}

