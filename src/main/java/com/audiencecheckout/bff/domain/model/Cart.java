package com.audiencecheckout.bff.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Cart(
        UUID id,
        List<Product> products,
        List<PaymentMethod> paymentMethods,
        CartStatus status
) {

    public Cart {
        Objects.requireNonNull(id, "Cart id must not be null");
        Objects.requireNonNull(products, "Products must not be null");
        Objects.requireNonNull(paymentMethods, "Payment methods must not be null");
        Objects.requireNonNull(status, "Cart status must not be null");

        products = List.copyOf(products);
        paymentMethods = List.copyOf(paymentMethods);

        if (products.isEmpty()) {
            throw new IllegalArgumentException(
                    "A cart must contain at least one product"
            );
        }

        if (paymentMethods.isEmpty()) {
            throw new IllegalArgumentException(
                    "A cart must contain at least one payment method"
            );
        }
    }

    public static Cart create(List<Product> products, List<PaymentMethod> paymentMethods) {
        return new Cart(
                UUID.randomUUID(),
                products,
                paymentMethods,
                CartStatus.CREATED
        );
    }

    public static Cart restore(
            UUID id,
            List<Product> products,
            List<PaymentMethod> paymentMethods,
            CartStatus status
    ) {
        return new Cart(
                id,
                products,
                paymentMethods,
                status
        );
    }

    public boolean allowsPaymentMethod(String paymentMethodName) {
        if (paymentMethodName == null || paymentMethodName.isBlank()) {
            return false;
        }

        return paymentMethods.stream()
                .anyMatch(method ->
                        method.name().equalsIgnoreCase(paymentMethodName)
                );
    }

    public boolean isApproved() {
        return status == CartStatus.APPROVED;
    }

    public Cart approve() {
        return new Cart(
                id,
                products,
                paymentMethods,
                CartStatus.APPROVED
        );
    }

    public Cart decline() {
        return new Cart(
                id,
                products,
                paymentMethods,
                CartStatus.DECLINED
        );
    }
}