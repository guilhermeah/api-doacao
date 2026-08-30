package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.DashboardResumoDTO;
import com.doacao.apidoacao.repository.CampanhaRepository;
import com.doacao.apidoacao.repository.DoacaoRepository;
import com.doacao.apidoacao.repository.OngRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final OngRepository ongRepository;
    private final CampanhaRepository campanhaRepository;
    private final DoacaoRepository doacaoRepository;

    public DashboardService(
            OngRepository ongRepository,
            CampanhaRepository campanhaRepository,
            DoacaoRepository doacaoRepository
    ) {
        this.ongRepository = ongRepository;
        this.campanhaRepository = campanhaRepository;
        this.doacaoRepository = doacaoRepository;
    }

    public DashboardResumoDTO resumo() {
        return new DashboardResumoDTO(
                ongRepository.count(),
                doacaoRepository.contarDonadoresDistintos(),
                campanhaRepository.count(),
                campanhaRepository.countByStatus("ativa"),
                doacaoRepository.count(),
                doacaoRepository.somarValorArrecadado()
        );
    }
}
