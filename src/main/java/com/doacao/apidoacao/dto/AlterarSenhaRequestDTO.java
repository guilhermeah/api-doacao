package com.doacao.apidoacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AlterarSenhaRequestDTO {

    @NotBlank(message = "A senha é obrigatória")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!*()_\\-]).{8,}$",
        message = "A senha deve ter no mínimo 8 caracteres, incluindo maiúscula, minúscula, número e símbolo especial (@#$%...)"
    )
    private String senha;

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
