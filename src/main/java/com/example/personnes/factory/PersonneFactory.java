package com.example.personnes.factory;
import com.example.personnes.dto.PersonneRequest;
import com.example.personnes.model.Personne;
public class PersonneFactory {

    public static Personne creerPersonne(PersonneRequest personneRequest) {

        return new Personne.Builder()
            .nom(personneRequest.nom())
            .prenom(personneRequest.prenom())
            .email(personneRequest.email())
            .age(personneRequest.age())
            .type(personneRequest.type())
            .actif(true)
            .build();
    }
}
