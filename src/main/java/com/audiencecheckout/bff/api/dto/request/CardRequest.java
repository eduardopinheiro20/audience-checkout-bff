package com.audiencecheckout.bff.api.dto.request;

public record CardRequest(
        String holderName,
        String number,
        String expiry,
        String cvv
) {
}