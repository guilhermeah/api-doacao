package com.doacao.apidoacao.dto;

public class LoginResponseDTO {

    private Long idUsuario;
    private Long idDoador;
    private String nome;
    private String email;
    private String perfil;
    private Long idOng;

    public LoginResponseDTO(Long idUsuario, Long idDoador, String nome, String email, String perfil, Long idOng) {
        this.idUsuario = idUsuario;
        this.idDoador = idDoador;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
        this.idOng = idOng;
    }

    public Long getIdUsuario() { return idUsuario; }
    public Long getIdDoador() { return idDoador; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getPerfil() { return perfil; }
    public Long getIdOng() { return idOng; }
}
