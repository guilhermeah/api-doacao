package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.LoginResponseDTO;
import com.doacao.apidoacao.dto.UsuarioRequestDTO;
import com.doacao.apidoacao.exception.BusinessException;
import com.doacao.apidoacao.model.Usuario;
import com.doacao.apidoacao.repository.OngRepository;
import com.doacao.apidoacao.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final OngRepository ongRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, OngRepository ongRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.ongRepository = ongRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<LoginResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(u -> {
                    Long idOng = ongRepository.findByUsuario(u).map(o -> o.getIdOng()).orElse(null);
                    return new LoginResponseDTO(u.getIdUsuario(), null, u.getNome(), u.getEmail(), u.getPerfil(), idOng);
                })
                .toList();
    }

    public LoginResponseDTO criar(UsuarioRequestDTO dto) {
        usuarioRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new BusinessException("Já existe um usuário com esse e-mail");
        });

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setPerfil(dto.getPerfil());
        usuario.setAtivo(true);

        usuario = usuarioRepository.save(usuario);
        return new LoginResponseDTO(usuario.getIdUsuario(), null, usuario.getNome(), usuario.getEmail(), usuario.getPerfil(), null);
    }

    public void alterarSenha(Long id, String novaSenha) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    public void ativarDesativar(Long id, boolean ativo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
        usuario.setAtivo(ativo);
        usuarioRepository.save(usuario);
    }
}
