package com.example.personnes.dto;

import jakarta.validation.constraints.NotNull;

public record ActifRequest(
    @NotNull Boolean actif)
    {
}
