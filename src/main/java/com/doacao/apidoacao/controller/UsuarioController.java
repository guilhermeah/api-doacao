package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.AlterarSenhaRequestDTO;
import com.doacao.apidoacao.dto.LoginResponseDTO;
import com.doacao.apidoacao.dto.UsuarioRequestDTO;
import com.doacao.apidoacao.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Gestão de usuários do sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Lista todos os usuários")
    public List<LoginResponseDTO> listarTodos() {
        return usuarioService.listarTodos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria um novo usuário com senha criptografada")
    public LoginResponseDTO criar(@RequestBody @Valid UsuarioRequestDTO dto) {
        return usuarioService.criar(dto);
    }

    @PatchMapping("/{id}/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Altera a senha de um usuário")
    public void alterarSenha(@PathVariable Long id, @RequestBody @Valid AlterarSenhaRequestDTO dto) {
        usuarioService.alterarSenha(id, dto.getSenha());
    }

    @PatchMapping("/{id}/ativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Ativa ou desativa um usuário")
    public void ativarDesativar(@PathVariable Long id, @RequestParam boolean ativo) {
        usuarioService.ativarDesativar(id, ativo);
    }
}
