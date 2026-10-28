package com.audiencecheckout.bff.application.exception;

import java.util.UUID;

public class CartAlreadyApprovedException extends RuntimeException {

    private final UUID cartId;

    public CartAlreadyApprovedException(UUID cartId) {
        super("Cart already approved: " + cartId);
        this.cartId = cartId;
    }

    public UUID getCartId() {
        return cartId;
    }
}