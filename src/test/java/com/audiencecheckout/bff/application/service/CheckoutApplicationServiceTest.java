package com.audiencecheckout.bff.application.service;

import com.audiencecheckout.bff.api.dto.request.CardRequest;
import com.audiencecheckout.bff.api.dto.request.CheckoutRequest;
import com.audiencecheckout.bff.api.dto.request.PaymentMethodRequest;
import com.audiencecheckout.bff.api.dto.request.PreCheckoutRequest;
import com.audiencecheckout.bff.api.dto.request.ProductSelectionRequest;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutApplicationServiceTest {

    @Mock
    private ProductCatalogPort productCatalogPort;

    @Mock
    private PaymentMethodPort paymentMethodPort;

    @Mock
    private CartRepositoryPort cartRepositoryPort;

    private CheckoutApplicationService service;

    private Product premiumProduct;

    private List<PaymentMethod> paymentMethods;

    @BeforeEach
    void setUp() {
        service = new CheckoutApplicationService(
                productCatalogPort,
                paymentMethodPort,
                cartRepositoryPort
        );

        premiumProduct = new Product(
                "premium",
                "Premium Plan",
                "Complete monthly subscription",
                new BigDecimal("39.90"),
                "BRL"
        );

        paymentMethods = List.of(
                new PaymentMethod("Credit Card"),
                new PaymentMethod("Debit Card"),
                new PaymentMethod("Pix")
        );
    }

    @Test
    void shouldCreatePreCheckoutSuccessfully() {
        var request = new PreCheckoutRequest(
                List.of(
                        new ProductSelectionRequest("premium")
                )
        );

        when(productCatalogPort.findBySlug("premium"))
                .thenReturn(Optional.of(premiumProduct));

        when(paymentMethodPort.findAvailableMethods())
                .thenReturn(paymentMethods);

        when(cartRepositoryPort.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.preCheckout(request);

        assertNotNull(response.cartId());

        assertEquals(
                1,
                response.products().size()
        );

        assertEquals(
                "premium",
                response.products().getFirst().slug()
        );

        assertEquals(
                new BigDecimal("39.90"),
                response.products().getFirst().price()
        );

        assertEquals(
                3,
                response.paymentMethods().size()
        );

        verify(productCatalogPort)
                .findBySlug("premium");

        verify(paymentMethodPort)
                .findAvailableMethods();

        verify(cartRepositoryPort)
                .save(any(Cart.class));
    }

    @Test
    void shouldThrowWhenProductDoesNotExist() {
        var request = new PreCheckoutRequest(
                List.of(
                        new ProductSelectionRequest("invalid-product")
                )
        );

        when(productCatalogPort.findBySlug("invalid-product"))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.preCheckout(request)
        );
    }

    @Test
    void shouldApproveCheckoutSuccessfully() {
        var cart = createCart();

        var request = createCreditCardCheckoutRequest(
                cart.id(),
                "4111111111114242"
        );

        when(cartRepositoryPort.findById(cart.id()))
                .thenReturn(Optional.of(cart));

        when(cartRepositoryPort.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.checkout(request);

        assertEquals(
                "APPROVED",
                response.status()
        );

        assertEquals(
                "Credit Card",
                response.payment().method()
        );

        assertEquals(
                "4242",
                response.payment().lastDigits()
        );

        assertEquals(
                new BigDecimal("39.90"),
                response.payment().amount()
        );

        assertEquals(
                "BRL",
                response.payment().currency()
        );

        assertNotNull(response.paidAt());

        verify(cartRepositoryPort)
                .findById(cart.id());

        verify(cartRepositoryPort)
                .save(any(Cart.class));
    }

    @Test
    void shouldDeclineCardEndingIn0002() {
        var cart = createCart();

        var request = createCreditCardCheckoutRequest(
                cart.id(),
                "4111111111110002"
        );

        when(cartRepositoryPort.findById(cart.id()))
                .thenReturn(Optional.of(cart));

        when(cartRepositoryPort.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(
                PaymentDeclinedException.class,
                () -> service.checkout(request)
        );

        verify(cartRepositoryPort)
                .save(any(Cart.class));
    }

    @Test
    void shouldThrowWhenCartDoesNotExist() {
        var cartId = UUID.randomUUID();

        var request = createCreditCardCheckoutRequest(
                cartId,
                "4111111111114242"
        );

        when(cartRepositoryPort.findById(cartId))
                .thenReturn(Optional.empty());

        assertThrows(
                CartNotFoundException.class,
                () -> service.checkout(request)
        );
    }

    @Test
    void shouldThrowWhenPaymentMethodIsNotAllowed() {
        var cart = createCart();

        var request = new CheckoutRequest(
                cart.id(),
                new PaymentMethodRequest(
                        "Bitcoin",
                        null
                )
        );

        when(cartRepositoryPort.findById(cart.id()))
                .thenReturn(Optional.of(cart));

        assertThrows(
                PaymentMethodNotAllowedException.class,
                () -> service.checkout(request)
        );
    }

    @Test
    void shouldThrowWhenCartIsAlreadyApproved() {
        var cart = createCart().approve();

        var request = createCreditCardCheckoutRequest(
                cart.id(),
                "4111111111114242"
        );

        when(cartRepositoryPort.findById(cart.id()))
                .thenReturn(Optional.of(cart));

        assertThrows(
                CartAlreadyApprovedException.class,
                () -> service.checkout(request)
        );
    }

    @Test
    void shouldApprovePixCheckoutSuccessfully() {
        var cart = createCart();

        var request = new CheckoutRequest(
                cart.id(),
                new PaymentMethodRequest(
                        "Pix",
                        null
                )
        );

        when(cartRepositoryPort.findById(cart.id()))
                .thenReturn(Optional.of(cart));

        when(cartRepositoryPort.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.checkout(request);

        assertEquals(
                "APPROVED",
                response.status()
        );

        assertEquals(
                "Pix",
                response.payment().method()
        );

        assertNotNull(
                response.payment().pixCode()
        );

        assertTrue(
                response.payment()
                        .pixCode()
                        .startsWith("PIX-")
        );
    }

    private Cart createCart() {
        return Cart.create(
                List.of(premiumProduct),
                paymentMethods
        );
    }

    private CheckoutRequest createCreditCardCheckoutRequest(
            UUID cartId,
            String cardNumber
    ) {
        return new CheckoutRequest(
                cartId,
                new PaymentMethodRequest(
                        "Credit Card",
                        new CardRequest(
                                "Ana Souza",
                                cardNumber,
                                "12/28",
                                "123"
                        )
                )
        );
    }
}