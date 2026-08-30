package com.doacao.apidoacao.service;

import com.doacao.apidoacao.dto.DoadorRequestDTO;
import com.doacao.apidoacao.exception.BusinessException;
import com.doacao.apidoacao.exception.ResourceNotFoundException;
import com.doacao.apidoacao.model.Doacao;
import com.doacao.apidoacao.model.Doador;
import com.doacao.apidoacao.model.Usuario;
import com.doacao.apidoacao.repository.DoacaoRepository;
import com.doacao.apidoacao.repository.DoadorRepository;
import com.doacao.apidoacao.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoadorService {

    private final DoadorRepository doadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final DoacaoRepository doacaoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DoadorService(DoadorRepository doadorRepository, UsuarioRepository usuarioRepository,
                         DoacaoRepository doacaoRepository, BCryptPasswordEncoder passwordEncoder) {
        this.doadorRepository = doadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.doacaoRepository = doacaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Doador> listarTodos() {
        return doadorRepository.findAll();
    }

    public Doador buscarPorId(Long id) {
        return doadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doador não encontrado"));
    }

    @Transactional
    public Doador criar(DoadorRequestDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new BusinessException("O e-mail é obrigatório para cadastro de doador");
        }
        if (dto.getSenha() == null || dto.getSenha().isBlank()) {
            throw new BusinessException("A senha é obrigatória para cadastro de doador");
        }

        usuarioRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new BusinessException("Já existe um usuário com esse e-mail");
        });

        if (dto.getCpfCnpj() != null && !dto.getCpfCnpj().isBlank()) {
            doadorRepository.findByCpfCnpj(dto.getCpfCnpj()).ifPresent(d -> {
                throw new BusinessException("Já existe um doador com esse CPF/CNPJ");
            });
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setPerfil("doador");
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);

        Doador doador = new Doador();
        doador.setUsuario(usuario);
        mapear(dto, doador);

        return doadorRepository.save(doador);
    }

    @Transactional
    public Doador atualizar(Long id, DoadorRequestDTO dto) {
        Doador doador = buscarPorId(id);

        if (dto.getCpfCnpj() != null && !dto.getCpfCnpj().isBlank()) {
            doadorRepository.findByCpfCnpj(dto.getCpfCnpj()).ifPresent(existente -> {
                if (!existente.getIdDoador().equals(id)) {
                    throw new BusinessException("Já existe outro doador com esse CPF/CNPJ");
                }
            });
        }

        Usuario usuario = doador.getUsuario();
        if (usuario != null) {
            if (dto.getNome() != null && !dto.getNome().isBlank()) {
                usuario.setNome(dto.getNome());
            }
            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                usuario.setEmail(dto.getEmail());
            }
            if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
                usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
            }
            usuarioRepository.save(usuario);
        }

        mapear(dto, doador);
        return doadorRepository.save(doador);
    }

    public Doador atualizarFoto(Long id, String fotoUrl) {
        Doador doador = buscarPorId(id);
        doador.setFotoUrl(fotoUrl);
        return doadorRepository.save(doador);
    }

    public List<Doacao> listarDoacoes(Long idDoador) {
        buscarPorId(idDoador);
        return doacaoRepository.findByDoadorIdDoador(idDoador);
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
