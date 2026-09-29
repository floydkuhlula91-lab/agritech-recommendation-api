package com.agritech.recommendation.chat;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(@NotBlank(message = "prompt is required") String prompt) {
}

