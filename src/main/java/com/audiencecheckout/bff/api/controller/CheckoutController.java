package com.audiencecheckout.bff.api.controller;

import com.audiencecheckout.bff.api.dto.request.CheckoutRequest;
import com.audiencecheckout.bff.api.dto.request.PreCheckoutRequest;
import com.audiencecheckout.bff.api.dto.response.CheckoutResponse;
import com.audiencecheckout.bff.api.dto.response.PreCheckoutResponse;
import com.audiencecheckout.bff.application.service.CheckoutApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operations")
public class CheckoutController {

    private final CheckoutApplicationService checkoutApplicationService;

    public CheckoutController(
            CheckoutApplicationService checkoutApplicationService
    ) {
        this.checkoutApplicationService = checkoutApplicationService;
    }

    @PostMapping("/pre-checkout")
    public ResponseEntity<PreCheckoutResponse> preCheckout(
            @Valid @RequestBody PreCheckoutRequest request
    ) {
        var response = checkoutApplicationService.preCheckout(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(
            @RequestBody CheckoutRequest request
    ) {
        var response = checkoutApplicationService.checkout(request);

        return ResponseEntity.ok(response);
    }
}