package com.audiencecheckout.bff.infrastructure.client.mock;

import com.audiencecheckout.bff.domain.model.Product;
import com.audiencecheckout.bff.domain.port.out.ProductCatalogPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Component
public class MockProductCatalogAdapter implements ProductCatalogPort {

    private static final Map<String, Product> CATALOG = Map.of(
            "basic", new Product(
                    "basic",
                    "Basic Plan",
                    "Essential content subscription",
                    new BigDecimal("19.90"),
                    "BRL"
            ),
            "premium", new Product(
                    "premium",
                    "Premium Plan",
                    "Monthly subscription with the complete catalog",
                    new BigDecimal("39.90"),
                    "BRL"
            ),
            "family", new Product(
                    "family",
                    "Family Plan",
                    "Subscription designed for the whole family",
                    new BigDecimal("59.90"),
                    "BRL"
            )
    );

    @Override
    public Optional<Product> findBySlug(String slug) {
        return Optional.ofNullable(CATALOG.get(slug));
    }
}
