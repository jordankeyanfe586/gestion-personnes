package com.example.personnes.model;

import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Personne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String nom;
    private  String prenom;
    private  String email;
    private  int age;

    @Enumerated(EnumType.STRING)
    private  TypePersonne type;

    private  boolean actif;

    protected Personne() {

    }

    private Personne(Builder builder) {
        this.id = builder.id;
        this.nom = builder.nom;
        this.prenom = builder.prenom;
        this.email = builder.email;
        this.age = builder.age;
        this.type = builder.type;
        this.actif = builder.actif;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public int getage() {
        return age;
    }

    public TypePersonne getType() {
        return type;
    }

    public boolean getActif() {
        return actif;
    }

    public static class Builder {
        private  Long id;
        private  String nom;
        private  String prenom;
        private  String email;
        private  int age;
        private  TypePersonne type;
        private  boolean actif;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public Builder prenom(String prenom) {
            this.prenom = prenom;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder type(TypePersonne type) {
            this.type = type;
            return this;
        }

        public Builder actif(boolean actif) {
            this.actif = actif;
            return this;
        }

        public Personne build() {
            return new Personne(this);
        }
    }
}
