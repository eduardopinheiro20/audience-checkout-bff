package com.audiencecheckout.bff.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "checkout_cart_products")
public class CartProductEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;

    @Column(name = "product_slug", nullable = false, length = 100)
    private String slug;

    @Column(name = "product_name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(
            name = "price",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal price;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    protected CartProductEntity() {
        // Required by JPA.
    }

    public CartProductEntity(
            String slug,
            String name,
            String description,
            BigDecimal price,
            String currency
    ) {
        this.id = UUID.randomUUID();
        this.slug = Objects.requireNonNull(slug);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.price = Objects.requireNonNull(price);
        this.currency = Objects.requireNonNull(currency);
    }

    void assignTo(CartEntity cart) {
        this.cart = Objects.requireNonNull(cart);
    }

    public UUID getId() {
        return id;
    }

    public CartEntity getCart() {
        return cart;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }
}