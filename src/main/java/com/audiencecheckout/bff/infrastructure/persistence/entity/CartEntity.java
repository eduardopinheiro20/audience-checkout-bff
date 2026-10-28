package com.audiencecheckout.bff.infrastructure.persistence.entity;

import com.audiencecheckout.bff.domain.model.CartStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "checkout_carts")
public class CartEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CartStatus status;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CartProductEntity> products = new ArrayList<>();

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CartPaymentMethodEntity> paymentMethods = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    protected CartEntity() {
        // Required by JPA.
    }

    public CartEntity(UUID id, CartStatus status) {
        this.id = id;
        this.status = status;
    }

    @PrePersist
    private void onCreate() {
        var now = Instant.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = Instant.now();
    }

    public void addProduct(CartProductEntity product) {
        products.add(product);
        product.assignTo(this);
    }

    public void addPaymentMethod(CartPaymentMethodEntity paymentMethod) {
        paymentMethods.add(paymentMethod);
        paymentMethod.assignTo(this);
    }

    public void changeStatus(CartStatus status) {
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public CartStatus getStatus() {
        return status;
    }

    public List<CartProductEntity> getProducts() {
        return List.copyOf(products);
    }

    public List<CartPaymentMethodEntity> getPaymentMethods() {
        return List.copyOf(paymentMethods);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }
}