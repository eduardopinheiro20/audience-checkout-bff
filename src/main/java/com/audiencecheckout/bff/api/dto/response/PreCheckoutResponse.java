package com.audiencecheckout.bff.api.dto.response;

import java.util.List;
import java.util.UUID;

public record PreCheckoutResponse(
        UUID cartId,
        List<ProductResponse> products,
        List<PaymentMethodResponse> paymentMethods
) {
}