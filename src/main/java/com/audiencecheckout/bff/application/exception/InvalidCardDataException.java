package com.audiencecheckout.bff.application.exception;

import java.util.UUID;

public class InvalidCardDataException extends RuntimeException {

    private final UUID cartId;

    public InvalidCardDataException(UUID cartId) {
        super("Invalid card data");
        this.cartId = cartId;
    }

    public UUID getCartId() {
        return cartId;
    }
}