package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.DoadorRequestDTO;
import com.doacao.apidoacao.exception.BusinessException;
import com.doacao.apidoacao.exception.ResourceNotFoundException;
import com.doacao.apidoacao.model.Doador;
import com.doacao.apidoacao.repository.DoadorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoadorService {

    private final DoadorRepository doadorRepository;

    public DoadorService(DoadorRepository doadorRepository) {
        this.doadorRepository = doadorRepository;
    }

    public List<Doador> listarTodos() {
        return doadorRepository.findAll();
    }

    public Doador buscarPorId(Long id) {
        return doadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doador não encontrado"));
    }

    public Doador criar(DoadorRequestDTO dto) {
        if (dto.getCpfCnpj() != null && !dto.getCpfCnpj().isBlank()) {
            doadorRepository.findByCpfCnpj(dto.getCpfCnpj())
                    .ifPresent(d -> {
                        throw new BusinessException("Já existe um doador com esse CPF/CNPJ");
                    });
        }

        Doador doador = new Doador();
        mapear(dto, doador);

        return doadorRepository.save(doador);
    }

    public Doador atualizar(Long id, DoadorRequestDTO dto) {
        Doador doador = buscarPorId(id);

        if (dto.getCpfCnpj() != null && !dto.getCpfCnpj().isBlank()) {
            doadorRepository.findByCpfCnpj(dto.getCpfCnpj())
                    .ifPresent(existente -> {
                        if (!existente.getIdDoador().equals(id)) {
                            throw new BusinessException("Já existe outro doador com esse CPF/CNPJ");
                        }
                    });
        }

        mapear(dto, doador);
        return doadorRepository.save(doador);
    }

    public void deletar(Long id) {
        Doador doador = buscarPorId(id);
        doadorRepository.delete(doador);
    }

    private void mapear(DoadorRequestDTO dto, Doador doador) {
        doador.setTipoDoador(dto.getTipoDoador());
        doador.setNome(dto.getNome());
        doador.setCpfCnpj(dto.getCpfCnpj());
        doador.setEmail(dto.getEmail());
        doador.setTelefone(dto.getTelefone());
        doador.setEndereco(dto.getEndereco());
        doador.setNumero(dto.getNumero());
        doador.setComplemento(dto.getComplemento());
        doador.setBairro(dto.getBairro());
        doador.setCidade(dto.getCidade());
        doador.setEstado(dto.getEstado());
        doador.setCep(dto.getCep());
    }
}