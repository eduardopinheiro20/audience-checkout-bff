package com.audiencecheckout.bff.api.controller;

import com.audiencecheckout.bff.api.dto.response.ApiStatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public ApiStatusResponse health() {
        return new ApiStatusResponse(
                "audience-checkout-bff",
                "UP"
        );
    }
}
