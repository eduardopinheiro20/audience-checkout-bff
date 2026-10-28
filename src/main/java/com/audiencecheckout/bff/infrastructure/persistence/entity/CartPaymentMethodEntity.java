package com.audiencecheckout.bff.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "checkout_cart_payment_methods")
public class CartPaymentMethodEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;

    @Column(name = "payment_method_name", nullable = false, length = 100)
    private String name;

    protected CartPaymentMethodEntity() {
        // Required by JPA.
    }

    public CartPaymentMethodEntity(String name) {
        this.id = UUID.randomUUID();
        this.name = Objects.requireNonNull(
                name,
                "Payment method name must not be null"
        );
    }

    void assignTo(CartEntity cart) {
        this.cart = Objects.requireNonNull(
                cart,
                "Cart must not be null"
        );
    }

    public UUID getId() {
        return id;
    }

    public CartEntity getCart() {
        return cart;
    }

    public String getName() {
        return name;
    }
}