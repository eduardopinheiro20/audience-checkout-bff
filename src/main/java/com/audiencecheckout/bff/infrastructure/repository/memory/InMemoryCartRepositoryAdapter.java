package com.audiencecheckout.bff.infrastructure.repository.memory;

import com.audiencecheckout.bff.domain.model.Cart;
import com.audiencecheckout.bff.domain.port.out.CartRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCartRepositoryAdapter implements CartRepositoryPort {

    private final Map<UUID, Cart> carts = new ConcurrentHashMap<>();

    @Override
    public Cart save(Cart cart) {
        carts.put(cart.id(), cart);
        return cart;
    }

    @Override
    public Optional<Cart> findById(UUID cartId) {
        return Optional.ofNullable(carts.get(cartId));
    }
}
