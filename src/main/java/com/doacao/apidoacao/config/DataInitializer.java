package com.doacao.apidoacao.config;

import com.doacao.apidoacao.model.Usuario;
import com.doacao.apidoacao.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        long total = usuarioRepository.count();

        if (total == 0) {
            System.out.println("[DataInitializer] Nenhum usuário encontrado. Criando usuários padrão...");
            usuarioRepository.saveAll(List.of(
                    build("Admin Sistema", "admin@doacao.com",  "123456", "admin"),
                    build("João Silva",    "joao@doacao.com",   "123456", "funcionario"),
                    build("Maria Souza",   "maria@doacao.com",  "123456", "voluntario"),
                    build("Pedro Lima",    "pedro@doacao.com",  "123456", "funcionario"),
                    build("Ana Costa",     "ana@doacao.com",    "123456", "voluntario")
            ));
            System.out.println("[DataInitializer] Usuários padrão criados. Senha de todos: 123456");
        } else {
            System.out.println("[DataInitializer] " + total + " usuário(s) já existem no banco.");
        }
    }

    private Usuario build(String nome, String email, String senha, String perfil) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha(passwordEncoder.encode(senha));
        u.setPerfil(perfil);
        u.setAtivo(true);
        return u;
    }
}
