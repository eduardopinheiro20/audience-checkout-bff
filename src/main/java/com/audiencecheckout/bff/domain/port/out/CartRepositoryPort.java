package com.audiencecheckout.bff.domain.port.out;

import com.audiencecheckout.bff.domain.model.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepositoryPort {

    Cart save(Cart cart);

    Optional<Cart> findById(UUID cartId);
}
