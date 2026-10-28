package com.audiencecheckout.bff.api.dto.request;

public record PaymentMethodRequest(
        String name,
        CardRequest card
) {
}