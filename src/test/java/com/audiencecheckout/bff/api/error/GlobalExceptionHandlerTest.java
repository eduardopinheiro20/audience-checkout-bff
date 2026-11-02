package com.audiencecheckout.bff.api.error;

import com.audiencecheckout.bff.api.controller.CheckoutController;
import com.audiencecheckout.bff.api.dto.request.CheckoutRequest;
import com.audiencecheckout.bff.application.exception.*;
import com.audiencecheckout.bff.application.service.CheckoutApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        var service = Mockito.mock(CheckoutApplicationService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CheckoutController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

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

    @Test
    void shouldHandleInvalidCardData() {
        var cartId = UUID.randomUUID();

        var exception =
                new InvalidCardDataException(cartId);

        var response =
                handler.handleInvalidCardData(exception);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        var body = response.getBody();

        assertEquals(
                "BLOCK",
                body.displayType()
        );

        assertEquals(
                "INVALID_CARD_DATA",
                body.errorCode()
        );

        assertEquals(
                cartId,
                body.cartId()
        );

        assertEquals(
                "P-1005",
                body.frontendError().code()
        );

        assertEquals(
                "Invalid card data",
                body.frontendError().title()
        );

        assertEquals(
                "Review card data",
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
    }

    @Test
    void shouldHandleValidationErrorWithoutExposingRejectedValues() throws Exception {
        var bindingResult = new BeanPropertyBindingResult(
                new Object(), "checkoutRequest"
        );
        bindingResult.rejectValue(null, "NotNull"); // placeholder, see note below

        var method = CheckoutController.class.getMethod(
                "checkout", CheckoutRequest.class
        );

        var exception = new MethodArgumentNotValidException(
                new MethodParameter(method, 0),
                bindingResult
        );

        var response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        var body = response.getBody();

        assertNotNull(body);
        assertEquals("BLOCK", body.displayType());
        assertEquals("INVALID_REQUEST", body.errorCode());
        assertNull(body.cartId());
        assertEquals("P-9000", body.frontendError().code());
        assertNotNull(body.frontendError().primaryButton());
    }

    @Test
    void shouldHandleMalformedRequestBody() {
        var exception = new HttpMessageNotReadableException(
                "Invalid JSON",
                Mockito.mock(HttpInputMessage.class)
        );

        var response = handler.handleUnreadableBody(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        var body = response.getBody();

        assertNotNull(body);
        assertEquals("MALFORMED_REQUEST", body.errorCode());
        assertEquals("P-9001", body.frontendError().code());
    }

    @Test
    void shouldHandleUnexpectedErrorWithoutLeakingDetails() {
        var exception = new RuntimeException("db password is secret");

        var response = handler.handleUnexpected(exception);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        var body = response.getBody();

        assertNotNull(body);
        assertEquals("UNEXPECTED_ERROR", body.errorCode());
        assertEquals("P-9999", body.frontendError().code());
        assertTrue(
                !body.frontendError().description().contains("secret")
        );
    }
}