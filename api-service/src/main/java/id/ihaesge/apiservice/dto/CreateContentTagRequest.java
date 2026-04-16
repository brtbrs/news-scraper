package id.ihaesge.apiservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateContentTagRequest(
        @NotNull UUID contentId,
        @NotBlank String tag
) {}
