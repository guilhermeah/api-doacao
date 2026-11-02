package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.OngDashboardDTO;
import com.doacao.apidoacao.dto.OngRequestDTO;
import com.doacao.apidoacao.exception.BusinessException;
import com.doacao.apidoacao.exception.ResourceNotFoundException;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.model.Ong;
import com.doacao.apidoacao.model.Usuario;
import com.doacao.apidoacao.repository.CampanhaRepository;
import com.doacao.apidoacao.repository.DoacaoRepository;
import com.doacao.apidoacao.repository.OngRepository;
import com.doacao.apidoacao.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OngService {

    private final OngRepository ongRepository;
    private final CampanhaRepository campanhaRepository;
    private final DoacaoRepository doacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public OngService(OngRepository ongRepository, CampanhaRepository campanhaRepository,
                      DoacaoRepository doacaoRepository, UsuarioRepository usuarioRepository) {
        this.ongRepository = ongRepository;
        this.campanhaRepository = campanhaRepository;
        this.doacaoRepository = doacaoRepository;
        this.usuarioRepository = usuarioRepository;
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
        if (dto.getIdUsuario() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
            ong.setUsuario(usuario);
        }
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

    public Ong atualizarFoto(Long id, String logoUrl) {
        Ong ong = buscarPorId(id);
        ong.setLogoUrl(logoUrl);
        return ongRepository.save(ong);
    }

    public void deletar(Long id) {
        Ong ong = buscarPorId(id);
        ongRepository.delete(ong);
    }

    public List<Doacao> listarDoacoes(Long id) {
        Ong ong = buscarPorId(id);
        return doacaoRepository.findByOng(ong);
    }

    public OngDashboardDTO dashboard(Long id) {
        Ong ong = buscarPorId(id);

        List<com.doacao.apidoacao.model.Campanha> campanhas = campanhaRepository.findByOng(ong);
        long totalCampanhas = campanhas.size();
        long campanhasAtivas = campanhas.stream()
                .filter(c -> "ativa".equalsIgnoreCase(c.getStatus()))
                .count();
        long totalDoacoes = doacaoRepository.countByOng(ong);

        // Soma direto da tabela de doações, e não de campanhas.valor_arrecadado.
        // Os dois deveriam bater (CampanhaService.recalcularArrecadacao mantém a
        // campanha alinhada), mas somar as doações também cobre a doação que não
        // está ligada a nenhuma campanha — que não aparece em campanha nenhuma e
        // fazia este card divergir da tela de "Doações Recebidas", que sempre
        // somou as doações.
        java.math.BigDecimal valorArrecadado = doacaoRepository.somarValorArrecadadoPorOng(ong);
        if (valorArrecadado == null) valorArrecadado = java.math.BigDecimal.ZERO;

        String nomeOng = ong.getNomeFantasia() != null ? ong.getNomeFantasia() : ong.getRazaoSocial();
        return new OngDashboardDTO(ong.getIdOng(), nomeOng, totalCampanhas, campanhasAtivas, totalDoacoes, valorArrecadado);
    }

    /**
     * Atualizacao parcial: campo que nao veio no payload (null) mantem o valor
     * que ja estava salvo. Antes, um PUT com poucos campos zerava CNPJ, e-mail,
     * endereco e cidade da ONG.
     */
    private void mapear(OngRequestDTO dto, Ong ong) {
        if (dto.getRazaoSocial() != null)  ong.setRazaoSocial(dto.getRazaoSocial());
        if (dto.getNomeFantasia() != null) ong.setNomeFantasia(dto.getNomeFantasia());
        if (dto.getCnpj() != null)         ong.setCnpj(dto.getCnpj());
        if (dto.getEmail() != null)        ong.setEmail(dto.getEmail());
        if (dto.getTelefone() != null)     ong.setTelefone(dto.getTelefone());
        if (dto.getResponsavel() != null)  ong.setResponsavel(dto.getResponsavel());
        if (dto.getEndereco() != null)     ong.setEndereco(dto.getEndereco());
        if (dto.getNumero() != null)       ong.setNumero(dto.getNumero());
        if (dto.getComplemento() != null)  ong.setComplemento(dto.getComplemento());
        if (dto.getBairro() != null)       ong.setBairro(dto.getBairro());
        if (dto.getCidade() != null)       ong.setCidade(dto.getCidade());
        if (dto.getEstado() != null)       ong.setEstado(dto.getEstado());
        if (dto.getCep() != null)          ong.setCep(dto.getCep());
        if (dto.getAreaAtuacao() != null)  ong.setAreaAtuacao(dto.getAreaAtuacao());
        if (dto.getStatusOng() != null)    ong.setStatusOng(dto.getStatusOng());
        if (dto.getChavePix() != null)     ong.setChavePix(dto.getChavePix().trim());
    }
}
