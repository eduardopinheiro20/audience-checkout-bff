package com.audiencecheckout.bff.domain.port.out;

import com.audiencecheckout.bff.domain.model.Product;

import java.util.Optional;

public interface ProductCatalogPort {

    Optional<Product> findBySlug(String slug);
}
