package com.example.personnes.repository;

import com.example.personnes.model.Personne;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonneRepository extends JpaRepository<Personne, Long>{
   Boolean existsByEmail(String email);
   Optional<Personne> findByEmail(String email);

}
