package com.example.personnes.repository;

import com.example.personnes.dto.PersonneRequest;
import com.example.personnes.factory.PersonneFactory;
import com.example.personnes.model.Personne;
import com.example.personnes.model.TypePersonne;
import com.example.personnes.dto.PersonneResponse;
import com.example.personnes.service.PersonneService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Import(PersonneService.class)
public class PersonneRepositoryTest {
    @Autowired
    private PersonneRepository repository;

    @Autowired
    private PersonneService service;

    @Test
    void suvergarderPermetDeRetrouverPersonneParEmail() {
        PersonneRequest request = new PersonneRequest(
            "jordan",
            "keyanfe",
            "personne@example.com",
            25,
            TypePersonne.ETUDIANT
        );

        Personne personne = PersonneFactory.creerPersonne(request);

        Personne personneSauvegardee = repository.saveAndFlush(personne);

        assertNotNull(personneSauvegardee.getId());
        assertTrue(repository.existsByEmail("personne@example.com"));

        Optional<Personne> resultat = repository.findByEmail("personne@example.com");

        assertTrue(resultat.isPresent());
        assertEquals(
            TypePersonne.ETUDIANT,
            resultat.get().getType()
        );
    }

    @Test
    void rechercheCombineLesCriteresEtRespecteLaPagination() {
        sauvegarder("Jordan", 25, TypePersonne.EMPLOYE, true);
        sauvegarder("JORDAN", 30, TypePersonne.EMPLOYE, true);
        sauvegarder("Jordan", 35, TypePersonne.ETUDIANT, true);
        sauvegarder("Jordan", 40, TypePersonne.EMPLOYE, false);

        Page<PersonneResponse> resultat = service.rechercher(
            "ordan", TypePersonne.EMPLOYE, true, null, null, PageRequest.of(0, 1)
        );

        assertEquals(2, resultat.getTotalElements());
        assertEquals(1, resultat.getContent().size());
        assertEquals(TypePersonne.EMPLOYE, resultat.getContent().getFirst().type());
        assertTrue(resultat.getContent().getFirst().actif());
    }

    @Test
    void rechercheSansCritereRetourneToutesLesPersonnesEtUneRechercheSansCorrespondanceEstVide() {
        sauvegarder("Jordan", 25, TypePersonne.ETUDIANT, true);
        sauvegarder("Alice", 40, TypePersonne.AUTRE, false);

        Page<PersonneResponse> toutes = service.rechercher(null, null, null, null, null, PageRequest.of(0, 10));
        Page<PersonneResponse> nomVide = service.rechercher(" ", null, null, null, null, PageRequest.of(0, 10));
        Page<PersonneResponse> aucune = service.rechercher("inconnu", null, null, null, null, PageRequest.of(0, 10));

        assertEquals(2, toutes.getTotalElements());
        assertEquals(2, nomVide.getTotalElements());
        assertTrue(aucune.isEmpty());
    }

    @Test
    void rechercheParAgeRespecteLesBornesInclusives() {
        sauvegarder("Alice", 20, TypePersonne.ETUDIANT, true);
        sauvegarder("Bruno", 25, TypePersonne.ETUDIANT, true);
        sauvegarder("Chloe", 30, TypePersonne.ETUDIANT, true);
        sauvegarder("David", 35, TypePersonne.ETUDIANT, true);

        Page<PersonneResponse> ageMin = service.rechercher(null, null, null, 30, null, PageRequest.of(0, 10));
        Page<PersonneResponse> ageMax = service.rechercher(null, null, null, null, 25, PageRequest.of(0, 10));
        Page<PersonneResponse> intervalle = service.rechercher(null, null, null, 25, 30, PageRequest.of(0, 10));

        assertEquals(2, ageMin.getTotalElements());
        assertEquals(2, ageMax.getTotalElements());
        assertEquals(2, intervalle.getTotalElements());
        assertTrue(ageMin.getContent().stream().allMatch(personne -> personne.age() >= 30));
        assertTrue(ageMax.getContent().stream().allMatch(personne -> personne.age() <= 25));
        assertTrue(intervalle.getContent().stream().allMatch(personne ->
            personne.age() >= 25 && personne.age() <= 30
        ));
        assertTrue(intervalle.getContent().stream().anyMatch(personne -> personne.age() == 25));
        assertTrue(intervalle.getContent().stream().anyMatch(personne -> personne.age() == 30));
    }

    private void sauvegarder(String nom, int age, TypePersonne type, boolean actif) {
        repository.saveAndFlush(new Personne.Builder()
            .nom(nom)
            .prenom("Prenom")
            .email(nom + type + actif + "@example.com")
            .age(age)
            .type(type)
            .actif(actif)
            .build());
    }
}
