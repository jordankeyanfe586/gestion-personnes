package com.example.personnes.repository;

import com.example.personnes.model.Personne;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PersonneRepository extends JpaRepository<Personne, Long>, JpaSpecificationExecutor<Personne> {
   Boolean existsByEmail(String email);
   Optional<Personne> findByEmail(String email);

}
