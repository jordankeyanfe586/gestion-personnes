package com.example.personnes.repository;

import com.example.personnes.dto.PersonneRequest;
import com.example.personnes.factory.PersonneFactory;
import com.example.personnes.model.Personne;
import com.example.personnes.model.TypePersonne;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class PersonneRepositoryTest {
    @Autowired
    private PersonneRepository repository;

    @Test
    void suvergarderPermetDeRetrouverPersonneParEmail() {
        PersonneRequest request = new PersonneRequest(
            "jordan",
            "keyanfe",
            "jordan@gmail.com",
            25,
            TypePersonne.ETUDIANT
        );

        Personne personne = PersonneFactory.creerPersonne(request);

        Personne personneSauvegardee = repository.saveAndFlush(personne);

        assertNotNull(personneSauvegardee.getId());
        assertTrue(repository.existsByEmail("jordan@gmail.com"));

        Optional<Personne> resultat = repository.findByEmail("jordan@gmail.com");

        assertTrue(resultat.isPresent());
        assertEquals(
            TypePersonne.ETUDIANT,
            resultat.get().getType()
        );
    }
}
