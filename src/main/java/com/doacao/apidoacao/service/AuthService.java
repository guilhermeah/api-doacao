package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.LoginRequestDTO;
import com.doacao.apidoacao.dto.LoginResponseDTO;
import com.doacao.apidoacao.exception.BusinessException;
import com.doacao.apidoacao.model.Doador;
import com.doacao.apidoacao.model.Ong;
import com.doacao.apidoacao.model.Usuario;
import com.doacao.apidoacao.repository.DoadorRepository;
import com.doacao.apidoacao.repository.OngRepository;
import com.doacao.apidoacao.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final OngRepository ongRepository;
    private final DoadorRepository doadorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, OngRepository ongRepository,
                       DoadorRepository doadorRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.ongRepository = ongRepository;
        this.doadorRepository = doadorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas"));

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            throw new BusinessException("Usuário inativo");
        }

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new BusinessException("Credenciais inválidas");
        }

        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        Long idOng = null;
        Long idDoador = null;

        if ("ong".equalsIgnoreCase(usuario.getPerfil())) {
            idOng = ongRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                    .map(Ong::getIdOng)
                    .orElse(null);
        } else if ("doador".equalsIgnoreCase(usuario.getPerfil())) {
            idDoador = doadorRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                    .map(Doador::getIdDoador)
                    .orElse(null);
        }

        return new LoginResponseDTO(usuario.getIdUsuario(), idDoador, usuario.getNome(),
                usuario.getEmail(), usuario.getPerfil(), idOng);
    }
}
