package com.audiencecheckout.bff.application.service;

import com.audiencecheckout.bff.api.dto.request.CheckoutRequest;
import com.audiencecheckout.bff.api.dto.request.PreCheckoutRequest;
import com.audiencecheckout.bff.api.dto.response.CheckoutPaymentResponse;
import com.audiencecheckout.bff.api.dto.response.CheckoutProductResponse;
import com.audiencecheckout.bff.api.dto.response.CheckoutResponse;
import com.audiencecheckout.bff.api.dto.response.PaymentMethodResponse;
import com.audiencecheckout.bff.api.dto.response.PreCheckoutResponse;
import com.audiencecheckout.bff.api.dto.response.ProductResponse;
import com.audiencecheckout.bff.application.exception.CartAlreadyApprovedException;
import com.audiencecheckout.bff.application.exception.CartNotFoundException;
import com.audiencecheckout.bff.application.exception.PaymentDeclinedException;
import com.audiencecheckout.bff.application.exception.PaymentMethodNotAllowedException;
import com.audiencecheckout.bff.application.exception.ProductNotFoundException;
import com.audiencecheckout.bff.domain.model.Cart;
import com.audiencecheckout.bff.domain.model.PaymentMethod;
import com.audiencecheckout.bff.domain.model.Product;
import com.audiencecheckout.bff.domain.port.out.CartRepositoryPort;
import com.audiencecheckout.bff.domain.port.out.PaymentMethodPort;
import com.audiencecheckout.bff.domain.port.out.ProductCatalogPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CheckoutApplicationService {

    private static final String CREDIT_CARD = "Credit Card";
    private static final String DEBIT_CARD = "Debit Card";
    private static final String PIX = "Pix";

    private final ProductCatalogPort productCatalogPort;
    private final PaymentMethodPort paymentMethodPort;
    private final CartRepositoryPort cartRepositoryPort;

    public CheckoutApplicationService(
            ProductCatalogPort productCatalogPort,
            PaymentMethodPort paymentMethodPort,
            CartRepositoryPort cartRepositoryPort
    ) {
        this.productCatalogPort = productCatalogPort;
        this.paymentMethodPort = paymentMethodPort;
        this.cartRepositoryPort = cartRepositoryPort;
    }

    public PreCheckoutResponse preCheckout(PreCheckoutRequest request) {
        var products = resolveProducts(request);
        var paymentMethods = paymentMethodPort.findAvailableMethods();

        var cart = Cart.create(
                products,
                paymentMethods
        );

        var savedCart = cartRepositoryPort.save(cart);

        return new PreCheckoutResponse(
                savedCart.id(),
                savedCart.products()
                        .stream()
                        .map(this::toProductResponse)
                        .toList(),
                savedCart.paymentMethods()
                        .stream()
                        .map(this::toPaymentMethodResponse)
                        .toList()
        );
    }

    public CheckoutResponse checkout(CheckoutRequest request) {
        var cart = findCart(request.cartId());

        validateCart(cart);
        validatePaymentMethod(cart, request);

        if (isCardPayment(request.paymentMethod().name())) {
            validateCardData(request);
            validateCardApproval(cart, request);
        }

        var approvedCart = cartRepositoryPort.save(cart.approve());

        return buildCheckoutResponse(
                approvedCart,
                request
        );
    }

    private Cart findCart(UUID cartId) {
        return cartRepositoryPort.findById(cartId)
                .orElseThrow(() ->
                        new CartNotFoundException(cartId)
                );
    }

    private void validateCart(Cart cart) {
        if (cart.isApproved()) {
            throw new CartAlreadyApprovedException(
                    cart.id()
            );
        }
    }

    private void validatePaymentMethod(
            Cart cart,
            CheckoutRequest request
    ) {
        var paymentMethod = request.paymentMethod();

        if (paymentMethod == null
                || !cart.allowsPaymentMethod(paymentMethod.name())) {

            throw new PaymentMethodNotAllowedException(
                    cart.id(),
                    paymentMethod != null
                            ? paymentMethod.name()
                            : null
            );
        }
    }

    private boolean isCardPayment(String paymentMethod) {
        return CREDIT_CARD.equalsIgnoreCase(paymentMethod)
                || DEBIT_CARD.equalsIgnoreCase(paymentMethod);
    }

    private void validateCardData(CheckoutRequest request) {
        var card = request.paymentMethod().card();

        if (card == null
                || card.number() == null
                || card.number().isBlank()) {

            throw new IllegalArgumentException(
                    "Card data is required for card payments"
            );
        }
    }

    private void validateCardApproval(
            Cart cart,
            CheckoutRequest request
    ) {
        var cardNumber = request.paymentMethod()
                .card()
                .number();

        if (cardNumber.endsWith("0002")) {
            cartRepositoryPort.save(cart.decline());

            throw new PaymentDeclinedException(
                    cart.id()
            );
        }
    }

    private CheckoutResponse buildCheckoutResponse(
            Cart cart,
            CheckoutRequest request
    ) {
        var paymentMethod = request.paymentMethod().name();

        var amount = calculateTotal(cart.products());
        var currency = cart.products().getFirst().currency();

        String lastDigits = null;
        String pixCode = null;

        if (isCardPayment(paymentMethod)) {
            lastDigits = extractLastDigits(
                    request.paymentMethod().card().number()
            );
        }

        if (PIX.equalsIgnoreCase(paymentMethod)) {
            pixCode = generatePixCode();
        }

        var payment = new CheckoutPaymentResponse(
                paymentMethod,
                lastDigits,
                pixCode,
                amount,
                currency
        );

        var products = cart.products()
                .stream()
                .map(this::toCheckoutProductResponse)
                .toList();

        return new CheckoutResponse(
                cart.id(),
                cart.status().name(),
                Instant.now(),
                payment,
                products
        );
    }

    private BigDecimal calculateTotal(List<Product> products) {
        return products.stream()
                .map(Product::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String extractLastDigits(String cardNumber) {
        var normalizedNumber = cardNumber.replaceAll("\\s+", "");

        return normalizedNumber.substring(
                Math.max(0, normalizedNumber.length() - 4)
        );
    }

    private String generatePixCode() {
        return "PIX-" + UUID.randomUUID();
    }

    private List<Product> resolveProducts(
            PreCheckoutRequest request
    ) {
        return request.products()
                .stream()
                .map(productRequest ->
                        productCatalogPort
                                .findBySlug(productRequest.slug())
                                .orElseThrow(() ->
                                        new ProductNotFoundException(
                                                productRequest.slug()
                                        )
                                )
                )
                .toList();
    }

    private ProductResponse toProductResponse(
            Product product
    ) {
        return new ProductResponse(
                product.slug(),
                product.name(),
                product.description(),
                product.price(),
                product.currency()
        );
    }

    private PaymentMethodResponse toPaymentMethodResponse(
            PaymentMethod paymentMethod
    ) {
        return new PaymentMethodResponse(
                paymentMethod.name()
        );
    }

    private CheckoutProductResponse toCheckoutProductResponse(
            Product product
    ) {
        return new CheckoutProductResponse(
                product.slug(),
                product.name(),
                product.price()
        );
    }
}