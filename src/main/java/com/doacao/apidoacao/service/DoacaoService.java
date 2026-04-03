package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.DoacaoRequestDTO;
import com.doacao.apidoacao.exception.BusinessException;
import com.doacao.apidoacao.exception.ResourceNotFoundException;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.model.Doador;
import com.doacao.apidoacao.model.Ong;
import com.doacao.apidoacao.repository.DoacaoRepository;
import com.doacao.apidoacao.repository.DoadorRepository;
import com.doacao.apidoacao.repository.OngRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoacaoService {

    private final DoacaoRepository doacaoRepository;
    private final DoadorRepository doadorRepository;
    private final OngRepository ongRepository;

    public DoacaoService(
            DoacaoRepository doacaoRepository,
            DoadorRepository doadorRepository,
            OngRepository ongRepository
    ) {
        this.doacaoRepository = doacaoRepository;
        this.doadorRepository = doadorRepository;
        this.ongRepository = ongRepository;
    }

    public List<Doacao> listarTodas() {
        return doacaoRepository.findAll();
    }

    public Doacao buscarPorId(Long id) {
        return doacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doação não encontrada"));
    }

    public Doacao criar(DoacaoRequestDTO dto) {
        Doador doador = doadorRepository.findById(dto.getIdDoador())
                .orElseThrow(() -> new ResourceNotFoundException("Doador não encontrado"));

        Ong ong = ongRepository.findById(dto.getIdOng())
                .orElseThrow(() -> new ResourceNotFoundException("ONG não encontrada"));

        validarTipoValor(dto);

        Doacao doacao = new Doacao();
        doacao.setDoador(doador);
        doacao.setOng(ong);
        doacao.setTipoDoacao(dto.getTipoDoacao());
        doacao.setDescricaoGeral(dto.getDescricaoGeral());
        doacao.setValorTotal(dto.getValorTotal());
        doacao.setStatusDoacao(dto.getStatusDoacao());
        doacao.setObservacoes(dto.getObservacoes());

        return doacaoRepository.save(doacao);
    }

    public Doacao atualizar(Long id, DoacaoRequestDTO dto) {
        Doacao doacao = buscarPorId(id);

        Doador doador = doadorRepository.findById(dto.getIdDoador())
                .orElseThrow(() -> new ResourceNotFoundException("Doador não encontrado"));

        Ong ong = ongRepository.findById(dto.getIdOng())
                .orElseThrow(() -> new ResourceNotFoundException("ONG não encontrada"));

        validarTipoValor(dto);

        doacao.setDoador(doador);
        doacao.setOng(ong);
        doacao.setTipoDoacao(dto.getTipoDoacao());
        doacao.setDescricaoGeral(dto.getDescricaoGeral());
        doacao.setValorTotal(dto.getValorTotal());
        doacao.setStatusDoacao(dto.getStatusDoacao());
        doacao.setObservacoes(dto.getObservacoes());

        return doacaoRepository.save(doacao);
    }

    public void deletar(Long id) {
        Doacao doacao = buscarPorId(id);
        doacaoRepository.delete(doacao);
    }

    private void validarTipoValor(DoacaoRequestDTO dto) {
        if ("financeira".equalsIgnoreCase(dto.getTipoDoacao())
                && (dto.getValorTotal() == null || dto.getValorTotal().signum() <= 0)) {
            throw new BusinessException("Doações financeiras precisam ter valor total maior que zero");
        }
    }
}