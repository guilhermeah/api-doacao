package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.OngRequestDTO;
import com.doacao.apidoacao.model.Ong;
import com.doacao.apidoacao.service.OngService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ongs")
public class OngController {

    private final OngService ongService;

    public OngController(OngService ongService) {
        this.ongService = ongService;
    }

    @GetMapping
    public List<Ong> listarTodas() {
        return ongService.listarTodas();
    }

    @GetMapping("/{id}")
    public Ong buscarPorId(@PathVariable Long id) {
        return ongService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ong criar(@RequestBody @Valid OngRequestDTO dto) {
        return ongService.criar(dto);
    }

    @PutMapping("/{id}")
    public Ong atualizar(@PathVariable Long id, @RequestBody @Valid OngRequestDTO dto) {
        return ongService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        ongService.deletar(id);
    }
}