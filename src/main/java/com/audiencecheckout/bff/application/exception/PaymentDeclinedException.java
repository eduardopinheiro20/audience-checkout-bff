package com.audiencecheckout.bff.application.exception;

import java.util.UUID;

public class PaymentDeclinedException extends RuntimeException {

    private final UUID cartId;

    public PaymentDeclinedException(UUID cartId) {
        super("Payment declined for cart: " + cartId);
        this.cartId = cartId;
    }

    public UUID getCartId() {
        return cartId;
    }
}