package id.ihaesge.apiservice.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateContentStatusRequest(
        @NotBlank String statusCode
) {}
