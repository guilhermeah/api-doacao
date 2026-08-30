package com.doacao.apidoacao.repository;

import com.doacao.apidoacao.model.Ong;
import com.doacao.apidoacao.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OngRepository extends JpaRepository<Ong, Long> {
    Optional<Ong> findByCnpj(String cnpj);
    Optional<Ong> findByUsuario(Usuario usuario);
    Optional<Ong> findByUsuarioIdUsuario(Long idUsuario);
}