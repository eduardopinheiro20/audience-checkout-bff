package com.audiencecheckout.bff.api.dto.error;

public record FrontendErrorResponse(
        String code,
        String title,
        String description,
        ErrorButtonResponse primaryButton,
        ErrorButtonResponse secondaryButton
) {
}