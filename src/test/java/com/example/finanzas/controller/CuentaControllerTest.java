package com.example.finanzas.controller;

import com.example.finanzas.dto.CuentaRequest;
import com.example.finanzas.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CuentaControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    private String token;

    @BeforeEach
    void setup() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setNombre("Cuenta User");
        register.setCorreo("cuentas-" + UUID.randomUUID() + "@correo.com");
        register.setPassword("Password1");

        String response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andReturn().getResponse().getContentAsString();
        token = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void createCuentaShouldWork() throws Exception {
        CuentaRequest request = new CuentaRequest();
        request.setNombre("Bancolombia");
        request.setTipo(com.example.finanzas.entity.CuentaTipo.BANCO);

        mockMvc.perform(post("/api/cuentas")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Bancolombia"))
                .andExpect(jsonPath("$.tipo").value("BANCO"))
                .andExpect(jsonPath("$.saldo").value(0));
    }

    @Test
    void listCuentasShouldReturnDefaultAccount() throws Exception {
        mockMvc.perform(get("/api/cuentas")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipo").value("EFECTIVO"));
    }

    @Test
    void updateCuentaShouldWork() throws Exception {
        CuentaRequest create = new CuentaRequest();
        create.setNombre("Ahorros");
        create.setTipo(com.example.finanzas.entity.CuentaTipo.AHORRO);

        String createResponse = mockMvc.perform(post("/api/cuentas")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)))
                .andReturn().getResponse().getContentAsString();
        Long cuentaId = objectMapper.readTree(createResponse).get("id").asLong();

        CuentaRequest update = new CuentaRequest();
        update.setNombre("Ahorros Editada");
        update.setTipo(com.example.finanzas.entity.CuentaTipo.OTRO);

        mockMvc.perform(put("/api/cuentas/{id}", cuentaId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ahorros Editada"))
                .andExpect(jsonPath("$.tipo").value("OTRO"));
    }

    @Test
    void deleteCuentaShouldSoftDelete() throws Exception {
        CuentaRequest create = new CuentaRequest();
        create.setNombre("Eliminar");
        create.setTipo(com.example.finanzas.entity.CuentaTipo.OTRO);

        String createResponse = mockMvc.perform(post("/api/cuentas")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)))
                .andReturn().getResponse().getContentAsString();
        Long cuentaId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/api/cuentas/{id}", cuentaId)
                .param("confirm", "true")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cuenta eliminada correctamente"));
    }
}
