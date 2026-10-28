package com.audiencecheckout.bff.infrastructure.persistence.adapter;

import com.audiencecheckout.bff.domain.model.Cart;
import com.audiencecheckout.bff.domain.model.PaymentMethod;
import com.audiencecheckout.bff.domain.model.Product;
import com.audiencecheckout.bff.domain.port.out.CartRepositoryPort;
import com.audiencecheckout.bff.infrastructure.persistence.entity.CartEntity;
import com.audiencecheckout.bff.infrastructure.persistence.entity.CartPaymentMethodEntity;
import com.audiencecheckout.bff.infrastructure.persistence.entity.CartProductEntity;
import com.audiencecheckout.bff.infrastructure.persistence.repository.SpringDataCartRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PostgresCartRepositoryAdapter implements CartRepositoryPort {

    private final SpringDataCartRepository repository;

    public PostgresCartRepositoryAdapter(SpringDataCartRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Cart save(Cart cart) {
        var entity = repository.findById(cart.id())
                .map(existingEntity -> {
                    existingEntity.changeStatus(cart.status());
                    return existingEntity;
                })
                .orElseGet(() -> toEntity(cart));

        var savedEntity = repository.save(entity);

        return toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> findById(UUID cartId) {
        return repository.findById(cartId)
                .map(this::toDomain);
    }

    private CartEntity toEntity(Cart cart) {
        var entity = new CartEntity(
                cart.id(),
                cart.status()
        );

        cart.products()
                .stream()
                .map(this::toProductEntity)
                .forEach(entity::addProduct);

        cart.paymentMethods()
                .stream()
                .map(this::toPaymentMethodEntity)
                .forEach(entity::addPaymentMethod);

        return entity;
    }

    private CartProductEntity toProductEntity(Product product) {
        return new CartProductEntity(
                product.slug(),
                product.name(),
                product.description(),
                product.price(),
                product.currency()
        );
    }

    private CartPaymentMethodEntity toPaymentMethodEntity(
            PaymentMethod paymentMethod
    ) {
        return new CartPaymentMethodEntity(
                paymentMethod.name()
        );
    }

    private Cart toDomain(CartEntity entity) {
        var products = entity.getProducts()
                .stream()
                .map(product -> new Product(
                        product.getSlug(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getCurrency()
                ))
                .toList();

        var paymentMethods = entity.getPaymentMethods()
                .stream()
                .map(paymentMethod ->
                        new PaymentMethod(paymentMethod.getName())
                )
                .toList();

        return Cart.restore(
                entity.getId(),
                products,
                paymentMethods,
                entity.getStatus()
        );
    }
}