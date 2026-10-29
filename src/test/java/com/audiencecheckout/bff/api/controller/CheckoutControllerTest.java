package com.audiencecheckout.bff.api.controller;

import com.audiencecheckout.bff.api.controller.CheckoutController;
import com.audiencecheckout.bff.api.dto.response.CheckoutPaymentResponse;
import com.audiencecheckout.bff.api.dto.response.CheckoutProductResponse;
import com.audiencecheckout.bff.api.dto.response.CheckoutResponse;
import com.audiencecheckout.bff.api.dto.response.PaymentMethodResponse;
import com.audiencecheckout.bff.api.dto.response.PreCheckoutResponse;
import com.audiencecheckout.bff.api.dto.response.ProductResponse;
import com.audiencecheckout.bff.application.service.CheckoutApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckoutController.class)
class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckoutApplicationService checkoutApplicationService;

    @Test
    void shouldReturnPreCheckoutSuccessfully() throws Exception {
        var cartId = UUID.randomUUID();

        var response = new PreCheckoutResponse(
                cartId,
                List.of(
                        new ProductResponse(
                                "premium",
                                "Premium Plan",
                                "Complete monthly subscription",
                                new BigDecimal("39.90"),
                                "BRL"
                        )
                ),
                List.of(
                        new PaymentMethodResponse("Credit Card"),
                        new PaymentMethodResponse("Debit Card"),
                        new PaymentMethodResponse("Pix")
                )
        );

        when(checkoutApplicationService.preCheckout(any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/operations/pre-checkout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "products": [
                                            {
                                              "slug": "premium"
                                            }
                                          ]
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.cartId")
                                .value(cartId.toString())
                )
                .andExpect(
                        jsonPath("$.products[0].slug")
                                .value("premium")
                )
                .andExpect(
                        jsonPath("$.products[0].name")
                                .value("Premium Plan")
                )
                .andExpect(
                        jsonPath("$.products[0].price")
                                .value(39.90)
                )
                .andExpect(
                        jsonPath("$.products[0].currency")
                                .value("BRL")
                )
                .andExpect(
                        jsonPath("$.paymentMethods.length()")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.paymentMethods[0].name")
                                .value("Credit Card")
                )
                .andExpect(
                        jsonPath("$.paymentMethods[1].name")
                                .value("Debit Card")
                )
                .andExpect(
                        jsonPath("$.paymentMethods[2].name")
                                .value("Pix")
                );

        verify(checkoutApplicationService)
                .preCheckout(any());
    }

    @Test
    void shouldReturnApprovedCardCheckoutSuccessfully()
            throws Exception {

        var cartId = UUID.randomUUID();
        var paidAt = Instant.parse(
                "2026-10-29T18:00:00Z"
        );

        var response = new CheckoutResponse(
                cartId,
                "APPROVED",
                paidAt,
                new CheckoutPaymentResponse(
                        "Credit Card",
                        "4242",
                        null,
                        new BigDecimal("39.90"),
                        "BRL"
                ),
                List.of(
                        new CheckoutProductResponse(
                                "premium",
                                "Premium Plan",
                                new BigDecimal("39.90")
                        )
                )
        );

        when(checkoutApplicationService.checkout(any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/operations/checkout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "cartId": "%s",
                                          "paymentMethod": {
                                            "name": "Credit Card",
                                            "card": {
                                              "holderName": "Ana Souza",
                                              "number": "4111111111114242",
                                              "expiry": "12/28",
                                              "cvv": "123"
                                            }
                                          }
                                        }
                                        """.formatted(cartId))
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.cartId")
                                .value(cartId.toString())
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("APPROVED")
                )
                .andExpect(
                        jsonPath("$.payment.method")
                                .value("Credit Card")
                )
                .andExpect(
                        jsonPath("$.payment.lastDigits")
                                .value("4242")
                )
                .andExpect(
                        jsonPath("$.payment.amount")
                                .value(39.90)
                )
                .andExpect(
                        jsonPath("$.payment.currency")
                                .value("BRL")
                )
                .andExpect(
                        jsonPath("$.products[0].slug")
                                .value("premium")
                )
                .andExpect(
                        jsonPath("$.products[0].name")
                                .value("Premium Plan")
                );

        verify(checkoutApplicationService)
                .checkout(any());
    }

    @Test
    void shouldReturnApprovedPixCheckoutSuccessfully()
            throws Exception {

        var cartId = UUID.randomUUID();

        var response = new CheckoutResponse(
                cartId,
                "APPROVED",
                Instant.parse(
                        "2026-10-29T18:00:00Z"
                ),
                new CheckoutPaymentResponse(
                        "Pix",
                        null,
                        "PIX-123456",
                        new BigDecimal("39.90"),
                        "BRL"
                ),
                List.of(
                        new CheckoutProductResponse(
                                "premium",
                                "Premium Plan",
                                new BigDecimal("39.90")
                        )
                )
        );

        when(checkoutApplicationService.checkout(any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/operations/checkout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "cartId": "%s",
                                          "paymentMethod": {
                                            "name": "Pix",
                                            "card": null
                                          }
                                        }
                                        """.formatted(cartId))
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("APPROVED")
                )
                .andExpect(
                        jsonPath("$.payment.method")
                                .value("Pix")
                )
                .andExpect(
                        jsonPath("$.payment.pixCode")
                                .value("PIX-123456")
                )
                .andExpect(
                        jsonPath("$.payment.lastDigits")
                                .doesNotExist()
                )
                .andExpect(
                        jsonPath("$.payment.amount")
                                .value(39.90)
                );

        verify(checkoutApplicationService)
                .checkout(any());
    }
}