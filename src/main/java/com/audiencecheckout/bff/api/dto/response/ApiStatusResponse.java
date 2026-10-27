package com.audiencecheckout.bff.api.dto.response;

public record ApiStatusResponse(
        String service,
        String status
) {
}
