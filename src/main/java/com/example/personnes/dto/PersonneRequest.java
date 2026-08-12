package com.example.personnes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;

import com.example.personnes.model.TypePersonne;

import jakarta.validation.constraints.Email;

public record PersonneRequest (
    @NotBlank String nom,
    @NotBlank String prenom,
    @Email @NotBlank String email,
    @Positive int age,
    @NotNull TypePersonne type)
    {
}
