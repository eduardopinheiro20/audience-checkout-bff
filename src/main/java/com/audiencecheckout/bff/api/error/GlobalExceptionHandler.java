package com.audiencecheckout.bff.api.error;

import com.audiencecheckout.bff.api.dto.error.ApiErrorResponse;
import com.audiencecheckout.bff.api.dto.error.ErrorButtonResponse;
import com.audiencecheckout.bff.api.dto.error.FrontendErrorResponse;
import com.audiencecheckout.bff.application.exception.CartAlreadyApprovedException;
import com.audiencecheckout.bff.application.exception.CartNotFoundException;
import com.audiencecheckout.bff.application.exception.PaymentDeclinedException;
import com.audiencecheckout.bff.application.exception.PaymentMethodNotAllowedException;
import com.audiencecheckout.bff.application.exception.ProductNotFoundException;
import com.audiencecheckout.bff.application.exception.InvalidCardDataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFound(
            ProductNotFoundException exception
    ) {
        var response = new ApiErrorResponse(
                "BLOCK",
                "PRODUCT_NOT_FOUND",
                null,
                new FrontendErrorResponse(
                        "P-1000",
                        "Product not found",
                        "The selected product does not exist: "
                                + exception.getProductSlug(),
                        new ErrorButtonResponse(
                                "Back to checkout",
                                "/checkout/payment"
                        ),
                        null
                )
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCartNotFound(
            CartNotFoundException exception
    ) {
        var response = new ApiErrorResponse(
                "BLOCK",
                "CART_NOT_FOUND",
                exception.getCartId(),
                new FrontendErrorResponse(
                        "P-1002",
                        "Cart not found",
                        "The checkout cart could not be found.",
                        new ErrorButtonResponse(
                                "Back to checkout",
                                "/checkout/payment"
                        ),
                        null
                )
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(PaymentMethodNotAllowedException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentMethodNotAllowed(
            PaymentMethodNotAllowedException exception
    ) {
        var response = new ApiErrorResponse(
                "BLOCK",
                "PAYMENT_METHOD_NOT_ALLOWED",
                exception.getCartId(),
                new FrontendErrorResponse(
                        "P-1003",
                        "Payment method not allowed",
                        "The selected payment method is not available for this cart.",
                        new ErrorButtonResponse(
                                "Change payment method",
                                "/checkout/payment"
                        ),
                        null
                )
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(PaymentDeclinedException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentDeclined(
            PaymentDeclinedException exception
    ) {
        var response = new ApiErrorResponse(
                "BLOCK",
                "PAYMENT_DECLINED",
                exception.getCartId(),
                new FrontendErrorResponse(
                        "P-1001",
                        "Payment not approved",
                        "The payment could not be completed. Try again or choose another payment method.",
                        new ErrorButtonResponse(
                                "Try again",
                                "RETRY"
                        ),
                        new ErrorButtonResponse(
                                "Change payment method",
                                "/checkout/payment"
                        )
                )
        );

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(response);
    }

    @ExceptionHandler(CartAlreadyApprovedException.class)
    public ResponseEntity<ApiErrorResponse> handleCartAlreadyApproved(
            CartAlreadyApprovedException exception
    ) {
        var response = new ApiErrorResponse(
                "BLOCK",
                "CART_ALREADY_APPROVED",
                exception.getCartId(),
                new FrontendErrorResponse(
                        "P-1004",
                        "Payment already completed",
                        "This cart has already been approved and cannot be paid again.",
                        new ErrorButtonResponse(
                                "Back to checkout",
                                "/checkout/payment"
                        ),
                        null
                )
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(InvalidCardDataException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCardData(
            InvalidCardDataException exception
    ) {
        var response = new ApiErrorResponse(
                "BLOCK",
                "INVALID_CARD_DATA",
                exception.getCartId(),
                new FrontendErrorResponse(
                        "P-1005",
                        "Invalid card data",
                        "The card information is incomplete or invalid.",
                        new ErrorButtonResponse(
                                "Review card data",
                                "RETRY"
                        ),
                        null
                )
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}