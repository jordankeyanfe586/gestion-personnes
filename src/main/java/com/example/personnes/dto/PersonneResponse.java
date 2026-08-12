package com.example.personnes.dto;

import com.example.personnes.model.TypePersonne;

public record PersonneResponse(
    Long id,
    String nom,
    String prenom,
    String email,
    int age,
    TypePersonne type,
    boolean actif)
    {
}
