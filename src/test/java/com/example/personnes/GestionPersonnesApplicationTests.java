package com.example.personnes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.personnes.dto.PersonneRequest;
import com.example.personnes.dto.PersonneResponse;
import com.example.personnes.model.TypePersonne;
import com.example.personnes.repository.PersonneRepository;
import com.example.personnes.service.PersonneService;

import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GestionPersonnesApplicationTests {

	@Autowired
	private PersonneService service;

	@Autowired
	private PersonneRepository repository;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void nettoyerBase() {
		repository.deleteAll();
	}

	@Test
	void creerPersonnePersisteDansLaBase() {
		PersonneRequest request = new PersonneRequest(
			"jordan",
			"keyanfe",
			"personne@example.com",
			25,
			TypePersonne.ETUDIANT);

		PersonneResponse response = service.creer(request);

		assertEquals("personne@example.com", response.email());
		assertTrue(repository.existsByEmail("personne@example.com"));
	}

	@Test
	void envoyerUneRequettePost() throws Exception{
		mockMvc.perform(post("/api/personnes")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
					{
						"nom": "jordan",
						"prenom": "keyanfe",
						"email": "personne@example.com",
						"age": 26,
						"type": "ETUDIANT"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email")
				.value("personne@example.com"))
			.andExpect(jsonPath("$.id")
				.isNumber());

	}

}
