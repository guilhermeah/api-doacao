package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.DoadorRequestDTO;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.model.Doador;
import com.doacao.apidoacao.service.DoadorService;
import com.doacao.apidoacao.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/doadores")
@Tag(name = "Doadores", description = "Gestão de doadores")
public class DoadorController {

    private final DoadorService doadorService;
    private final FileStorageService fileStorageService;

    public DoadorController(DoadorService doadorService, FileStorageService fileStorageService) {
        this.doadorService = doadorService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public List<Doador> listarTodos() {
        return doadorService.listarTodos();
    }

    @GetMapping("/{id}")
    public Doador buscarPorId(@PathVariable Long id) {
        return doadorService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Doador criar(@RequestBody @Valid DoadorRequestDTO dto) {
        return doadorService.criar(dto);
    }

    @PutMapping("/{id}")
    public Doador atualizar(@PathVariable Long id, @RequestBody @Valid DoadorRequestDTO dto) {
        return doadorService.atualizar(id, dto);
    }

    @GetMapping("/{id}/doacoes")
    @Operation(summary = "Lista todas as doações feitas por um doador")
    public List<Doacao> listarDoacoes(@PathVariable Long id) {
        return doadorService.listarDoacoes(id);
    }

    @PostMapping("/{id}/foto")
    @Operation(summary = "Faz upload da foto de perfil do doador")
    public Doador uploadFoto(@PathVariable Long id, @RequestParam("arquivo") MultipartFile arquivo) {
        String url = fileStorageService.salvarFotoDoador(id, arquivo);
        return doadorService.atualizarFoto(id, url);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        doadorService.deletar(id);
    }
}