package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.DoadorRequestDTO;
import com.doacao.apidoacao.model.Doador;
import com.doacao.apidoacao.service.DoadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doadores")
public class DoadorController {

    private final DoadorService doadorService;

    public DoadorController(DoadorService doadorService) {
        this.doadorService = doadorService;
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        doadorService.deletar(id);
    }
}