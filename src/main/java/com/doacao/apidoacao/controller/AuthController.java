package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.LoginRequestDTO;
import com.doacao.apidoacao.dto.LoginResponseDTO;
import com.doacao.apidoacao.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login de usuários do sistema")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Realiza login com e-mail e senha")
    public LoginResponseDTO login(@RequestBody @Valid LoginRequestDTO dto) {
        return authService.login(dto);
    }
}
