package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.DoacaoRequestDTO;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.service.DoacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doacoes")
public class DoacaoController {

    private final DoacaoService doacaoService;

    public DoacaoController(DoacaoService doacaoService) {
        this.doacaoService = doacaoService;
    }

    @GetMapping
    public List<Doacao> listarTodas() {
        return doacaoService.listarTodas();
    }

    @GetMapping("/{id}")
    public Doacao buscarPorId(@PathVariable Long id) {
        return doacaoService.buscarPorId(id);
    }

    @GetMapping("/ong/{idOng}")
    public List<Doacao> listarPorOng(@PathVariable Long idOng) {
        return doacaoService.listarPorOng(idOng);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Doacao criar(@RequestBody @Valid DoacaoRequestDTO dto) {
        return doacaoService.criar(dto);
    }

    @PutMapping("/{id}")
    public Doacao atualizar(@PathVariable Long id, @RequestBody @Valid DoacaoRequestDTO dto) {
        return doacaoService.atualizar(id, dto);
    }

    @PostMapping("/{id}/fotos")
    public Doacao adicionarFotos(
            @PathVariable Long id,
            @RequestParam("fotos") List<MultipartFile> fotos) {
        return doacaoService.adicionarFotos(id, fotos);
    }

    @PatchMapping("/{id}/status")
    public Doacao atualizarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return doacaoService.atualizarStatus(id, body.get("status"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        doacaoService.deletar(id);
    }
}