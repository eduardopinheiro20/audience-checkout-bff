package com.audiencecheckout.bff.api.error;

import com.audiencecheckout.bff.application.exception.CartAlreadyApprovedException;
import com.audiencecheckout.bff.application.exception.CartNotFoundException;
import com.audiencecheckout.bff.application.exception.PaymentDeclinedException;
import com.audiencecheckout.bff.application.exception.PaymentMethodNotAllowedException;
import com.audiencecheckout.bff.application.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldHandleProductNotFound() {
        var exception =
                new ProductNotFoundException("invalid-product");

        var response =
                handler.handleProductNotFound(exception);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        var body = response.getBody();

        assertEquals(
                "BLOCK",
                body.displayType()
        );

        assertEquals(
                "PRODUCT_NOT_FOUND",
                body.errorCode()
        );

        assertNull(
                body.cartId()
        );

        assertEquals(
                "P-1000",
                body.frontendError().code()
        );

        assertEquals(
                "Product not found",
                body.frontendError().title()
        );

        assertEquals(
                "Back to checkout",
                body.frontendError()
                        .primaryButton()
                        .label()
        );
    }

    @Test
    void shouldHandleCartNotFound() {
        var cartId = UUID.randomUUID();

        var exception =
                new CartNotFoundException(cartId);

        var response =
                handler.handleCartNotFound(exception);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        var body = response.getBody();

        assertEquals(
                "CART_NOT_FOUND",
                body.errorCode()
        );

        assertEquals(
                cartId,
                body.cartId()
        );

        assertEquals(
                "P-1002",
                body.frontendError().code()
        );
    }

    @Test
    void shouldHandlePaymentMethodNotAllowed() {
        var cartId = UUID.randomUUID();

        var exception =
                new PaymentMethodNotAllowedException(
                        cartId,
                        "Bitcoin"
                );

        var response =
                handler.handlePaymentMethodNotAllowed(exception);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        var body = response.getBody();

        assertEquals(
                "PAYMENT_METHOD_NOT_ALLOWED",
                body.errorCode()
        );

        assertEquals(
                cartId,
                body.cartId()
        );

        assertEquals(
                "P-1003",
                body.frontendError().code()
        );

        assertEquals(
                "Change payment method",
                body.frontendError()
                        .primaryButton()
                        .label()
        );
    }

    @Test
    void shouldHandlePaymentDeclined() {
        var cartId = UUID.randomUUID();

        var exception =
                new PaymentDeclinedException(cartId);

        var response =
                handler.handlePaymentDeclined(exception);

        assertEquals(
                HttpStatus.UNPROCESSABLE_ENTITY,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        var body = response.getBody();

        assertEquals(
                "BLOCK",
                body.displayType()
        );

        assertEquals(
                "PAYMENT_DECLINED",
                body.errorCode()
        );

        assertEquals(
                cartId,
                body.cartId()
        );

        assertEquals(
                "P-1001",
                body.frontendError().code()
        );

        assertEquals(
                "Payment not approved",
                body.frontendError().title()
        );

        assertEquals(
                "Try again",
                body.frontendError()
                        .primaryButton()
                        .label()
        );

        assertEquals(
                "RETRY",
                body.frontendError()
                        .primaryButton()
                        .url()
        );

        assertNotNull(
                body.frontendError()
                        .secondaryButton()
        );

        assertEquals(
                "Change payment method",
                body.frontendError()
                        .secondaryButton()
                        .label()
        );
    }

    @Test
    void shouldHandleCartAlreadyApproved() {
        var cartId = UUID.randomUUID();

        var exception =
                new CartAlreadyApprovedException(cartId);

        var response =
                handler.handleCartAlreadyApproved(exception);

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        var body = response.getBody();

        assertEquals(
                "CART_ALREADY_APPROVED",
                body.errorCode()
        );

        assertEquals(
                cartId,
                body.cartId()
        );

        assertEquals(
                "P-1004",
                body.frontendError().code()
        );

        assertEquals(
                "Payment already completed",
                body.frontendError().title()
        );
    }
}