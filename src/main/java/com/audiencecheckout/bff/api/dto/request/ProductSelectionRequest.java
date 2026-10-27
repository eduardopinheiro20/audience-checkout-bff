package com.audiencecheckout.bff.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProductSelectionRequest(
        @NotBlank String slug
) {
}
