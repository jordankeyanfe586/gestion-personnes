package com.example.personnes.service;

import com.example.personnes.dto.PersonneRequest;
import com.example.personnes.dto.PersonneResponse;
import com.example.personnes.repository.PersonneRepository;
import com.example.personnes.factory.PersonneFactory;
import com.example.personnes.model.Personne;
import com.example.personnes.model.TypePersonne;
import com.example.personnes.exception.EmaildejaUtiliserException;
import com.example.personnes.exception.PersonneIntrouvableException;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

@Service
public class PersonneService {
    private final PersonneRepository repository;

    public PersonneService(PersonneRepository repository) {
        this.repository = repository;
    }

    public PersonneResponse creer(PersonneRequest personneRequest) {
        if(repository.existsByEmail(personneRequest.email())){
            throw new EmaildejaUtiliserException("Email déja utiliser");
        }
        Personne personneFactory = PersonneFactory.creerPersonne(personneRequest);
        Personne personneSave = repository.save(personneFactory);
        return new PersonneResponse(
            personneSave.getId(),
            personneSave.getNom(),
            personneSave.getPrenom(),
            personneSave.getEmail(),
            personneSave.getage(),
            personneSave.getType(),
            personneSave.getActif()
        );
    }

    public PersonneResponse trouverParId(Long id ) {
        Personne personneTrouver = repository.findById(id)
            .orElseThrow(() -> new PersonneIntrouvableException(
                "Aucune personne trouvée avec l'identifiant" + id));

        return new PersonneResponse(
            personneTrouver.getId(),
            personneTrouver.getNom(),
            personneTrouver.getPrenom(),
            personneTrouver.getEmail(),
            personneTrouver.getage(),
            personneTrouver.getType(),
            personneTrouver.getActif()
            );
    }

    public Page<PersonneResponse> trouverToutes(Pageable peagable) {
        Page<Personne> pagePersonnes= repository.findAll(peagable);
        return pagePersonnes.map(this::versResponse);
    }

    public Page<PersonneResponse> rechercher(String nom, TypePersonne type, Boolean actif, Pageable pageable) {
        Specification<Personne> specification = (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (nom != null && !nom.isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("nom")),
                    "%" + nom.toLowerCase(Locale.ROOT) + "%"
                ));
            }
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (actif != null) {
                predicates.add(criteriaBuilder.equal(root.get("actif"), actif));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };

        return repository.findAll(specification, pageable).map(this::versResponse);
    }

    private PersonneResponse versResponse(Personne personne) {
        return new PersonneResponse(
            personne.getId(),
            personne.getNom(),
            personne.getPrenom(),
            personne.getEmail(),
            personne.getage(),
            personne.getType(),
            personne.getActif()
        );
    }

    public PersonneResponse modifier(PersonneRequest personneRequest) {
        Personne personneTrouver = repository.findByEmail(personneRequest.email())
            .orElseThrow(() ->  new IllegalArgumentException(
                "Aucune personne trouvée avec l'email" + personneRequest.email()));
            Personne personneFactory = PersonneFactory.creerPersonne(personneRequest);
            personneFactory.setId(personneTrouver.getId());
            Personne personneSave = repository.save(personneFactory);
            return new PersonneResponse(
                personneSave.getId(),
                personneSave.getNom(),
                personneSave.getPrenom(),
                personneSave.getEmail(),
                personneSave.getage(),
                personneSave.getType(),
                personneSave.getActif()
            );
    }

    public PersonneResponse changerStatutActif(Long id, boolean actif) {
        Personne personneTrouver = repository.findById(id)
            .orElseThrow(() -> new PersonneIntrouvableException(
                "Aucune personne trouvée avec l'identifiant" + id));

        personneTrouver.setActif(actif);
        Personne personneSave = repository.save(personneTrouver);

        return new PersonneResponse(
            personneSave.getId(),
            personneSave.getNom(),
            personneSave.getPrenom(),
            personneSave.getEmail(),
            personneSave.getage(),
            personneSave.getType(),
            personneSave.getActif()
        );
    }

    public void supprimer(Long id) {
        PersonneResponse personneTrouver = trouverParId(id);
        repository.deleteById(personneTrouver.id());
    }

}
