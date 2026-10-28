package com.audiencecheckout.bff.api.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CheckoutResponse(
        UUID cartId,
        String status,
        Instant paidAt,
        CheckoutPaymentResponse payment,
        List<CheckoutProductResponse> products
) {
}