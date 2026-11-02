package com.audiencecheckout.bff.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheckoutRequest(
        @NotNull UUID cartId,
        @NotNull @Valid PaymentMethodRequest paymentMethod
) {
}