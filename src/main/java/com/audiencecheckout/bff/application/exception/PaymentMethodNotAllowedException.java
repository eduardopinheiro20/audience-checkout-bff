package com.audiencecheckout.bff.application.exception;

import java.util.UUID;

public class PaymentMethodNotAllowedException extends RuntimeException {

    private final UUID cartId;
    private final String paymentMethod;

    public PaymentMethodNotAllowedException(
            UUID cartId,
            String paymentMethod
    ) {
        super(
                "Payment method not allowed for cart "
                        + cartId
                        + ": "
                        + paymentMethod
        );

        this.cartId = cartId;
        this.paymentMethod = paymentMethod;
    }

    public UUID getCartId() {
        return cartId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}