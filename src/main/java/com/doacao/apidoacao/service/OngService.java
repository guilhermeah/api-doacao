package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.OngDashboardDTO;
import com.doacao.apidoacao.dto.OngRequestDTO;
import com.doacao.apidoacao.exception.BusinessException;
import com.doacao.apidoacao.exception.ResourceNotFoundException;
import com.doacao.apidoacao.model.Ong;
import com.doacao.apidoacao.repository.CampanhaRepository;
import com.doacao.apidoacao.repository.DoacaoRepository;
import com.doacao.apidoacao.repository.OngRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OngService {

    private final OngRepository ongRepository;
    private final CampanhaRepository campanhaRepository;
    private final DoacaoRepository doacaoRepository;

    public OngService(OngRepository ongRepository, CampanhaRepository campanhaRepository, DoacaoRepository doacaoRepository) {
        this.ongRepository = ongRepository;
        this.campanhaRepository = campanhaRepository;
        this.doacaoRepository = doacaoRepository;
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

    public OngDashboardDTO dashboard(Long id) {
        Ong ong = buscarPorId(id);

        long totalCampanhas = campanhaRepository.findByOng(ong).size();
        long campanhasAtivas = campanhaRepository.findByOng(ong).stream()
                .filter(c -> "ativa".equalsIgnoreCase(c.getStatus()))
                .count();
        long totalDoacoes = doacaoRepository.countByOng(ong);
        var valorArrecadado = doacaoRepository.somarValorArrecadadoPorOng(ong);

        String nomeOng = ong.getNomeFantasia() != null ? ong.getNomeFantasia() : ong.getRazaoSocial();
        return new OngDashboardDTO(ong.getIdOng(), nomeOng, totalCampanhas, campanhasAtivas, totalDoacoes, valorArrecadado);
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