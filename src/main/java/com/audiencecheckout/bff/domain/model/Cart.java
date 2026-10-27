package com.audiencecheckout.bff.domain.model;

import java.util.List;
import java.util.UUID;

public record Cart(
        UUID id,
        List<Product> products,
        List<PaymentMethod> paymentMethods,
        CartStatus status
) {
}
