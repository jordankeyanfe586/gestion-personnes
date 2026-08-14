package com.example.personnes.controller;

import com.example.personnes.dto.PersonneResponse;
import com.example.personnes.exception.PersonneIntrouvableException;
import com.example.personnes.model.TypePersonne;
import com.example.personnes.service.PersonneService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonneController.class)
public class PersonneControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonneService service;

    @Test
    void postInvalideRetourne400() throws Exception {
        mockMvc.perform(post("/api/personnes")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "nom": "",
                    "prenom": "keyanfe",
                    "email": "email-invalide",
                    "age": 0,
                    "type": "ETUDIANT"
                }
                """))
            .andExpect(status().isBadRequest());


        verifyNoInteractions(service);
    }
    @Test
    void getPersonneInexistanteRetourne404() throws Exception {
        Long id = 999L;
        String message =
            "Aucune personne trouvée avec l'identifiant" + id;

        when(service.trouverParId(id))
            .thenThrow(
                new PersonneIntrouvableException(message)
        );

        mockMvc.perform(get("/api/personnes/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message")
                .value(message));

        verify(service).trouverParId(id);


    }

    @Test
    void patchActifAvecIdExistantRetourne200() throws Exception {
        Long id = 1L;
        PersonneResponse response = new PersonneResponse(
            id,
            "jordan",
            "keyanfe",
            "jordan@gmail.com",
            26,
            TypePersonne.ETUDIANT,
            true
        );

        when(service.changerStatutActif(id, true))
            .thenReturn(response);

        mockMvc.perform(patch("/api/personnes/{id}/actif", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "actif": true
                }
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.actif").value(true));

        verify(service).changerStatutActif(id, true);
    }

    @Test
    void patchActifAvecIdInexistantRetourne404() throws Exception {
        Long id = 999L;
        String message =
            "Aucune personne trouvée avec l'identifiant" + id;

        when(service.changerStatutActif(id, true))
            .thenThrow(
                new PersonneIntrouvableException(message)
        );

        mockMvc.perform(patch("/api/personnes/{id}/actif", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "actif": true
                }
                """))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message")
                .value(message));

        verify(service).changerStatutActif(id, true);
    }

    @Test
    void getRechercheAvecFiltresRetourneUnePage() throws Exception {
        PersonneResponse response = new PersonneResponse(
            1L, "Jordan", "Keyanfe", "jordan@gmail.com", 26,
            TypePersonne.EMPLOYE, true
        );
        when(service.rechercher(eq("ord"), eq(TypePersonne.EMPLOYE), eq(true), any(Pageable.class)))
            .thenReturn(new PageImpl<>(java.util.List.of(response)));

        mockMvc.perform(get("/api/personnes")
            .param("nom", "ord")
            .param("type", "EMPLOYE")
            .param("actif", "true")
            .param("page", "0")
            .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].nom").value("Jordan"))
            .andExpect(jsonPath("$.content[0].type").value("EMPLOYE"))
            .andExpect(jsonPath("$.content[0].actif").value(true));

        verify(service).rechercher(eq("ord"), eq(TypePersonne.EMPLOYE), eq(true), any(Pageable.class));
    }

    @Test
    void getRechercheSansResultatRetourneUnePageVide() throws Exception {
        when(service.rechercher(eq(null), eq(null), eq(null), any(Pageable.class)))
            .thenReturn(new PageImpl<>(java.util.List.of()));

        mockMvc.perform(get("/api/personnes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty());

        verify(service).rechercher(eq(null), eq(null), eq(null), any(Pageable.class));
    }

}
