package com.audiencecheckout.bff.application.exception;

public class ProductNotFoundException extends RuntimeException {

    private final String productSlug;

    public ProductNotFoundException(String productSlug) {
        super("Product not found: " + productSlug);
        this.productSlug = productSlug;
    }

    public String getProductSlug() {
        return productSlug;
    }
}