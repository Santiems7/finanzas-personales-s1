package com.example.finanzas.controller;

import com.example.finanzas.dto.LoginRequest;
import com.example.finanzas.dto.RegisterRequest;
import com.example.finanzas.entity.Usuario;
import com.example.finanzas.entity.UsuarioRol;
import com.example.finanzas.repository.UsuarioRepository;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void registerShouldCreateUserAndDefaultAccount() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Santiago");
        request.setCorreo("santiago-" + UUID.randomUUID() + "@correo.com");
        request.setPassword("Password1");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.nombre").value("Santiago"));
    }

    @Test
    void loginShouldReturnJwt() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNombre("Demo");
        String correo = "demo-" + UUID.randomUUID() + "@correo.com";
        usuario.setCorreo(correo);
        usuario.setPasswordHash(passwordEncoder.encode("Password1"));
        usuario.setActivo(true);
        usuario.setMoneda("COP");
        usuario.setRol(UsuarioRol.USUARIO_ESTANDAR);

        usuarioRepository.save(usuario);

        LoginRequest request = new LoginRequest();
        request.setCorreo(correo);
        request.setPassword("Password1");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void logoutShouldInvalidateToken() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setNombre("Logout User");
        register.setCorreo("logout-" + UUID.randomUUID() + "@correo.com");
        register.setPassword("Password1");

        String response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Sesión cerrada correctamente"));

        mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}
