package com.audiencecheckout.bff.api.dto.response;

import java.math.BigDecimal;

public record ProductResponse(
        String slug,
        String name,
        String description,
        BigDecimal price,
        String currency
) {
}