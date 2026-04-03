package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.OngRequestDTO;
import com.doacao.apidoacao.exception.BusinessException;
import com.doacao.apidoacao.exception.ResourceNotFoundException;
import com.doacao.apidoacao.model.Ong;
import com.doacao.apidoacao.repository.OngRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OngService {

    private final OngRepository ongRepository;

    public OngService(OngRepository ongRepository) {
        this.ongRepository = ongRepository;
    }

    public List<Ong> listarTodas() {
        return ongRepository.findAll();
    }

    public Ong buscarPorId(Long id) {
        return ongRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ONG não encontrada"));
    }

    public Ong criar(OngRequestDTO dto) {
        if (dto.getCnpj() != null && !dto.getCnpj().isBlank()) {
            ongRepository.findByCnpj(dto.getCnpj())
                    .ifPresent(o -> {
                        throw new BusinessException("Já existe uma ONG com esse CNPJ");
                    });
        }

        Ong ong = new Ong();
        mapear(dto, ong);
        return ongRepository.save(ong);
    }

    public Ong atualizar(Long id, OngRequestDTO dto) {
        Ong ong = buscarPorId(id);

        if (dto.getCnpj() != null && !dto.getCnpj().isBlank()) {
            ongRepository.findByCnpj(dto.getCnpj())
                    .ifPresent(existente -> {
                        if (!existente.getIdOng().equals(id)) {
                            throw new BusinessException("Já existe outra ONG com esse CNPJ");
                        }
                    });
        }

        mapear(dto, ong);
        return ongRepository.save(ong);
    }

    public void deletar(Long id) {
        Ong ong = buscarPorId(id);
        ongRepository.delete(ong);
    }

    private void mapear(OngRequestDTO dto, Ong ong) {
        ong.setRazaoSocial(dto.getRazaoSocial());
        ong.setNomeFantasia(dto.getNomeFantasia());
        ong.setCnpj(dto.getCnpj());
        ong.setEmail(dto.getEmail());
        ong.setTelefone(dto.getTelefone());
        ong.setResponsavel(dto.getResponsavel());
        ong.setEndereco(dto.getEndereco());
        ong.setNumero(dto.getNumero());
        ong.setComplemento(dto.getComplemento());
        ong.setBairro(dto.getBairro());
        ong.setCidade(dto.getCidade());
        ong.setEstado(dto.getEstado());
        ong.setCep(dto.getCep());
        ong.setAreaAtuacao(dto.getAreaAtuacao());
        ong.setStatusOng(dto.getStatusOng());
    }
}