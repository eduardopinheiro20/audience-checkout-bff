package com.audiencecheckout.bff.api.dto.response;

import java.math.BigDecimal;

public record CheckoutPaymentResponse(
        String method,
        String lastDigits,
        String pixCode,
        BigDecimal amount,
        String currency
) {
}