package com.audiencecheckout.bff.api.dto.error;

import java.util.UUID;

public record ApiErrorResponse(
        String displayType,
        String errorCode,
        UUID cartId,
        FrontendErrorResponse frontendError
) {
}