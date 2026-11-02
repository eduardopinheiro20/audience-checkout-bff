package com.audiencecheckout.bff.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record PaymentMethodRequest(
        @NotBlank String name,
        @Valid CardRequest card
) {
}