package com.example.personnes.service;
import com.example.personnes.model.Personne;
import com.example.personnes.model.TypePersonne;
import com.example.personnes.repository.PersonneRepository;
import com.example.personnes.dto.PersonneRequest;
import com.example.personnes.dto.PersonneResponse;
import com.example.personnes.exception.EmaildejaUtiliserException;
import com.example.personnes.exception.PersonneIntrouvableException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.List;

public class PersonneServiceTest {
      private PersonneRepository repository;
      private PersonneService service;

    @BeforeEach
    void preparerTest() {
        repository = mock(PersonneRepository.class);
        service = new PersonneService(repository);
    }

    @Test
     void creerEmailDejaUtiliserDeclencheException() {
        PersonneRequest request = new PersonneRequest(
            "jordan",
            "keyanfe",
            "jordan@gmail.com",
            26,
            TypePersonne.ETUDIANT
        );

        when(repository.existsByEmail(request.email()))
            .thenReturn(true);

        EmaildejaUtiliserException exception = assertThrows(
            EmaildejaUtiliserException.class,
            () -> service.creer(request)
        );

        assertEquals("Email déja utiliser", exception.getMessage());

        verify(repository, never()).save(any(Personne.class));
    }

    @Test
    void creerAvecNouvelEmailSauvegardePersonne() {
        PersonneRequest request = new PersonneRequest(
            "jordan",
            "keyanfe",
            "jordan@gmail.com",
            26,
            TypePersonne.ETUDIANT
        );

        PersonneResponse response = new PersonneResponse(
            1L,
            "jordan",
            "keyanfe",
            "jordan@gmail.com",
            26,
            TypePersonne.ETUDIANT,
            true
        );

        when(repository.existsByEmail(request.email()))
            .thenReturn(false);

        when(repository.save(any(Personne.class)))
            .thenAnswer(invocation -> {
                Personne personne = invocation.getArgument(0);
                personne.setId(1L);
                return personne;
            });
        assertEquals(response, service.creer(request));
        verify(repository).save(any(Personne.class));
    }

    @Test
    void modifierUnePersonneDesactiveeConserveSonStatut() {
        Personne personneExistante = new Personne.Builder()
            .id(1L)
            .nom("Ancien nom")
            .prenom("Demo")
            .email("personne@example.com")
            .age(25)
            .type(TypePersonne.ETUDIANT)
            .actif(false)
            .build();
        PersonneRequest request = new PersonneRequest(
            "Nouveau nom", "Demo", "personne@example.com", 26, TypePersonne.EMPLOYE
        );

        when(repository.findByEmail(request.email()))
            .thenReturn(Optional.of(personneExistante));
        when(repository.save(any(Personne.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        PersonneResponse resultat = service.modifier(request);

        assertEquals(new PersonneResponse(
            1L, "Nouveau nom", "Demo", "personne@example.com", 26, TypePersonne.EMPLOYE, false
        ), resultat);
        org.mockito.ArgumentCaptor<Personne> personneSauvegardee =
            org.mockito.ArgumentCaptor.forClass(Personne.class);
        verify(repository).save(personneSauvegardee.capture());
        assertEquals(false, personneSauvegardee.getValue().getActif());
    }

    @Test
    void trouverAvecIdInexistantDeclencheException() {
        Long id = 120L;

        when(repository.findById(id))
            .thenReturn(Optional.empty());

        PersonneIntrouvableException execption = assertThrows(
            PersonneIntrouvableException.class,
            () -> service.trouverParId(id)
        );

        assertEquals("Aucune personne trouvée avec l'identifiant" + id, execption.getMessage());
    }

    @Test
    void changerStatutActifAvecIdExistantSauvegardeEtRetourneReponse() {
        Long id = 1L;
        Personne personne = new Personne.Builder()
            .id(id)
            .nom("jordan")
            .prenom("keyanfe")
            .email("jordan@gmail.com")
            .age(26)
            .type(TypePersonne.ETUDIANT)
            .actif(false)
            .build();

        PersonneResponse response = new PersonneResponse(
            id,
            "jordan",
            "keyanfe",
            "jordan@gmail.com",
            26,
            TypePersonne.ETUDIANT,
            true
        );

        when(repository.findById(id))
            .thenReturn(Optional.of(personne));

        when(repository.save(any(Personne.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals(response, service.changerStatutActif(id, true));

        verify(repository).save(any(Personne.class));
    }

    @Test
    void changerStatutActifAvecIdInexistantDeclencheException() {
        Long id = 120L;

        when(repository.findById(id))
            .thenReturn(Optional.empty());

        PersonneIntrouvableException execption = assertThrows(
            PersonneIntrouvableException.class,
            () -> service.changerStatutActif(id, true)
        );

        assertEquals("Aucune personne trouvée avec l'identifiant" + id, execption.getMessage());

        verify(repository, never()).save(any(Personne.class));
    }

    @Test
    void rechercherRetourneUnePageDePersonnesFiltrees() {
        Pageable pageable = PageRequest.of(1, 2);
        Personne personne = new Personne.Builder()
            .id(1L)
            .nom("Jordan")
            .prenom("Keyanfe")
            .email("jordan@gmail.com")
            .age(26)
            .type(TypePersonne.EMPLOYE)
            .actif(true)
            .build();

        when(repository.findAll(any(Specification.class), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(personne), pageable, 3));

        Page<PersonneResponse> resultat = service.rechercher(
            "ordan", TypePersonne.EMPLOYE, true, 25, 30, pageable
        );

        assertEquals(3, resultat.getTotalElements());
        assertEquals(1, resultat.getNumber());
        assertEquals("Jordan", resultat.getContent().getFirst().nom());
        verify(repository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void rechercherAvecAgeMinSuperieurAgeMaxDeclencheException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.rechercher(null, null, null, 30, 20, PageRequest.of(0, 10))
        );

        assertEquals("L'âge minimum doit être inférieur ou égal à l'âge maximum.", exception.getMessage());
        verify(repository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void rechercherAvecAgeMinNonPositifDeclencheException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.rechercher(null, null, null, 0, null, PageRequest.of(0, 10))
        );

        assertEquals("L'âge minimum doit être strictement positif.", exception.getMessage());
        verify(repository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void rechercherAvecAgeMaxNonPositifDeclencheException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.rechercher(null, null, null, null, -1, PageRequest.of(0, 10))
        );

        assertEquals("L'âge maximum doit être strictement positif.", exception.getMessage());
        verify(repository, never()).findAll(any(Specification.class), any(Pageable.class));
    }
}
