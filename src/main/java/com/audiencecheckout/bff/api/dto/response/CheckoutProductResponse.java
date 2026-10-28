package com.audiencecheckout.bff.api.dto.response;

import java.math.BigDecimal;

public record CheckoutProductResponse(
        String slug,
        String name,
        BigDecimal price
) {
}