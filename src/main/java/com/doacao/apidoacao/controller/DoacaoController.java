package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.DoacaoRequestDTO;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.service.DoacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Doacao criar(@RequestBody @Valid DoacaoRequestDTO dto) {
        return doacaoService.criar(dto);
    }

    @PutMapping("/{id}")
    public Doacao atualizar(@PathVariable Long id, @RequestBody @Valid DoacaoRequestDTO dto) {
        return doacaoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        doacaoService.deletar(id);
    }
}