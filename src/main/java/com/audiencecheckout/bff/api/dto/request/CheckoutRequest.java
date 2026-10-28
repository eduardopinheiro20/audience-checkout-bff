package com.audiencecheckout.bff.api.dto.request;

import java.util.UUID;

public record CheckoutRequest(
        UUID cartId,
        PaymentMethodRequest paymentMethod
) {
}