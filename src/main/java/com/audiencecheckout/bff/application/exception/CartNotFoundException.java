package com.audiencecheckout.bff.application.exception;

import java.util.UUID;

public class CartNotFoundException extends RuntimeException {

    private final UUID cartId;

    public CartNotFoundException(UUID cartId) {
        super("Cart not found: " + cartId);
        this.cartId = cartId;
    }

    public UUID getCartId() {
        return cartId;
    }
}