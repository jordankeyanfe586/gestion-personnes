package com.example.personnes.service;

import com.example.personnes.dto.PersonneRequest;
import com.example.personnes.dto.PersonneResponse;
import com.example.personnes.repository.PersonneRepository;
import com.example.personnes.factory.PersonneFactory;
import com.example.personnes.model.Personne;
import com.example.personnes.exception.EmaildejaUtiliserException;
import com.example.personnes.exception.PersonneIntrouvableException;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
        return pagePersonnes.map(p -> new PersonneResponse(
                p.getId(),
                p.getNom(),
                p.getPrenom(),
                p.getEmail(),
                p.getage(),
                p.getType(),
                p.getActif())
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

    public void supprimer(Long id) {
        PersonneResponse personneTrouver = trouverParId(id);
        repository.deleteById(personneTrouver.id());
    }

}
