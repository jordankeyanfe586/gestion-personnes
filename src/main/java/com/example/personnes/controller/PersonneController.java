package com.example.personnes.controller;

import com.example.personnes.dto.PersonneRequest;
import com.example.personnes.dto.PersonneResponse;
import com.example.personnes.service.PersonneService;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/personnes")
public class PersonneController {
    private final PersonneService service;

    public PersonneController(PersonneService service) {
        this.service = service;
    }

    @GetMapping
    public Page<PersonneResponse> trouverToutes(Pageable pageable) {
        return service.trouverToutes(pageable);
    }

    @GetMapping("/{id}")
    public PersonneResponse trouverparId(@PathVariable Long id) {
        return service.trouverParId(id);
    }

    @PostMapping
    public PersonneResponse creer(@Valid @RequestBody PersonneRequest personneRequestt){
        return service.creer(personneRequestt);
    }

    @PutMapping
    public PersonneResponse modifier(@Valid @RequestBody PersonneRequest personneRequestt) {
        return service.modifier(personneRequestt);
    }

    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }

}
